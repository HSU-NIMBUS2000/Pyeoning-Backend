package com.hsu.pyeoning.domain.chat.web.dto;

import com.hsu.pyeoning.domain.summary.web.dto.ChatSummaryFastApiRequestDto;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
public class ChatMessageFastApiRequestDto {
    // 병명
    private String disease;

    // 질문하는 메세지 (새 메세지)
    private String newChat;

    // 채팅 기록 리스트 (기존 메세지)
    private List<ChatMessage> chatHistory;

    // 프롬프트
    private String prompt;

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
        this.chatHistory.add(new ChatMessageFastApiRequestDto.ChatMessage(sender, message));
    }
}
