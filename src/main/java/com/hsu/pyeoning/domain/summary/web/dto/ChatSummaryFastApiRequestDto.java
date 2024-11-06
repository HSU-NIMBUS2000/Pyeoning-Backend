package com.hsu.pyeoning.domain.summary.web.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
public class ChatSummaryFastApiRequestDto {
    private String disease;
    private List<ChatMessage> chatHistory = new ArrayList<>();

    @Data
    @NoArgsConstructor
    public static class ChatMessage {
        private String sender;
        private String message;

        public ChatMessage(String sender, String message) {
            this.sender = sender;
            this.message = message;
        }
    }

    // 편의 메서드 =========================================================
    // 새로운 메시지를 추가하는 메서드
    public void addChatMessage(String sender, String message) {
        this.chatHistory.add(new ChatMessage(sender, message));
    }
}
