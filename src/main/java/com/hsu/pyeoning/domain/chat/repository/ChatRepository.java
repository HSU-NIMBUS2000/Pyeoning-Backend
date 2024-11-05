package com.hsu.pyeoning.domain.chat.repository;

import com.hsu.pyeoning.domain.chat.entity.Chat;
import com.hsu.pyeoning.domain.patient.entity.Patient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Long> {
    Page<Chat> findByPatient_PatientId(Long patientId, Pageable pageable);
    Page<Chat> findByPatient_PatientCode(String patientCode, Pageable pageable);
    // 환자의 가장 최근 메시지 가져오기
    Optional<Chat> findTopByPatientOrderByCreatedAtDesc(Patient patient);
    // 가장 최신에 session_end가 true로 설정된 채팅부터 그 전 session_end가 true인 채팅까지 가져오기
    // ChatRepository.java

    @Query("SELECT c FROM Chat c WHERE c.patient = :patient AND c.createdAt > " +
            "(SELECT MAX(c2.createdAt) FROM Chat c2 WHERE c2.patient = :patient AND c2.sessionEnd = true) " +
            "ORDER BY c.createdAt ASC")
    List<Chat> findChatHistoryBetweenSessions(Patient patient);
}
