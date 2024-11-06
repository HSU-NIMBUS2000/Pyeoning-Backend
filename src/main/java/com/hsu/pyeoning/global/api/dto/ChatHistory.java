package com.hsu.pyeoning.global.api.dto;


import com.hsu.pyeoning.domain.chat.entity.Chat;
import com.hsu.pyeoning.domain.chat.web.dto.ChatMessageFastApiRequestDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
public class ChatHistory {
    private List<ChatMessage> chatHistory = new ArrayList<>();

    // 채팅 메시지 추가 메서드
    public void addChatMessage(String sender, String message) {
        this.chatHistory.add(new ChatMessage(sender, message));
    }

    // Chat 리스트를 ChatMessage 리스트로 변환하여 설정하는 메서드
    public void setChatHistoryFromChats(List<Chat> chats) {
        for (Chat chat : chats) {
            this.addChatMessage(chat.getSender(), chat.getChatContent());
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChatMessage {
        private String sender;
        private String message;
    }

}