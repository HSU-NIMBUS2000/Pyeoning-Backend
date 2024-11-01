package com.hsu.pyeoning.domain.patient.web.dto;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class ModifyPromptDto {
    private String patientName;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date patientBirth;
    
    private String pyeoningDisease;
    private String pyeoningPrompt;
    private String pyeoningSpecial;
}
