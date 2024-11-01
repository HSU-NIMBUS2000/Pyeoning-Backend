package com.hsu.pyeoning.domain.patient.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PatientListDto {
    private Long patientId;
    private String patientName;
    private String patientGender;
    private String patientBirth;
    private String patientAge;
    private String pyeoningSpecial;
}
