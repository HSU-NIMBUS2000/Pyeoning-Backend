package com.hsu.pyeoning.domain.doctor.service;

import com.hsu.pyeoning.domain.doctor.entity.Doctor;
import com.hsu.pyeoning.domain.doctor.repository.DoctorRepository;
import com.hsu.pyeoning.domain.doctor.web.dto.DoctorRegisterDto;
import com.hsu.pyeoning.global.response.CustomApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DoctorServiceImpl implements DoctorService {
    private final DoctorRepository doctorRepository;
    private final PasswordEncoder passwordEncoder;

    public ResponseEntity<CustomApiResponse<?>> registDoctor(DoctorRegisterDto dto) {
        Doctor doctor = Doctor.builder()
                .doctorName(dto.getDoctorName())
                .doctorHospital(dto.getDoctorHospital())
                .doctorLicense(dto.getDoctorLicense())
                .doctorPassword(passwordEncoder.encode(dto.getDoctorPassword()))
                .build();
        doctorRepository.save(doctor);

        return ResponseEntity.ok(CustomApiResponse.createSuccess(200, null, "의사 등록에 성공했습니다."));
    }
}
