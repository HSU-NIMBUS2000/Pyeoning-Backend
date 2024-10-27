package com.hsu.pyeoning.domain.patient.repository;

import com.hsu.pyeoning.domain.patient.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {
}
