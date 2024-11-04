package com.hsu.pyeoning.domain.chat.repository;

import com.hsu.pyeoning.domain.chat.entity.Chat;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Long> {
    Page<Chat> findByPatient_PatientId(Long patientId, Pageable pageable);
    Page<Chat> findByPatient_PatientCode(String patientCode, Pageable pageable);
}
