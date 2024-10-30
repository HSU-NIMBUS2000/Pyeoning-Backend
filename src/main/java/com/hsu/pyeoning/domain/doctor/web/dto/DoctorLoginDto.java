package com.hsu.pyeoning.domain.doctor.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class DoctorLoginDto {
    @NotNull(message = "면허번호를 입력해주세요.")
    private Long doctorLicense;

    @NotBlank(message = "비밀번호를 입력해주세요.")
    private String doctorPassword;
}
