package com.hsu.pyeoning.domain.chat.repository;

import com.hsu.pyeoning.domain.chat.entity.Chat;
import com.hsu.pyeoning.domain.patient.entity.Patient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Long> {
    Page<Chat> findByPatient_PatientId(Long patientId, Pageable pageable);
    Page<Chat> findByPatient_PatientCode(String patientCode, Pageable pageable);
    // 환자의 가장 최근 메시지 가져오기
    Optional<Chat> findTopByPatientOrderByCreatedAtDesc(Patient patient);
}
