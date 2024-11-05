package com.hsu.pyeoning.domain.chat.web.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class ChatMessageFastApiRequestDto {
    // 병명
    private String disease;

    // 질문하는 메세지 (새 메세지)
    private String newChat;

    // 채팅 기록 리스트 (기존 메세지)
    private List<ChatHistory> chatHistory;

    // 프롬프트
    private String prompt;

    @Data
    @Builder
    public static class ChatHistory {
        private String sender;
        private String message;
    }
}
