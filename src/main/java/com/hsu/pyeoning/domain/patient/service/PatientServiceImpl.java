// PatientServiceImpl.java
package com.hsu.pyeoning.domain.patient.service;

import com.hsu.pyeoning.domain.patient.entity.Patient;
import com.hsu.pyeoning.domain.patient.repository.PatientRepository;
import com.hsu.pyeoning.domain.patient.web.dto.PatientRegisterDto;
import com.hsu.pyeoning.domain.patient.web.dto.PatientLoginDto;
import com.hsu.pyeoning.domain.patient.web.dto.ModifyPromptDto;
import com.hsu.pyeoning.global.response.CustomApiResponse;
import com.hsu.pyeoning.domain.doctor.entity.Doctor;
import com.hsu.pyeoning.domain.doctor.repository.DoctorRepository;
import com.hsu.pyeoning.global.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class PatientServiceImpl implements PatientService {

    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public ResponseEntity<CustomApiResponse<?>> registerPatient(PatientRegisterDto dto) {
        String doctorLicenseStr = SecurityContextHolder.getContext().getAuthentication().getName();
        Long doctorLicense = Long.parseLong(doctorLicenseStr);
        Doctor doctor = doctorRepository.findByDoctorLicense(doctorLicense)
                .orElseThrow(() -> new RuntimeException("의사 정보를 찾을 수 없습니다."));

        Patient patient = Patient.builder()
                .patientName(dto.getPatientName())
                .patientBirth(dto.getPatientBirth())
                .patientGender(dto.getPatientGender())
                .patientEmail(dto.getPatientEmail())
                .patientCode(generatePatientCode())
                .doctorId(doctor)
                .build();

        patientRepository.save(patient);

        CustomApiResponse<?> response = new CustomApiResponse<>(
                HttpStatus.OK.value(),
                null,
                "환자 등록에 성공했습니다."
        );

        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<CustomApiResponse<?>> patientLogin(PatientLoginDto dto) {
        Patient patient = patientRepository.findByPatientCode(dto.getPatientCode())
                .orElse(null);
        if (patient == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new CustomApiResponse<>(404, null, "유효하지 않은 접속코드입니다."));
        }

        String token = jwtTokenProvider.createToken(patient.getPatientCode());
        return ResponseEntity.ok(new CustomApiResponse<>(200, token, "로그인에 성공했습니다."));
    }

    @Override
    public ResponseEntity<CustomApiResponse<?>> modifyPrompt(Long patientId, ModifyPromptDto dto) {
        String doctorLicenseStr = SecurityContextHolder.getContext().getAuthentication().getName();
        Long doctorLicense = Long.parseLong(doctorLicenseStr);
        Doctor doctor = doctorRepository.findByDoctorLicense(doctorLicense)
                .orElseThrow(() -> new RuntimeException("의사 정보를 찾을 수 없습니다."));

        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("해당 ID의 환자를 찾을 수 없습니다."));

        if (!patient.getDoctorId().getDoctorId().equals(doctor.getDoctorId())) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new CustomApiResponse<>(404, null, "해당 의사는 해당 환자의 담당 의사가 아닙니다."));
        }

        if (dto.getPatientName() != null) {
            patient.setPatientName(dto.getPatientName());
        }
        if (dto.getPatientBirth() != null) {
            patient.setPatientBirth(dto.getPatientBirth());
        }
        if (dto.getPyeoningDisease() != null) {
            patient.setPyeoningDisease(dto.getPyeoningDisease());
        }
        if (dto.getPyeoningPrompt() != null) {
            patient.setPyeoningPrompt(dto.getPyeoningPrompt());
        }
        if (dto.getPyeoningSpecial() != null) {
            patient.setPyeoningSpecial(dto.getPyeoningSpecial());
        }

        patientRepository.save(patient);

        return ResponseEntity.ok(new CustomApiResponse<>(
                HttpStatus.OK.value(),
                dto,
                "환자 정보 수정에 성공했습니다."
        ));
    }

    private String generatePatientCode() {
        int length = 8;
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new SecureRandom();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }
}