package com.finmate.application.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class RiskEducationDto {

    private String topic;
    private String topicLabel;
    private String definition;
    private String example;
    private String historicalExample;
    private String howToReact;
    private String simpleSummary;
    private List<String> keyPoints;
}
