package com.hsu.pyeoning.domain.chat.web.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class ChatResponseDto {
    private Long chatId;
    private int chatIsSend;
    private String chatContent;
    private LocalDateTime createdAt;
}
