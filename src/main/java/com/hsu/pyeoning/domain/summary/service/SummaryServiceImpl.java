package com.hsu.pyeoning.domain.summary.service;

import com.hsu.pyeoning.domain.summary.entity.Summary;
import com.hsu.pyeoning.domain.summary.repository.SummaryRepository;
import com.hsu.pyeoning.domain.summary.web.dto.SummaryDto;
import com.hsu.pyeoning.global.response.CustomApiResponse;
import com.hsu.pyeoning.global.security.jwt.util.AuthenticationUserUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;


@Service
@RequiredArgsConstructor
public class SummaryServiceImpl implements SummaryService {

    private final SummaryRepository summaryRepository;
    private final AuthenticationUserUtils authenticationUserUtils;

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

}
