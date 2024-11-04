package com.hsu.pyeoning.domain.chat.web.controller;

import com.hsu.pyeoning.domain.chat.service.ChatServiceImpl;
import com.hsu.pyeoning.global.response.CustomApiResponse;
import com.hsu.pyeoning.global.security.jwt.util.AuthenticationUserUtils;
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
    private final AuthenticationUserUtils authenticationUserUtils;

    // 의사가 특정 환자의 대화 내용 조회
    @GetMapping("/history/doctor")
    public ResponseEntity<CustomApiResponse<?>> getDoctorChatHistory(
            @RequestParam Long patientId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return chatService.getChatContentForDoctor(patientId, page, size);
    }

    // 환자가 자신의 대화 내용 조회
    @GetMapping("/history/patient")
    public ResponseEntity<CustomApiResponse<?>> getPatientChatHistory(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        String currentPatientCode = authenticationUserUtils.getCurrentUserId();
        return chatService.getChatContentForPatient(currentPatientCode, page, size);
    }
}
