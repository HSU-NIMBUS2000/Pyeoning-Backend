package com.hsu.pyeoning.domain.doctor.web.controller;

import com.hsu.pyeoning.domain.doctor.service.DoctorServiceImpl;
import com.hsu.pyeoning.domain.doctor.web.dto.CheckLicenseDto;
import com.hsu.pyeoning.domain.doctor.web.dto.DoctorRegisterDto;
import com.hsu.pyeoning.global.response.CustomApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/doctor")
@RequiredArgsConstructor
public class DoctorController {

    private final DoctorServiceImpl doctorService;

    @PostMapping("/register")
    public ResponseEntity<CustomApiResponse<?>> registerDoctor(@Valid @RequestBody DoctorRegisterDto dto) {
        return doctorService.registDoctor(dto);
    }

    @PostMapping("/checkLicense")
    public ResponseEntity<CustomApiResponse<?>> checkLicenseNumber(@RequestBody CheckLicenseDto dto) {
        return doctorService.checkLicenseNumber(dto);
    }



}
