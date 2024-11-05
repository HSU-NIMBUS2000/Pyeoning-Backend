package com.hsu.pyeoning.domain.chat.web.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ChatMessageRequestDto {
    @NotBlank
    private String chatContent;
}
