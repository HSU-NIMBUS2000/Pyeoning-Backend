package com.hsu.pyeoning.domain.patient.web.controller;

import com.hsu.pyeoning.domain.patient.service.PatientService;
import com.hsu.pyeoning.domain.patient.web.dto.PatientRegisterDto;
import com.hsu.pyeoning.domain.patient.web.dto.PatientLoginDto;
import com.hsu.pyeoning.global.response.CustomApiResponse;
import jakarta.validation.Valid;
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
}