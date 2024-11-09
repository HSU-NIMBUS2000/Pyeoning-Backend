package com.hsu.pyeoning.domain.chat.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hsu.pyeoning.domain.chat.entity.Chat;
import com.hsu.pyeoning.domain.chat.repository.ChatRepository;
import com.hsu.pyeoning.domain.chat.web.dto.*;
import com.hsu.pyeoning.domain.patient.entity.Patient;
import com.hsu.pyeoning.domain.patient.repository.PatientRepository;
import com.hsu.pyeoning.global.response.CustomApiResponse;
import com.hsu.pyeoning.global.security.jwt.util.AuthenticationUserUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.beans.factory.annotation.Value;import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {
    @Value("${fastapi.url}")
    private String fastApiUrl;

    private final ChatRepository chatRepository;
    private final PatientRepository patientRepository;
    private final AuthenticationUserUtils authenticationUserUtils;
    private final RestTemplate restTemplate;

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
    @Transactional
    @Override
    public ResponseEntity<CustomApiResponse<?>> processChatMessage(ChatMessageRequestDto chatMessageRequestDto) {
        // patientCode 가져오기
        String patientCode = authenticationUserUtils.getCurrentUserId(); // patientCode를 반환 ex. LAH8OP2C

        // 401 : 환자 정보 찾을 수 없음
        Patient patient = patientRepository.findByPatientCode(patientCode)
                .orElseThrow(() -> new RuntimeException("유효하지 않은 토큰이거나, 해당 ID에 해당하는 환자가 존재하지 않습니다."));

        String sendContent = chatMessageRequestDto.getChatContent();

        // 환자 Chat 저장
        Chat newPatientChat = Chat.addChat(sendContent, patient, true);
        chatRepository.save(newPatientChat);

        // 환자의 해당 세션동안 대화한 채팅 기록 가져오기
        List<Chat> chats = chatRepository.findChatHistoryBetweenSessions(patient.getPatientId());

        // FastAPI - request Dto 빌드
        ChatMessageFastApiRequestDto requestDto = ChatMessageFastApiRequestDto.builder()
                .disease(patient.getPyeoningDisease())
                .newChat(sendContent)
                .chats(chats)
                .prompt(patient.getPyeoningPrompt())
                .build();
//        System.out.println("requestDto : " + requestDto);

        // FastAPI - 엔드포인트 설정
        String fastApiEndpoint = fastApiUrl + "/api/doctor-ai/chatbot";
        String receivedContent = null;

        try {
            // FastAPI - 서버에 POST 요청 전송
            ResponseEntity<String> response = restTemplate.postForEntity(fastApiEndpoint, requestDto, String.class);
//            System.out.println("responseDto: " + response);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                // FastAPI - 응답 JSON 파싱
                ObjectMapper objectMapper = new ObjectMapper();
                ChatMessageFastApiResponseDto responseDto = objectMapper.readValue(response.getBody(), ChatMessageFastApiResponseDto.class);
                receivedContent = responseDto.getData().getNewChat(); // newChat 값 가져오기
            } else {
                // 502 : 응답 상태 코드가 성공 범위가 아남
                return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                        .body(CustomApiResponse.createFailWithout(502, "AI 서버와의 통신에 실패했습니다. 응답 코드가 성공 범위가 아닙니다."));
            }
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            // 502 : AI 서버와의 통신 실패
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                    .body(CustomApiResponse.createFailWithout(502, "AI 서버와의 통신 중 오류 발생: " + e.getMessage()));
        } catch (ResourceAccessException e) {
            // 504 : AI 서버 시간 초과
            return ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT)
                    .body(CustomApiResponse.createFailWithout(504, "AI 서버 응답 시간이 초과되었습니다. 잠시 후 다시 시도해 주세요."));
        } catch (Exception e) {
            // 500 : JSON 파싱 오류 발생 등 기타 예외
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.createFailWithout(500, "응답 파싱 중 오류가 발생했습니다. 잠시 후 다시 시도해 주세요."));
        }

        // 펴닝 응답 Chat 저장
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

    // 세션 종료
    @Override
    @Transactional
    public ResponseEntity<CustomApiResponse<?>> endSessionForPatient(SessionEndRequestDto sessionEndRequestDto) {
        // patientCode 가져오기
        String patientCode = authenticationUserUtils.getCurrentUserId();

        // 401 : 환자 정보 찾을 수 없음
        Patient patient = patientRepository.findByPatientCode(patientCode)
                .orElseThrow(() -> new RuntimeException("유효하지 않은 토큰이거나, 해당 ID에 해당하는 환자가 존재하지 않습니다."));

        // 환자의 마지막 채팅 메시지 가져오기
        Chat lastChat = chatRepository.findTopByPatientOrderByCreatedAtDesc(patient)
                .orElseThrow(() -> new RuntimeException("채팅 기록이 없습니다."));

        // 세션 종료 플래그 설정
        lastChat.setSessionEnd(true);
        chatRepository.save(lastChat);

        // 200 : 세션 종료 성공
        return ResponseEntity.ok(CustomApiResponse.createSuccess(200, null, "세션이 성공적으로 종료되었습니다."));
    }
}
