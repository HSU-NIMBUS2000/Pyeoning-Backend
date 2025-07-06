package com.hsu.pyeoning.domain.chat.repository;

import com.hsu.pyeoning.domain.chat.entity.Chat;
import com.hsu.pyeoning.domain.patient.entity.Patient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Long> {
    Page<Chat> findByPatient_PatientId(Long patientId, Pageable pageable);
    Page<Chat> findByPatient_PatientCode(String patientCode, Pageable pageable);

    // 환자의 가장 최근 메시지 가져오기
    Optional<Chat> findTopByPatientOrderByCreatedAtDesc(Patient patient);

    // 해당 환자의 가장 최신 session_end=true 부터 그 직전 session_end=true인 채팅 시간순 반환
    @Query("""
       SELECT c FROM Chat c
       WHERE c.patient.id = :patientId
       AND c.createdAt > (
           SELECT c1.createdAt
           FROM Chat c1
           WHERE c1.patient.id = :patientId
           AND c1.sessionEnd = true
           ORDER BY c1.createdAt DESC
           OFFSET 1 ROW FETCH FIRST 1 ROW ONLY
       )
       ORDER BY c.createdAt ASC
       """)
    List<Chat> findChatHistoryBetweenSessions(@Param("patientId") Long patientId);

    // 해당 환자의 최신 chat 레코드의 session_end 상태 가져오기
    @Query("SELECT c.sessionEnd FROM Chat c WHERE c.patient.id = :patientId ORDER BY c.createdAt DESC LIMIT 1")
    Boolean isLatestChatSessionEnded(@Param("patientId") Long patientId);
}
