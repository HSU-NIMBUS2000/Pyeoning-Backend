package com.hsu.pyeoning.domain.summary.repository;

import com.hsu.pyeoning.domain.patient.entity.Patient;
import com.hsu.pyeoning.domain.summary.entity.Summary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SummaryRepository extends JpaRepository<Summary, Long> {
    // 요약보고서 전체 조회
    List<Summary> findAllByPatient(Patient patient);

}
