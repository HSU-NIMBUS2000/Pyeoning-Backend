package com.hsu.pyeoning.domain.summary.web.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ChatSummaryResponseDto {
    private Long summaryId;
    private String summaryContent;
    private String createdAt; // ex. 2024.11.06
}