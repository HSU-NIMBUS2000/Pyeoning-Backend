package com.hsu.pyeoning.domain.doctor.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
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

    @NotNull(message = "면허 번호를 입력해주세요.")
    private Long doctorLicense;

    @NotBlank(message = "이메일을 입력해주세요.")
    private String doctorEmail;

    @NotBlank(message = "비밀번호를 입력해주세요.")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
            message = "비밀번호는 영문/숫자/특수기호를 조합하여 8자 이상 설정해야 합니다.")
    private String doctorPassword;
}
