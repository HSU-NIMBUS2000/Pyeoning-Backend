package com.hsu.pyeoning.domain.summary.web.dto;

import com.hsu.pyeoning.domain.chat.web.dto.ChatMessageFastApiResponseDto;
import com.hsu.pyeoning.global.api.dto.ChatHistory;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper=false) // warning 해결 : 상위 클래스 필드 제외
@Data
public class ChatSummaryFastApiResponseDto {
    private int status;
    private ChatSummaryFastApiResponseDto.DataContent data;
    private String message;

    @Data
    public static class DataContent {
        private String summary;
    }
}
