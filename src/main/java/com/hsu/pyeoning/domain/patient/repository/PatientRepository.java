package com.hsu.pyeoning.domain.patient.repository;

import com.hsu.pyeoning.domain.patient.entity.Patient;
import com.hsu.pyeoning.domain.doctor.entity.Doctor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {
    Optional<Patient> findByPatientCode(String patientCode);
    Page<Patient> findByDoctorId(Doctor doctor, Pageable pageable);
    Page<Patient> findByDoctorIdAndPyeoningDiseaseContaining(Doctor doctor, String disease, Pageable pageable);
    Page<Patient> findByDoctorIdAndPatientNameContaining(Doctor doctor, String name, Pageable pageable);
    Page<Patient> findByDoctorIdAndPyeoningSpecialContaining(Doctor doctor, String special, Pageable pageable);
}
