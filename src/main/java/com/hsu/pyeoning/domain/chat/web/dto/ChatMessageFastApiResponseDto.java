package com.hsu.pyeoning.domain.chat.web.dto;

import lombok.Data;

// FastAPI Response DTO
@Data
public class ChatMessageFastApiResponseDto {
    private int status;
    private DataContent data;
    private String message;

    @Data
    public static class DataContent {
        private String newChat;
    }
}
