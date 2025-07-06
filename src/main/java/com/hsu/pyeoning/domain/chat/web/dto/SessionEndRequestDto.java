package com.hsu.pyeoning.domain.chat.web.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SessionEndRequestDto {
    @NotBlank(message = "환자의 토큰을 삽입해주세요.")
    private String token;
}
