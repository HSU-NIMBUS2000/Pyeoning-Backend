// PatientServiceImpl.java
package com.hsu.pyeoning.domain.patient.service;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import com.hsu.pyeoning.domain.chat.entity.Chat;
import com.hsu.pyeoning.domain.chat.repository.ChatRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.core.Authentication;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.hsu.pyeoning.domain.doctor.entity.Doctor;
import com.hsu.pyeoning.domain.doctor.repository.DoctorRepository;
import com.hsu.pyeoning.domain.doctor.web.dto.DoctorInfoDto;
import com.hsu.pyeoning.domain.patient.entity.Patient;
import com.hsu.pyeoning.domain.patient.repository.PatientRepository;
import com.hsu.pyeoning.domain.patient.web.dto.ModifyPromptDto;
import com.hsu.pyeoning.domain.patient.web.dto.PatientListDto;
import com.hsu.pyeoning.domain.patient.web.dto.PatientLoginDto;
import com.hsu.pyeoning.domain.patient.web.dto.PatientRegisterDto;
import com.hsu.pyeoning.global.response.CustomApiResponse;
import com.hsu.pyeoning.global.security.jwt.JwtTokenProvider;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PatientServiceImpl implements PatientService {

    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final ChatRepository chatRepository;
    private final JavaMailSender mailSender;

    @Transactional
    @Override
    public ResponseEntity<CustomApiResponse<?>> registerPatient(PatientRegisterDto dto) {
        String doctorLicenseStr = SecurityContextHolder.getContext().getAuthentication().getName();
        Long doctorLicense = Long.valueOf(doctorLicenseStr);
        Doctor doctor = doctorRepository.findByDoctorLicense(doctorLicense)
                .orElseThrow(() -> new RuntimeException("의사 정보를 찾을 수 없습니다."));

        Patient patient = Patient.builder()
                .patientName(dto.getPatientName())
                .patientBirth(dto.getPatientBirth())
                .patientGender(dto.getPatientGender())
                .patientEmail(dto.getPatientEmail())
                .patientCode(generatePatientCode())
                .doctorId(doctor)
                .pyeoningDisease(dto.getPyeoningDisease())
                .pyeoningPrompt(dto.getPyeoningPrompt())
                .pyeoningSpecial(dto.getPyeoningSpecial())
                .build();

        patientRepository.save(patient);

        // 환자 코드 이메일로 전송
        sendPatientCodeEmail(patient.getPatientEmail(), patient.getPatientCode());

        CustomApiResponse<?> response = new CustomApiResponse<>(
                HttpStatus.OK.value(),
                null,
                "환자 등록에 성공했습니다."
        );

        // 환자 등록 시 Chat 기본 세팅
        Chat dafaultChat = Chat.builder()
                .patient(patient)
                .chatContent("안녕하세요 펴닝입니다. 무엇을 도와드릴까요?")
                .chatIsSend(false)
                .sessionEnd(true)
                .build();
        chatRepository.save(dafaultChat);

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

        String token = jwtTokenProvider.createToken(patient.getPatientCode(), "ROLE_PATIENT");
        return ResponseEntity.ok(new CustomApiResponse<>(200, token, "환자 로그인에 성공했습니다."));
    }

    @Transactional
    @Override
    public ResponseEntity<CustomApiResponse<?>> modifyPrompt(Long patientId, ModifyPromptDto dto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new CustomApiResponse<>(404, null, "유효하지 않은 토큰입니다."));
        }

        String doctorLicenseStr = authentication.getName();
        Long doctorLicense = Long.valueOf(doctorLicenseStr);

        Doctor doctor = doctorRepository.findByDoctorLicense(doctorLicense)
                .orElse(null);
        if (doctor == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new CustomApiResponse<>(404, null, "의사 정보를 찾을 수 없습니다."));
        }

        Patient patient = patientRepository.findById(patientId).orElse(null);
        if (patient == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new CustomApiResponse<>(404, null, "환자 정보를 찾을 수 없습니다."));
        }

        if (!patient.getDoctorId().getDoctorId().equals(doctor.getDoctorId())) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new CustomApiResponse<>(404, null, "해당 의사는 해당 환자의 담당 의사가 아닙니다."));
        }

        if (dto.getPatientName() != null) {
            patient.setPatientName(dto.getPatientName());
        } else {
            dto.setPatientName(patient.getPatientName());
        }
        if (dto.getPatientBirth() != null) {
            patient.setPatientBirth(dto.getPatientBirth());
        } else {
            dto.setPatientBirth(patient.getPatientBirth());
        }
        if (dto.getPyeoningDisease() != null) {
            patient.setPyeoningDisease(dto.getPyeoningDisease());
        } else {
            dto.setPyeoningDisease(patient.getPyeoningDisease());
        }
        if (dto.getPyeoningPrompt() != null) {
            patient.setPyeoningPrompt(dto.getPyeoningPrompt());
        } else {
            dto.setPyeoningPrompt(patient.getPyeoningPrompt());
        }
        if (dto.getPyeoningSpecial() != null) {
            patient.setPyeoningSpecial(dto.getPyeoningSpecial());
        } else {
            dto.setPyeoningSpecial(patient.getPyeoningSpecial());
        }

        patientRepository.save(patient);
        return ResponseEntity.ok(new CustomApiResponse<>(
                HttpStatus.OK.value(),
                dto,
                "환자 정보 수정에 성공했습니다."
        ));
    }

    @Override
    public ResponseEntity<CustomApiResponse<?>> getPatientList(int page, int size, String category, String keyword) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String doctorLicenseStr = authentication.getName();
        Long doctorLicense = Long.valueOf(doctorLicenseStr);
        Doctor doctor = doctorRepository.findByDoctorLicense(doctorLicense)
                .orElse(null);
        if (doctor == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new CustomApiResponse<>(404, null, "의사 정보를 찾을 수 없습니다."));
        }

        PageRequest pageRequest = PageRequest.of(page - 1, size);
        Page<Patient> patientPage;

        // 카테고리와 키워드가 없는 경우 전체 리스트 반환
        if (category == null || keyword == null) {
            patientPage = patientRepository.findByDoctorId(doctor, pageRequest);
        } else {
            // 카테고리별 검색
            switch (category) {
                case "disease":
                    patientPage = patientRepository.findByDoctorIdAndPyeoningDiseaseContaining(
                            doctor, keyword, pageRequest);
                    break;
                case "name":
                    patientPage = patientRepository.findByDoctorIdAndPatientNameContaining(
                            doctor, keyword, pageRequest);
                    break;
                case "special":
                    patientPage = patientRepository.findByDoctorIdAndPyeoningSpecialContaining(
                            doctor, keyword, pageRequest);
                    break;
                default:
                    return ResponseEntity.badRequest()
                            .body(new CustomApiResponse<>(400, null, "잘못된 카테고리입니다."));
            }
        }

        if (patientPage.hasContent()) {
            List<PatientListDto> patients = patientPage.getContent().stream()
                    .map(patient -> {
                        LocalDate birthDate = convertToLocalDateViaSqlDate(patient.getPatientBirth());
                        return new PatientListDto(
                                patient.getPatientId(),
                                patient.getPatientName(),
                                patient.getPatientGender().name(),
                                patient.getPatientBirth().toString(),
                                calculateAge(birthDate),
                                patient.getPyeoningSpecial()
                        );
                    })
                    .collect(Collectors.toList());
            
            return ResponseEntity.ok(new CustomApiResponse<>(
                    HttpStatus.OK.value(),
                    patients,
                    "환자 목록 조회에 성공했습니다."
            ));
        } else {
            return ResponseEntity.ok(new CustomApiResponse<>(
                    HttpStatus.OK.value(),
                    null,
                    "환자 목록 조회에 성공했습니다. 환자 목록이 없습니다."
            ));
        }
    }

    @Override
    public ResponseEntity<CustomApiResponse<?>> getPatientDetail(Long patientId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String doctorLicenseStr = authentication.getName();
        Long doctorLicense = Long.valueOf(doctorLicenseStr);
        Doctor doctor = doctorRepository.findByDoctorLicense(doctorLicense)
                .orElse(null);
        if (doctor == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new CustomApiResponse<>(404, null, "의사 정보를 찾을 수 없습니다."));
        }

        Patient patient = patientRepository.findById(patientId).orElse(null);
        if (patient == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new CustomApiResponse<>(404, null, "해당 ID의 환자가 DB에 존재하지 않습니다."));
        }

        if (!patient.getDoctorId().getDoctorId().equals(doctor.getDoctorId())) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new CustomApiResponse<>(404, null, "해당 의사는 해당 환자의 담당 의사가 아닙니다."));
        }

        PatientListDto patientDetail = new PatientListDto(
                patient.getPatientId(),
                patient.getPatientName(),
                patient.getPatientGender().name(),
                patient.getPatientBirth().toString(),
                calculateAge(convertToLocalDateViaSqlDate(patient.getPatientBirth())),
                patient.getPyeoningSpecial()
        );
        return ResponseEntity.ok(new CustomApiResponse<>(200, patientDetail, "환자 상세 조회에 성공했습니다."));
    }

    @Override
    public ResponseEntity<CustomApiResponse<?>> getDoctorInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String patientCode = authentication.getName();
        Patient patient = patientRepository.findByPatientCode(patientCode)
                .orElse(null);
        if (patient == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new CustomApiResponse<>(404, null, "환자 정보를 찾을 수 없습니다."));
        }

        Doctor doctor = patient.getDoctorId();
        if (doctor == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new CustomApiResponse<>(404, null, "해당 환자의 담당의사가 존재하지 않습니다."));
        }

        DoctorInfoDto doctorInfo = new DoctorInfoDto(
                doctor.getDoctorName(),
                doctor.getDoctorHospital()
        );

        return ResponseEntity.ok(new CustomApiResponse<>(200, doctorInfo, "의사 정보 조회에 성공했습니다."));
    }

    private LocalDate convertToLocalDateViaSqlDate(java.util.Date dateToConvert) {
        if (dateToConvert == null) {
            return null;
        }
        if (dateToConvert instanceof java.sql.Date sqlDate) {
            return sqlDate.toLocalDate();
        }
        return new java.sql.Date(dateToConvert.getTime()).toLocalDate();
    }

    private String calculateAge(LocalDate birthDate) {
        return String.valueOf(java.time.Period.between(birthDate, java.time.LocalDate.now()).getYears());
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

    // 이메일 전송 메소드
    private void sendPatientCodeEmail(String email, String patientCode) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("환자 코드 안내");
        message.setText("안녕하세요. 환자님의 환자 코드는 " + patientCode + "입니다.");
        mailSender.send(message);
    }
}