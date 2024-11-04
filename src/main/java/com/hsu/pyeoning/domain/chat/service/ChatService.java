package com.hsu.pyeoning.domain.chat.service;

import com.hsu.pyeoning.global.response.CustomApiResponse;
import org.springframework.http.ResponseEntity;

public interface ChatService {
    ResponseEntity<CustomApiResponse<?>> getChatContentForDoctor(Long patientId, int page, int size);
    ResponseEntity<CustomApiResponse<?>> getChatContentForPatient(String patientCode, int page, int size);
}
