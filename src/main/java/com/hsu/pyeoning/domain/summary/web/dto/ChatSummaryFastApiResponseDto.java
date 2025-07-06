package com.hsu.pyeoning.domain.summary.web.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper=false) // warning 해결 : 상위 클래스 필드 제외
@Data
public class ChatSummaryFastApiResponseDto {
    private int status;
    private ChatSummaryFastApiResponseDto.DataContent data;
    private String message;

    @Data
    public static class DataContent {
        private String summary;
        private int riskLevel;        // 위험도 (1-5)
        private String riskReason;    // 위험도 평가 근거
    }
}
