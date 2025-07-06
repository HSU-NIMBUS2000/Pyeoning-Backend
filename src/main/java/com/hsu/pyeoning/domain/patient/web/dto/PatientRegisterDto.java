package com.hsu.pyeoning.domain.patient.web.dto;

import com.hsu.pyeoning.domain.patient.entity.Gender;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import java.util.Date;

@Getter
@Setter
public class PatientRegisterDto {
    @NotBlank(message = "환자 이름을 입력해주세요.")
    private String patientName;

    @NotNull(message = "환자 생년월일을 입력해주세요.")
    private Date patientBirth;

    @NotNull(message = "환자 성별을 입력해주세요.")
    private Gender patientGender;

    @NotBlank(message = "환자 이메일을 입력해주세요.")
    private String patientEmail;

    @NotBlank(message = "예방 질병을 입력해주세요.")
    private String pyeoningDisease;

    @NotBlank(message = "예방 프롬프트를 입력해주세요.")
    private String pyeoningPrompt;

    @NotBlank(message = "예방 특성을 입력해주세요.")
    private String pyeoningSpecial;
}