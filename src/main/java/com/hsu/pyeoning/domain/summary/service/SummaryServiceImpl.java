package com.hsu.pyeoning.domain.summary.service;

import com.hsu.pyeoning.domain.summary.entity.Summary;
import com.hsu.pyeoning.domain.summary.repository.SummaryRepository;
import com.hsu.pyeoning.global.response.CustomApiResponse;
import com.hsu.pyeoning.global.security.jwt.util.AuthenticationUserUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;


@Service
@RequiredArgsConstructor
public class SummaryServiceImpl implements SummaryService {

    private final SummaryRepository summaryRepository;
    private final AuthenticationUserUtils authenticationUserUtils;

    public ResponseEntity<CustomApiResponse<?>> getPatientSummary(Long patientId) {
        String currentUserId = authenticationUserUtils.getCurrentUserId();

        // 토큰이 유효하고 인증된 사용자에 해당하는지 확인
        if (currentUserId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(CustomApiResponse.createFailWithout(401, "유효하지 않은 토큰이거나, 해당 ID에 해당하는 환자가 존재하지 않습니다."));
        }

        // 지정된 환자의 요약보고서 조회
        Summary summary = summaryRepository.findByPatient_PatientId(patientId).orElse(null);

        if (summary == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(CustomApiResponse.createFailWithout(404, "요약보고서 조회에 실패했습니다."));
        }

        // 응답 데이터 생성
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("summaryId", summary.getSummaryId());
        data.put("summaryContent", summary.getSummaryContent());
        data.put("createdAt", summary.getCreatedAt());

        return ResponseEntity.ok(CustomApiResponse.createSuccess(200, data, "요약보고서 조회에 성공했습니다."));
    }

}
