package com.hsu.pyeoning.domain.summary.web.dto;

import com.hsu.pyeoning.global.api.dto.ChatHistory;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper=false) // warning 해결 : 상위 클래스 필드 제외
@Data
public class ChatSummaryFastApiResponseDto {
    private String summary;
}
