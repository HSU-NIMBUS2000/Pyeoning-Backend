package com.hsu.pyeoning.domain.summary.web.dto;

import com.hsu.pyeoning.global.api.dto.ChatHistory;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
public class ChatSummaryFastApiRequestDto extends ChatHistory {
    private String disease;
    private List<ChatMessage> chatHistory = new ArrayList<>();

}
