package com.hsu.pyeoning.domain.chat.service;

import com.hsu.pyeoning.domain.chat.entity.Chat;
import com.hsu.pyeoning.domain.chat.repository.ChatRepository;
import com.hsu.pyeoning.domain.patient.repository.PatientRepository;
import com.hsu.pyeoning.global.response.CustomApiResponse;
import com.hsu.pyeoning.global.security.jwt.util.AuthenticationUserUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final ChatRepository chatRepository;
    private final PatientRepository patientRepository;
    private final AuthenticationUserUtils authenticationUserUtils;

    @Override
    public ResponseEntity<CustomApiResponse<?>> getChatContent(Long patientId, int page, int size) {
        String currentUserId = authenticationUserUtils.getCurrentUserId();

        // 의사 토큰이 유효한지 확인
        if (currentUserId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(CustomApiResponse.createFailWithout(401, "유효하지 않은 토큰입니다."));
        }

        // 환자 존재 여부 확인
        if (!patientRepository.existsById(patientId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(CustomApiResponse.createFailWithout(404, "해당 ID에 해당하는 환자가 존재하지 않습니다."));
        }

        // 페이징
        Page<Chat> chatPage = chatRepository.findByPatient_PatientId(patientId, PageRequest.of(page - 1, size));

        // 대화 내용이 존재하는 경우
        if (chatPage.hasContent()) {
            List<Map<String, Object>> chatList = chatPage.stream()
                    .map(chat -> {
                        Map<String, Object> chatData = new LinkedHashMap<>();
                        chatData.put("chatId", chat.getChatId());
                        chatData.put("chatIsSend", chat.isChatIsSend() ? 1 : 0); // 발신이면 1, 수신이면 0
                        chatData.put("chatContent", chat.getChatContent());
                        chatData.put("createdAt", chat.getCreatedAt());
                        return chatData;
                    })
                    .collect(Collectors.toList());

            return ResponseEntity.ok(CustomApiResponse.createSuccess(200, chatList, "대화 내용 조회에 성공했습니다."));
        }

        // 대화 내용이 존재하지 않는 경우
        return ResponseEntity.ok(CustomApiResponse.createSuccess(200, null, "대화 내용 조회에 성공했습니다. 대화 내용이 존재하지 않습니다."));
    }

}
