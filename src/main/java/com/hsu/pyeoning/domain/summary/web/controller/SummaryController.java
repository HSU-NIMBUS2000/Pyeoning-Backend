package com.hsu.pyeoning.domain.summary.web.controller;

import com.hsu.pyeoning.domain.doctor.service.DoctorServiceImpl;
import com.hsu.pyeoning.domain.patient.entity.Patient;
import com.hsu.pyeoning.domain.summary.service.SummaryServiceImpl;
import com.hsu.pyeoning.global.response.CustomApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/summary")
@RequiredArgsConstructor
public class SummaryController {

    private final SummaryServiceImpl summaryService;

    @GetMapping("/patientSummary/{patientId}")
    public ResponseEntity<CustomApiResponse<?>> getPatientSummary(@PathVariable Long patientId) {
        return summaryService.getPatientSummary(patientId);
    }

    @PostMapping("/create")
    public ResponseEntity<CustomApiResponse<?>> makePatientChatSummary() {
        return summaryService.makePatientSummary();
    }

}
