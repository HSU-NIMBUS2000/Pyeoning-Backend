package com.hsu.pyeoning.domain.doctor.entity;

import com.hsu.pyeoning.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "DOCTOR")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class Doctor extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "doctor_id")
    private Long doctorId; // 의사 PK

    @Column(name = "doctor_name", nullable = false)
    private String doctorName; // 의사 이름

    @Column(name = "doctor_hospital", nullable = false)
    private String doctorHospital; // 의사 소속 병원

    @Column(name = "doctor_license", nullable = false)
    private Long doctorLicense; // 의사 면허 번호

    @Column(name = "doctor_email", nullable = false)
    private String doctorEmail; // 의사 이메일

    @Column(name = "doctor_password", nullable = false)
    private String doctorPassword; // 의사 비밀번호
}
