package com.hsu.pyeoning.domain.doctor.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DoctorInfoDto {
    private String doctorName;
    private String doctorHospital;
}
