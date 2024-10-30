package com.hsu.pyeoning.domain.doctor.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class DoctorRegisterDto {
    @NotBlank(message = "이름을 입력해주세요.")
    private String doctorName;

    @NotBlank(message = "소속병원을 입력해주세요.")
    private String doctorHospital;

    @NotBlank(message = "면허번호를 입력해주세요.")
    private Long doctorLicense;

    @NotBlank(message = "비밀번호를 입력해주세요.")
    private String doctorPassword;
}
