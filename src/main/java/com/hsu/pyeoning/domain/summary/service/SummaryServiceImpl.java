package com.hsu.pyeoning.domain.summary.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hsu.pyeoning.domain.chat.entity.Chat;
import com.hsu.pyeoning.domain.chat.repository.ChatRepository;
import com.hsu.pyeoning.domain.patient.entity.Patient;
import com.hsu.pyeoning.domain.patient.repository.PatientRepository;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;


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

    @Override
    public ResponseEntity<CustomApiResponse<?>> getPatientSummary(Long patientId) {
        String currentUserId = authenticationUserUtils.getCurrentUserId();

        // 유효한 사용자 확인
        if (currentUserId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(CustomApiResponse.createFailWithout(401, "유효하지 않은 토큰이거나, 해당 ID에 해당하는 환자가 존재하지 않습니다."));
        }

        // 요약보고서 조회
        Summary summary = summaryRepository.findByPatient_PatientId(patientId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "요약보고서 조회에 실패했습니다."));

        SummaryDto dto = new SummaryDto(
                summary.getSummaryId(),
                summary.getSummaryContent(),
                summary.getCreatedAt()
        );

        return ResponseEntity.ok(CustomApiResponse.createSuccess(200, dto, "요약보고서 조회에 성공했습니다."));
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
//        System.out.println("chatHistory 출력");
//        for (Chat chat : chatHistory) {
//            System.out.println(chat.getChatId());
//        }

        // 409 : 세션 종료 이후 요약보고서 생성 가능
        Boolean isSessionEnded = chatRepository.isLatestChatSessionEnded(patient.getPatientId());
        if (!isSessionEnded) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(CustomApiResponse.createFailWithout(409, "환자 채팅의 세션 종료 이후 요약보고서 생성이 가능합니다."));
        }

        // FastAPI - 요청 DTO 빌드
        ChatSummaryFastApiRequestDto requestDto = ChatSummaryFastApiRequestDto.builder()
                .disease(disease)
                .chats(chatHistory)  // List<Chat>을 ChatSummaryFastApiRequestDto에 전달하면 자동 변환됨
                .build();

        // FastAPI - 엔드포인트 설정
        String fastApiEndpoint = fastApiUrl + "/api/doctor-ai/summarize";
        String summaryContent = null;

        try {
            // FastAPI - 서버에 POST 요청 전송
            ResponseEntity<String> response = restTemplate.postForEntity(fastApiEndpoint, requestDto, String.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                // FastAPI - 응답 JSON 파싱
                ObjectMapper objectMapper = new ObjectMapper();
                ChatSummaryFastApiResponseDto responseDto = objectMapper.readValue(response.getBody(), ChatSummaryFastApiResponseDto.class);
                summaryContent = responseDto.getData().getSummary(); // 요약 내용 가져오기
            } else {
                // 502 : 응답 상태 코드가 성공 범위가 아님
                return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                        .body(CustomApiResponse.createFailWithout(502, "AI 서버와의 통신에 실패했습니다. 응답 코드가 성공 범위가 아닙니다."));
            }

        } catch (HttpClientErrorException | HttpServerErrorException e) {
            // 502 : AI 서버와의 통신 실패
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                    .body(CustomApiResponse.createFailWithout(502, "AI 서버와의 통신 중 오류 발생: " + e.getMessage()));
        } catch (ResourceAccessException e) {
            // 504 : AI 서버 시간 초과
            return ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT)
                    .body(CustomApiResponse.createFailWithout(504, "AI 서버 응답 시간이 초과되었습니다. 잠시 후 다시 시도해 주세요."));
        } catch (Exception e) {
            // 500 : JSON 파싱 오류 발생 등 기타 예외
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.createFailWithout(500, "응답 파싱 중 오류가 발생했습니다. 잠시 후 다시 시도해 주세요."));
        }

        // 400 : 충분한 대화 내용 필요
        if (summaryContent.isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(CustomApiResponse.createFailWithout(400, "정확한 분석을 위해 충분한 양의 대화 내용이 필요합니다."));
        }

        // 요약 보고서 save
        Summary summary = Summary.builder()
                .patient(patient)
                .summaryContent(summaryContent)
                .build();
        summaryRepository.save(summary);

        // 요약 보고서 데이터 가공
        ChatSummaryResponseDto data = ChatSummaryResponseDto.builder()
                .summaryId(summary.getSummaryId())
                .summaryContent(summaryContent)
                .createdAt(summary.localDateToString()) // ex. 2024.11.06"
                .build();

        // 200 : 요약 보고서 생성 성공
        return ResponseEntity.ok(CustomApiResponse.createSuccess(200, data, "요약 보고서가 성공적으로 생성되었습니다."));
    }

}
