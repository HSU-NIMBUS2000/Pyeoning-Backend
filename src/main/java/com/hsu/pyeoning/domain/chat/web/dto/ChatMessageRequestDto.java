package com.hsu.pyeoning.domain.chat.web.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ChatMessageRequestDto {
    @NotBlank(message = "새로운 질문이 없습니다.")
    private String chatContent;
}