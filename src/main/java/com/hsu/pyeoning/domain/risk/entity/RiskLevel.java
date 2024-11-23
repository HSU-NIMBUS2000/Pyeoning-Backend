package com.hsu.pyeoning.domain.risk.entity;

import com.hsu.pyeoning.domain.patient.entity.Patient;
import com.hsu.pyeoning.global.entity.BaseEntity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "RISK_LEVEL")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class RiskLevel extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "risk_level_id")
    private Long riskLevelId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @Column(nullable = false)
    private Integer riskLevel; // 1~5 단계

    @Column(nullable = false)  
    private String description; // 위험도 판단 근거나 설명
}
