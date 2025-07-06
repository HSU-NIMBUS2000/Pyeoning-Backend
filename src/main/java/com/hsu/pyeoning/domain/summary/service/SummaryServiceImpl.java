package com.hsu.pyeoning.domain.summary.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hsu.pyeoning.domain.chat.entity.Chat;
import com.hsu.pyeoning.domain.chat.repository.ChatRepository;
import com.hsu.pyeoning.domain.coolsms.service.CoolsmsService;
import com.hsu.pyeoning.domain.doctor.entity.Doctor;
import com.hsu.pyeoning.domain.doctor.repository.DoctorRepository;
import com.hsu.pyeoning.domain.patient.entity.Patient;
import com.hsu.pyeoning.domain.patient.repository.PatientRepository;
import com.hsu.pyeoning.domain.risk.entity.RiskLevel;
import com.hsu.pyeoning.domain.risk.repository.RiskLevelRepository;
import com.hsu.pyeoning.domain.summary.entity.Summary;
import com.hsu.pyeoning.domain.summary.repository.SummaryRepository;
import com.hsu.pyeoning.domain.summary.web.dto.ChatSummaryFastApiRequestDto;
import com.hsu.pyeoning.domain.summary.web.dto.ChatSummaryFastApiResponseDto;
import com.hsu.pyeoning.domain.summary.web.dto.ChatSummaryResponseDto;
import com.hsu.pyeoning.domain.summary.web.dto.SummaryDto;
import com.hsu.pyeoning.global.exception.UnauthorizedException;
import com.hsu.pyeoning.global.response.CustomApiResponse;
import com.hsu.pyeoning.global.security.jwt.util.AuthenticationUserUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class SummaryServiceImpl implements SummaryService {
    @Value("${fastapi.url}")
    private String fastApiUrl;

    private final SummaryRepository summaryRepository;
    private final AuthenticationUserUtils authenticationUserUtils;
    private final PatientRepository patientRepository;
    private final ChatRepository chatRepository;
    private final RestTemplate restTemplate;
    private final RiskLevelRepository riskLevelRepository;
    private final DoctorRepository doctorRepository;
    private final CoolsmsService coolsmsService;

    @Override
    public ResponseEntity<CustomApiResponse<?>> getPatientSummary(Long patientId) {
        String doctorId = authenticationUserUtils.getCurrentUserId();

        // 401 : 유효하지 않은 의사 Id
        if (doctorId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(CustomApiResponse.createFailWithout(401, "유효하지 않은 토큰이거나, 해당 ID에 해당하는 의사가 존재하지 않습니다."));
        }

        // 404 : 존재하지 않는 환자
        Optional<Patient> foundPatient = patientRepository.findById(patientId);
        if (foundPatient.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(CustomApiResponse.createFailWithout(404, "해당 ID에 해당하는 환자가 존재하지 않습니다."));
        }
        Patient patient = foundPatient.get();

        // patientId로 모든 summary 조회
        List<Summary> summaries = summaryRepository.findAllByPatient(patient);

        // response
        List<SummaryDto> dtos = new ArrayList<>();
        for (Summary summary : summaries) {
            SummaryDto dto = new SummaryDto(
                    summary.getSummaryId(),
                    summary.getSummaryContent(),
                    summary.localDateToString()
            );
            dtos.add(dto);
        }

        // 200 : 요약 보고서 조회 성공
        return ResponseEntity.ok(CustomApiResponse.createSuccess(200, dtos, "요약보고서 조회에 성공했습니다."));
    }

    // 요약 보고서 생성
    @Override
    @Transactional
    public ResponseEntity<CustomApiResponse<?>> makePatientSummary() {
        String currentUserId = authenticationUserUtils.getCurrentUserId();

        // 401 : 환자 정보 찾을 수 없음
        Patient patient = patientRepository.findByPatientCode(currentUserId)
                .orElseThrow(() -> new UnauthorizedException("유효하지 않은 토큰이거나, 해당 ID에 해당하는 환자가 존재하지 않습니다."));

        // 400 : 병명 필요
        String disease = patient.getPyeoningDisease();
        if (disease == null || disease.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(CustomApiResponse.createFailWithout(400, "정확한 요약을 위해 병명이 필요합니다."));
        }

        // 환자의 해당 세션동안 대화한 채팅 기록 가져오기
        List<Chat> chatHistory = chatRepository.findChatHistoryBetweenSessions(patient.getPatientId());

        // 409 : 세션 종료 이후 요약보고서 생성 가능
        Boolean isSessionEnded = chatRepository.isLatestChatSessionEnded(patient.getPatientId());
        if (!isSessionEnded) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(CustomApiResponse.createFailWithout(409, "환자 채팅의 세션 종료 이후 요약보고서 생성이 가능합니다."));
        }

        // FastAPI - 요청 DTO 빌드
        ChatSummaryFastApiRequestDto summaryRequestDto = ChatSummaryFastApiRequestDto.builder()
                .disease(disease)
                .chats(chatHistory)
                .build();

        // 요약 API 호출
        String fastApiSummaryEndpoint = fastApiUrl + "/api/doctor-ai/analyze";
        ChatSummaryFastApiResponseDto summaryResponseDto;

        try {
            summaryResponseDto = sendPostRequest(fastApiSummaryEndpoint, summaryRequestDto, ChatSummaryFastApiResponseDto.class);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                    .body(CustomApiResponse.createFailWithout(502, e.getMessage()));
        }

        // 400 : 충분한 대화 내용 필요
        if (summaryResponseDto.getData().getSummary().isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(CustomApiResponse.createFailWithout(400, "정확한 분석을 위해 충분한 양의 대화 내용이 필요합니다."));
        }

        int risk_level = summaryResponseDto.getData().getRiskLevel();
        String description = summaryResponseDto.getData().getRiskReason();

        // 위험도가 5(최고 레벨)인 경우 담당 의사에게 문자 발송
        if (risk_level == 5) {
            // 의사 전화번호에서 하이픈 지우기
            String doctorPhone = patient.getDoctorId().getDoctorPhone().replace("-", "");
            // 담당 의사에게 문자 발송
            coolsmsService.sendSms(doctorPhone, patient.getPatientName(), patient.getPyeoningDisease(), description);
        }

        // RiskLevel save
        RiskLevel riskLevel = RiskLevel.builder()
                .patient(patient)
                .riskLevel(risk_level)
                .description(description)
                .build();
        riskLevelRepository.save(riskLevel);

        // Summary save
        Summary summary = Summary.builder()
                .patient(patient)
                .summaryContent(summaryResponseDto.getData().getSummary())
                .build();
        summaryRepository.save(summary);

        // response 데이터 가공
        ChatSummaryResponseDto data = ChatSummaryResponseDto.builder()
                .summaryId(summary.getSummaryId())
                .summaryContent(summary.getSummaryContent())
                .createdAt(summary.localDateToString()) // ex. 2024.11.06
//                .riskLevel(risk_level)
//                .description(description)
                .build();

        // 200 : 요약 보고서 생성 성공
        return ResponseEntity.ok(CustomApiResponse.createSuccess(200, data, "요약 보고서가 성공적으로 생성되었습니다."));
    }

    // fastAPI 통신 요청 처리 메서드
    private <T> T sendPostRequest(String url, Object request, Class<T> responseType) {
        try {
            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                // JSON 파싱
                ObjectMapper objectMapper = new ObjectMapper();
                return objectMapper.readValue(response.getBody(), responseType);
            } else {
                throw new HttpClientErrorException(response.getStatusCode(), "응답 상태 코드가 성공 범위가 아닙니다.");
            }
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            throw new RuntimeException("AI 서버와의 통신 중 오류 발생: " + e.getMessage(), e);
        } catch (ResourceAccessException e) {
            throw new RuntimeException("AI 서버 응답 시간이 초과되었습니다.", e);
        } catch (Exception e) {
            throw new RuntimeException("응답 파싱 중 오류가 발생했습니다.", e);
        }
    }


}

