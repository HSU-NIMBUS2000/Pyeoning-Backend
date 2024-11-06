package com.hsu.pyeoning.domain.chat.web.dto;

import com.hsu.pyeoning.domain.summary.web.dto.ChatSummaryFastApiRequestDto;
import com.hsu.pyeoning.global.api.dto.ChatHistory;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
public class ChatMessageFastApiRequestDto extends ChatHistory {
    // 병명
    private String disease;

    // 질문하는 메세지 (새 메세지)
    private String newChat;

    // 채팅 기록 리스트 (기존 메세지)
    private List<ChatMessage> chatHistory;

    // 프롬프트
    private String prompt;

}
