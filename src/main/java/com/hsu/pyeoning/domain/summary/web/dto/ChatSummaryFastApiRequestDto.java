package com.hsu.pyeoning.domain.summary.web.dto;

import com.hsu.pyeoning.domain.chat.entity.Chat;
import com.hsu.pyeoning.global.api.dto.ChatHistory;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper=false) // warning 해결 : 상위 클래스 필드 제외
@Data
public class ChatSummaryFastApiRequestDto extends ChatHistory {
    private String disease;
    private List<ChatMessage> chatHistory = new ArrayList<>();

    // ================ 편의 메서드 ================
    @Builder
    public ChatSummaryFastApiRequestDto(String disease, String newChat, List<Chat> chats, String prompt) {
        this.disease = disease;
        this.chatHistory = convertChatsToChatMessages(chats); // 변환 메서드 호출
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
