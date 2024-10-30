package com.hsu.pyeoning.domain.doctor.service;

import com.hsu.pyeoning.domain.doctor.entity.Doctor;
import com.hsu.pyeoning.domain.doctor.repository.DoctorRepository;
import com.hsu.pyeoning.domain.doctor.web.dto.CheckLicenseDto;
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

    // 의사 등록
    public ResponseEntity<CustomApiResponse<?>> registDoctor(DoctorRegisterDto dto) {

        // 면허 번호 중복 확인
        if (doctorRepository.findByDoctorLicense(dto.getDoctorLicense()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(CustomApiResponse.createFailWithout(409, "이미 등록된 면허 번호입니다."));
        }

        Doctor doctor = Doctor.builder()
                .doctorName(dto.getDoctorName())
                .doctorHospital(dto.getDoctorHospital())
                .doctorLicense(dto.getDoctorLicense())
                .doctorPassword(passwordEncoder.encode(dto.getDoctorPassword()))
                .build();
        doctorRepository.save(doctor);

        return ResponseEntity.ok(CustomApiResponse.createSuccess(200, null, "의사 등록에 성공했습니다."));
    }

    // 의사 면허 중복 확인
    public ResponseEntity<CustomApiResponse<?>> checkLicenseNumber(CheckLicenseDto dto) {
        boolean isDuplicate = isLicenseNumberDuplicate(dto.getDoctorLicense());
        if (isDuplicate) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(CustomApiResponse.createFailWithout(409, "이미 등록된 면허 번호입니다."));
        }
        return ResponseEntity.ok(CustomApiResponse.createSuccess(200, null, "사용 가능한 면허 번호입니다."));
    }

    public boolean isLicenseNumberDuplicate(Long licenseNumber) {
        return doctorRepository.findByDoctorLicense(licenseNumber).isPresent();
    }

}
