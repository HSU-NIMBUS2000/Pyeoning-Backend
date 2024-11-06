package com.hsu.pyeoning.domain.summary.service;

import com.hsu.pyeoning.domain.chat.entity.Chat;
import com.hsu.pyeoning.domain.chat.repository.ChatRepository;
import com.hsu.pyeoning.domain.patient.entity.Patient;
import com.hsu.pyeoning.domain.patient.repository.PatientRepository;
import com.hsu.pyeoning.domain.summary.entity.Summary;
import com.hsu.pyeoning.domain.summary.repository.SummaryRepository;
import com.hsu.pyeoning.domain.summary.web.dto.ChatSummaryResponseDto;
import com.hsu.pyeoning.domain.summary.web.dto.SummaryDto;
import com.hsu.pyeoning.global.response.CustomApiResponse;
import com.hsu.pyeoning.global.security.jwt.util.AuthenticationUserUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;


@Service
@RequiredArgsConstructor
public class SummaryServiceImpl implements SummaryService {

    private final SummaryRepository summaryRepository;
    private final AuthenticationUserUtils authenticationUserUtils;
    private final PatientRepository patientRepository;
    private final ChatRepository chatRepository;

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
                .orElseThrow(() -> new RuntimeException("유효하지 않은 토큰이거나, 해당 ID에 해당하는 환자가 존재하지 않습니다."));

        // 400 : 병명 필요
        String disease = patient.getPyeoningDisease();
        if (disease == null || disease.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(CustomApiResponse.createFailWithout(400, "정확한 요약을 위해 병명이 필요합니다."));
        }

        // 환자의 해당 세션동안 대화한 채팅 기록 가져오기
        List<Chat> chatHistory = chatRepository.findChatHistoryBetweenSessions(patient);

        // 400 : 충분한 대화 내용 필요
        if (chatHistory.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(CustomApiResponse.createFailWithout(400, "정확한 분석을 위해 충분한 양의 대화 내용이 필요합니다."));
        }

        // FastAPI 통신 부분은 나중에 추가

        // 요약 보고서 예제 데이터 생성 (임시로 설정)
        String summaryContent = "환자는 현재 불안 증세를 호소하고 있으며, 상담을 통해 상태를 점검할 필요가 있습니다.";

        // 요약 보고서 save

        // 요약 보고서 데이터 가공

        // 200 : 요약 보고서 생성 성공
        return ResponseEntity.ok(CustomApiResponse.createSuccess(200, data, "요약 보고서가 성공적으로 생성되었습니다."));
    }

}
