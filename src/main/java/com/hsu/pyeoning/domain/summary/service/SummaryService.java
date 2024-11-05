package com.hsu.pyeoning.domain.summary.service;

import com.hsu.pyeoning.domain.patient.entity.Patient;
import com.hsu.pyeoning.global.response.CustomApiResponse;
import org.springframework.http.ResponseEntity;

public interface SummaryService {
    ResponseEntity<CustomApiResponse<?>> getPatientSummary(Long patientId);
    // 요약 보고서 생성
    ResponseEntity<CustomApiResponse<?>> makePatientSummary(Long patientId);
}
