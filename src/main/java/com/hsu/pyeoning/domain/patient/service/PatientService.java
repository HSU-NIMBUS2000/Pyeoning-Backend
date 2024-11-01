// PatientService.java
package com.hsu.pyeoning.domain.patient.service;

import com.hsu.pyeoning.domain.patient.web.dto.PatientRegisterDto;
import com.hsu.pyeoning.domain.patient.web.dto.PatientLoginDto;
import com.hsu.pyeoning.domain.patient.web.dto.ModifyPromptDto;
import com.hsu.pyeoning.global.response.CustomApiResponse;
import org.springframework.http.ResponseEntity;

public interface PatientService {
    ResponseEntity<CustomApiResponse<?>> registerPatient(PatientRegisterDto dto);
    ResponseEntity<CustomApiResponse<?>> patientLogin(PatientLoginDto dto);
    ResponseEntity<CustomApiResponse<?>> modifyPrompt(Long patientId, ModifyPromptDto dto);
    ResponseEntity<CustomApiResponse<?>> getPatientList(int page, int size);
}