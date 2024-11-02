package com.hsu.pyeoning.domain.summary.entity;

import com.hsu.pyeoning.domain.patient.entity.Patient;
import com.hsu.pyeoning.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "SUMMARY")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class Summary extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "summary_id")
    private Long summaryId; // 요약보고서 PK

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient; // 환자 FK

    @Lob
    @Column(name = "summary_content")
    private String summaryContent; // 요약보고서 내용

}
