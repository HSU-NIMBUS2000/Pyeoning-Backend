package com.hsu.pyeoning.domain.chat.service;

import com.hsu.pyeoning.global.response.CustomApiResponse;
import org.springframework.http.ResponseEntity;

public interface ChatService {
    ResponseEntity<CustomApiResponse<?>> getChatContent(Long patientId, int page, int size);
}
