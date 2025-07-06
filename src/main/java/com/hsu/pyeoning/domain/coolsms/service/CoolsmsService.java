package com.hsu.pyeoning.domain.coolsms.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.response.SingleMessageSentResponse;
import net.nurigo.sdk.message.service.DefaultMessageService;


@Service
public class CoolsmsService {

    private final DefaultMessageService messageService;

    @Value("${coolsms.api.key}")
    private String apiKey;

    @Value("${coolsms.api.secret}")
    private String apiSecret;

    @Value("${coolsms.phoneNumber}")
    private String fromNumber;

    @Value("${coolsms.phoneNumber.fileId}")
    private String fileId;


    public CoolsmsService(@Value("${coolsms.api.key}") String apiKey,
                          @Value("${coolsms.api.secret}") String apiSecret) {
        this.messageService = NurigoApp.INSTANCE.initialize(apiKey, apiSecret, "https://api.coolsms.co.kr");
    }


    public void sendSms(String doctorPhone, String patientName, String patientDisease, String description) {
        // Message 객체 생성
        Message message = new Message();
        message.setFrom(fromNumber); // 발신번호 (하이픈 제외한 String 값)
        message.setTo(doctorPhone); // 수신번호 (하이픈 제외한 String 값)
        message.setText(
                "[펴닝 경고 알림]\n\n" +
                        "환자 이름: " + patientName + " 님\n" +
                        "위험 상황: 최고 위험 등급 (Level 5)\n" +
                        "질환: " + patientDisease + "\n" +
//                        "상황 설명: " + description + "\n" +
                        "\n환자의 채팅 분석 결과 심각한 위험 신호가 감지되었습니다. 즉시 펴닝 서비스에 접속하여 요약 보고서를 확인해주세요.\n");
        message.setImageId(fileId); // 이미지 추가

        // SingleMessageSendingRequest로 감싸기
        SingleMessageSendingRequest request = new SingleMessageSendingRequest(message);

        try {
            SingleMessageSentResponse response = messageService.sendOne(request);
            System.out.println("SMS 전송 성공: " + response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
