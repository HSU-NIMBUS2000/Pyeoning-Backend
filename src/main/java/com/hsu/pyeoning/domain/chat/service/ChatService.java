package com.hsu.pyeoning.domain.chat.service;

import com.hsu.pyeoning.domain.chat.web.dto.ChatMessageRequestDTO;
import com.hsu.pyeoning.global.response.CustomApiResponse;
import org.springframework.http.ResponseEntity;

public interface ChatService {
    ResponseEntity<CustomApiResponse<?>> getChatContentForDoctor(Long patientId, int page, int size);
    ResponseEntity<CustomApiResponse<?>> getChatContentForPatient(String patientCode, int page, int size);
    // 환자가 채팅 메세지 전송
    ResponseEntity<CustomApiResponse<?>> processChatMessage(String currentPatientCode, ChatMessageRequestDTO chatMessageRequestDTO);
}
