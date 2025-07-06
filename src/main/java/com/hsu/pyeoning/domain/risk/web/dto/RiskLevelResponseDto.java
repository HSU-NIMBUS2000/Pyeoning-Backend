package com.hsu.pyeoning.domain.risk.web.dto;

import lombok.*;

@Getter
@Builder
public class RiskLevelResponseDto {
    private Integer riskLevel;
    private String createdAt;
    private String description;
}
