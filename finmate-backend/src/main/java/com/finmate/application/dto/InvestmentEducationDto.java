package com.finmate.application.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class InvestmentEducationDto {

    private String topic;
    private String topicLabel;
    private String definition;
    private String example;
    private String risk;
    private String simpleSummary;
    private List<String> keyPoints;
}
