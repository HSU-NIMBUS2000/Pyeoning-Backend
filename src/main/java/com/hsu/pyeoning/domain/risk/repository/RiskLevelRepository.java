package com.hsu.pyeoning.domain.risk.repository;

import com.hsu.pyeoning.domain.patient.entity.Patient;
import com.hsu.pyeoning.domain.risk.entity.RiskLevel;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RiskLevelRepository extends JpaRepository<RiskLevel, Long> {
    List<RiskLevel> findTop10ByPatientOrderByCreatedAtDesc(Patient patient);
    Optional<RiskLevel> findTopByPatientOrderByCreatedAtDesc(Patient patient);
}