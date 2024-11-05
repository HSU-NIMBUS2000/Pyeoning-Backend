package com.hsu.pyeoning.domain.chat.service;

import com.hsu.pyeoning.domain.chat.web.dto.ChatMessageRequestDto;
import com.hsu.pyeoning.global.response.CustomApiResponse;
import org.springframework.http.ResponseEntity;

public interface ChatService {
    ResponseEntity<CustomApiResponse<?>> getChatContentForDoctor(Long patientId, int page, int size);
    ResponseEntity<CustomApiResponse<?>> getChatContentForPatient(String patientCode, int page, int size);
    // 환자가 채팅 메세지 전송
    ResponseEntity<CustomApiResponse<?>> processChatMessage(ChatMessageRequestDto chatMessageRequestDto);
    // 세션 종료
    ResponseEntity<CustomApiResponse<?>> endSessionForPatient();
}
