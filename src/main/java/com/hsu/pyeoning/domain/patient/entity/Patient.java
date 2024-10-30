package com.hsu.pyeoning.domain.patient.entity;

import com.hsu.pyeoning.domain.doctor.entity.Doctor;
import com.hsu.pyeoning.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Table(name = "PATIENT")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class Patient extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "patient_id")
    private Long patientId; // 환자 PK

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctorId; // 환자 담당 의사

    @Column(name = "patient_code", nullable = false)
    private String patientCode; // 환자 접속 코드

    @Column(name = "patient_email", nullable = false)
    private String patientEmail; // 환자 이메일

    @Column(name = "patient_name", nullable = false)
    private String patientName; // 환자 이름

    @Column(name = "patient_gender", nullable = false)
    private Gender patientGender; // 환자 성별

    @Column(name = "patient_birth", nullable = false)
    private Date patientBirth; // 환자 생년월일
}
