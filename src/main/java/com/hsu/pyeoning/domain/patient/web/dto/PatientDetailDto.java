package com.hsu.pyeoning.domain.patient.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PatientDetailDto {
    private Long patientId;         // 추가
    private String patientName;
    private String patientGender;
    private String patientBirth;
    private String patientAge;      // 추가
    private String pyeoningDisease;
    private String pyeoningPrompt;
    private String pyeoningSpecial;
}
