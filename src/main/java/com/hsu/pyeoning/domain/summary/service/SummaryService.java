package com.hsu.pyeoning.domain.summary.service;

import com.hsu.pyeoning.domain.patient.entity.Patient;
import com.hsu.pyeoning.global.response.CustomApiResponse;
import org.springframework.http.ResponseEntity;

public interface SummaryService {
    ResponseEntity<CustomApiResponse<?>> getPatientSummary(Long patientId);
}
