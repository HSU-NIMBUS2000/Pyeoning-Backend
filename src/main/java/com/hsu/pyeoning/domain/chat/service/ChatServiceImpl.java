package com.hsu.pyeoning.domain.chat.service;

import com.hsu.pyeoning.domain.chat.entity.Chat;
import com.hsu.pyeoning.domain.chat.repository.ChatRepository;
import com.hsu.pyeoning.domain.chat.web.dto.ChatDto;
import com.hsu.pyeoning.domain.chat.web.dto.ChatMessageRequestDto;
import com.hsu.pyeoning.domain.chat.web.dto.ChatMessageResponseDto;
import com.hsu.pyeoning.domain.patient.entity.Patient;
import com.hsu.pyeoning.domain.patient.repository.PatientRepository;
import com.hsu.pyeoning.global.response.CustomApiResponse;
import com.hsu.pyeoning.global.security.jwt.util.AuthenticationUserUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final ChatRepository chatRepository;
    private final PatientRepository patientRepository;
    private final AuthenticationUserUtils authenticationUserUtils;

    // 의사가 특정 환자의 대화 내용 조회
    @Override
    public ResponseEntity<CustomApiResponse<?>> getChatContentForDoctor(Long patientId, int page, int size) {
        String currentUserId = authenticationUserUtils.getCurrentUserId();

        // 현재 사용자가 의사 권한인지 확인
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isDoctor = authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_DOCTOR"));
        if (!isDoctor) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(CustomApiResponse.createFailWithout(403, "접근 권한이 없습니다."));
        }

        // 환자 존재 여부 확인
        if (!patientRepository.existsById(patientId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(CustomApiResponse.createFailWithout(404, "해당 ID에 해당하는 환자가 존재하지 않습니다."));
        }

        // 페이징 처리 후 대화 내용 조회
        Page<Chat> chatPage = chatRepository.findByPatient_PatientId(patientId, PageRequest.of(page - 1, size));
        if (chatPage.hasContent()) {
            List<ChatDto> chatList = chatPage.stream()
                    .map(chat -> new ChatDto(
                            chat.getChatId(),
                            chat.isChatIsSend() ? 1 : 0,
                            chat.getChatContent(),
                            chat.getCreatedAt()
                    ))
                    .collect(Collectors.toList());

            return ResponseEntity.ok(CustomApiResponse.createSuccess(200, chatList, "대화 내용 조회에 성공했습니다."));
        }

        // 대화 내용이 존재하지 않는 경우
        return ResponseEntity.ok(CustomApiResponse.createSuccess(200, null, "대화 내용 조회에 성공했습니다. 대화 내용이 존재하지 않습니다."));
    }

    // 환자가 자신의 대화 내용 조회
    @Override
    public ResponseEntity<CustomApiResponse<?>> getChatContentForPatient(String patientCode, int page, int size) {
        String currentUserId = authenticationUserUtils.getCurrentUserId();

        // 현재 사용자가 환자 권한인지 확인
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isPatient = authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_PATIENT"));
        if (!isPatient) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(CustomApiResponse.createFailWithout(403, "접근 권한이 없습니다."));
        }

        // 현재 사용자가 조회하려는 환자와 동일한지 확인
        Patient patient = patientRepository.findByPatientCode(patientCode)
                .orElse(null);
        if (patient == null || !patient.getPatientCode().equals(currentUserId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(CustomApiResponse.createFailWithout(403, "자신의 대화 내용만 조회할 수 있습니다."));
        }

        // 페이징 처리 후 대화 내용 조회
        Page<Chat> chatPage = chatRepository.findByPatient_PatientCode(patientCode, PageRequest.of(page - 1, size));
        if (chatPage.hasContent()) {
            List<ChatDto> chatList = chatPage.stream()
                    .map(chat -> new ChatDto(
                            chat.getChatId(),
                            chat.isChatIsSend() ? 1 : 0,
                            chat.getChatContent(),
                            chat.getCreatedAt()
                    ))
                    .collect(Collectors.toList());

            return ResponseEntity.ok(CustomApiResponse.createSuccess(200, chatList, "대화 내용 조회에 성공했습니다."));
        }

        // 대화 내용이 존재하지 않는 경우
        return ResponseEntity.ok(CustomApiResponse.createSuccess(200, null, "대화 내용 조회에 성공했습니다. 대화 내용이 존재하지 않습니다."));
    }

    // 환자가 채팅 메세지 전송
    @Override
    public ResponseEntity<CustomApiResponse<?>> processChatMessage(ChatMessageRequestDto chatMessageRequestDto) {
        String currentUserId = authenticationUserUtils.getCurrentUserId();
//        System.out.println("currentUserId: "+currentUserId); -> currentUserId: LAH8OP2C

        // 401 : 환자 정보 찾을 수 없음
        Patient patient = patientRepository.findByPatientCode(currentUserId)
                .orElseThrow(() -> new RuntimeException("유효하지 않은 토큰이거나, 해당 ID에 해당하는 환자가 존재하지 않습니다."));


        String sendContent = chatMessageRequestDto.getChatContent();

        // 환자 Chat save
        Chat newPatientChat = Chat.addChat(sendContent, patient, true);
        chatRepository.save(newPatientChat);

//        // FastAPI 통신
//        ChatMessageFastApiRequestDto.builder()
//                .disease(patient.getPyeoningDisease())
//                .newChat(content)
//                .chatHistory()
//                .build();

        // 502 : AI 서버와의 통신 실패

        // 504 : AI 서버 시간초과

        // FastAPI 통신 성공
        String receivedContent = "응답응답";

        // 펴닝 응답 Chat save
        Chat newPyeoningChat = Chat.addChat(receivedContent, patient, false);
        chatRepository.save(newPyeoningChat);

        // data 가공
        ChatMessageResponseDto data = ChatMessageResponseDto.builder()
                .chatId(newPyeoningChat.getChatId())
                .chatContent(newPyeoningChat.getChatContent())
                .createdAt(newPyeoningChat.localDateTimeToString())
                .build();

        // 201 : 메세지 전송 및 AI 응답 생성에 성공
        return ResponseEntity.ok(CustomApiResponse.createSuccess(201, data, "메세지 전송 및 AI 응답 생성에 성공했습니다."));
    }
}
