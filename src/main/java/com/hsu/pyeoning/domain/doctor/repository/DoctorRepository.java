package com.hsu.pyeoning.domain.doctor.repository;

import com.hsu.pyeoning.domain.doctor.entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {
}
