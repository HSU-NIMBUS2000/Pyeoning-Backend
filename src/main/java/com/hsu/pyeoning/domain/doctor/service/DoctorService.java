package com.hsu.pyeoning.domain.doctor.service;

import com.hsu.pyeoning.domain.doctor.web.dto.CheckLicenseDto;
import com.hsu.pyeoning.domain.doctor.web.dto.DoctorLoginDto;
import com.hsu.pyeoning.domain.doctor.web.dto.DoctorRegisterDto;
import com.hsu.pyeoning.global.response.CustomApiResponse;
import org.springframework.http.ResponseEntity;

public interface DoctorService {
    ResponseEntity<CustomApiResponse<?>> registDoctor(DoctorRegisterDto dto);
    ResponseEntity<CustomApiResponse<?>> checkLicenseNumber(CheckLicenseDto dto);
    ResponseEntity<CustomApiResponse<?>> doctorLogin(DoctorLoginDto dto);
    boolean isLicenseNumberDuplicate(Long licenseNumber);
}
