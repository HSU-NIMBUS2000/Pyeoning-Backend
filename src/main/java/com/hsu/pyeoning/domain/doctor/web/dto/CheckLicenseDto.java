package com.hsu.pyeoning.domain.doctor.web.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CheckLicenseDto {
    @NotNull(message = "면허 번호를 입력해주세요.")
    private Long doctorLicense;
}
