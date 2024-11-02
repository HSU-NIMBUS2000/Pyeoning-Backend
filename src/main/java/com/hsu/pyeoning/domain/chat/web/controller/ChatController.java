package com.hsu.pyeoning.domain.chat.web.controller;

import com.hsu.pyeoning.domain.chat.service.ChatServiceImpl;
import com.hsu.pyeoning.global.response.CustomApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatServiceImpl chatService;

    @GetMapping("/history")
    public ResponseEntity<CustomApiResponse<?>> getChatHistory(
            @RequestParam Long patientId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return chatService.getChatContent(patientId, page, size);
    }
}
