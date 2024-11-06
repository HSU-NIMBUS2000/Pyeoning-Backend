package com.hsu.pyeoning.domain.chat.web.dto;

import com.hsu.pyeoning.domain.chat.entity.Chat;
import com.hsu.pyeoning.global.api.dto.ChatHistory;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ChatMessageFastApiRequestDto extends ChatHistory {
    // 병명
    private String disease;
    // 질문하는 메세지 (새 메세지)
    private String newChat;
    // 채팅 기록 리스트 (기존 메세지)
    private List<ChatMessage> chatHistory = new ArrayList<>();
    // 프롬프트
    private String prompt;

    // ================ 편의 메서드 ================
    @Builder
    public ChatMessageFastApiRequestDto(String disease, String newChat, List<Chat> chats, String prompt) {
        this.disease = disease;
        this.newChat = newChat;
        this.chatHistory = convertChatsToChatMessages(chats); // 변환 메서드 호출
        this.prompt = prompt;
    }

    // List<Chat> ->  List<ChatMessage> 변환 메서드
    private List<ChatMessage> convertChatsToChatMessages(List<Chat> chats) {
        List<ChatMessage> chatMessages = new ArrayList<>();
        for (Chat chat : chats) {
            chatMessages.add(new ChatMessage(chat.getSender(), chat.getChatContent()));
        }
        return chatMessages;
    }
}
