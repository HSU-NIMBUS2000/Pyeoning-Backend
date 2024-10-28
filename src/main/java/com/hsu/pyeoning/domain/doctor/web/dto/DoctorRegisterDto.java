package com.hsu.pyeoning.domain.doctor.web.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class DoctorRegisterDto {
    @NotNull(message = "이름을 입력해주세요.")
    private String doctorName;

    @NotNull(message = "소속병원을 입력해주세요.")
    private String doctorHospital;

    @NotNull(message = "면허번호를 입력해주세요.")
    private Long doctorLicense;

    @NotNull(message = "비밀번호를 입력해주세요.")
    private String doctorPassword;
}
