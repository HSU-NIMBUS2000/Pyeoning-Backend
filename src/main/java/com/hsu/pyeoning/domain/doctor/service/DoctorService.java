package com.hsu.pyeoning.domain.doctor.service;

import com.hsu.pyeoning.domain.doctor.web.dto.DoctorRegisterDto;
import com.hsu.pyeoning.global.response.CustomApiResponse;
import org.springframework.http.ResponseEntity;

public interface DoctorService {
    ResponseEntity<CustomApiResponse<?>> registDoctor(DoctorRegisterDto dto);
}
