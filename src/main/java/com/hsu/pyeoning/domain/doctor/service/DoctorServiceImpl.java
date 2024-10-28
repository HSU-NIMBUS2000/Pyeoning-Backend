package com.hsu.pyeoning.domain.doctor.service;

import com.hsu.pyeoning.domain.doctor.entity.Doctor;
import com.hsu.pyeoning.domain.doctor.repository.DoctorRepository;
import com.hsu.pyeoning.domain.doctor.web.dto.DoctorRegisterDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DoctorServiceImpl implements DoctorService {
    private final DoctorRepository doctorRepository;
    private final PasswordEncoder passwordEncoder;

    public Doctor registDoctor(DoctorRegisterDto dto) {
        Doctor doctor = Doctor.builder()
                .doctorName(dto.getDoctorName())
                .doctorHospital(dto.getDoctorHospital())
                .doctorLicense(dto.getDoctorLicense())
                .doctorPassword(passwordEncoder.encode(dto.getDoctorPassword()))
                .build();
        return doctorRepository.save(doctor);
    }
}
