package com.hsu.pyeoning.domain.chat.entity;

import com.hsu.pyeoning.domain.patient.entity.Patient;
import com.hsu.pyeoning.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "CHAT")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class Chat extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chat_id")
    private Long chatId; // 채팅 PK

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient; // 환자 FK

    @Column(name = "chat_is_send", nullable = false)
    private boolean chatIsSend; // 수신 및 발신

    @Lob
    @Column(name = "chat_content")
    private String chatContent; // 채팅 내용

    // ======== 편의 메서드 ========
    // 채팅 저장 메서드
    public static Chat addChat(String content, Patient patient, boolean isSend) {
        return Chat.builder()
                .chatContent(content)
                .chatIsSend(isSend)
                .patient(patient)
                .build();
    }

}
