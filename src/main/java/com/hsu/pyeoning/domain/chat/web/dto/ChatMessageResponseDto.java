package com.hsu.pyeoning.domain.chat.web.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ChatMessageResponseDto {
    // 채팅 ID
    private Long chatId;
    // 채팅 내용
    private String chatContent;
    // 채팅 생성 일시
    private LocalDateTime createdAt;
}
