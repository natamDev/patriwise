package com.finmate.application.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SavingStreakDto {
    private int currentStreak;
    private int longestStreak;
}
