package com.hsu.pyeoning.domain.summary.web.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SummaryDto {
    private Long summaryId;
    private String summaryContent;
    private LocalDateTime createdAt;
}
