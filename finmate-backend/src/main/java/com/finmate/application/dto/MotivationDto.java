package com.finmate.application.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MotivationDto {

    private String message;
    private int currentStreak;
    private int longestStreak;
    private int financialScore;
    private String scoreLabel;
    private int averageGoalProgress;
    private int activeGoals;
}
