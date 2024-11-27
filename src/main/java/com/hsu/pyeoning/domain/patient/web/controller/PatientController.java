package com.hsu.pyeoning.domain.patient.web.controller;

import com.hsu.pyeoning.domain.patient.service.PatientService;
import com.hsu.pyeoning.domain.patient.web.dto.PatientRegisterDto;
import com.hsu.pyeoning.domain.patient.web.dto.PatientLoginDto;
import com.hsu.pyeoning.domain.patient.web.dto.ModifyPromptDto;
import com.hsu.pyeoning.global.response.CustomApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/patient")
@RequiredArgsConstructor
public class PatientController {

    private final PatientService patientService;

    @PostMapping("/registration")
    public ResponseEntity<CustomApiResponse<?>> registerPatient(@Valid @RequestBody PatientRegisterDto dto) {
        return patientService.registerPatient(dto);
    }

    @PostMapping("/login")
    public ResponseEntity<CustomApiResponse<?>> patientLogin(@Valid @RequestBody PatientLoginDto dto) {
        return patientService.patientLogin(dto);
    }

    @PutMapping("/{patientId}/modifyPrompt")
    public ResponseEntity<CustomApiResponse<?>> modifyPrompt(
            @PathVariable("patientId") Long patientId,
            @Valid @RequestBody ModifyPromptDto dto) {
        return patientService.modifyPrompt(patientId, dto);
    }

    @GetMapping("/list")
    public ResponseEntity<CustomApiResponse<?>> getPatientList(
            @RequestParam(name = "page", defaultValue = "1") @Min(1) int page,
            @RequestParam(name = "size", defaultValue = "10") @Min(1) int size,
            @RequestParam(name = "category", required = false) String category,
            @RequestParam(name = "keyword", required = false) String keyword) {
        return patientService.getPatientList(page, size, category, keyword);
    }

    @GetMapping("/{patientId}/detail")
    public ResponseEntity<CustomApiResponse<?>> getPatientDetail(@PathVariable("patientId") Long patientId) {
        return patientService.getPatientDetail(patientId);
    }

    @GetMapping("/doctorInfo")
    public ResponseEntity<CustomApiResponse<?>> getDoctorInfo() {
        return patientService.getDoctorInfo();
    }

    @GetMapping("/{patientId}/riskLevels")
    public ResponseEntity<CustomApiResponse<?>> getPatientRiskLevels(@PathVariable("patientId") Long patientId) {
        return patientService.getPatientRiskLevels(patientId);
    }
}