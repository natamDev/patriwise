package com.finmate.application.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
public class GoalProgressDto {
    private UUID goalId;
    private BigDecimal savedAmount;
    private BigDecimal targetAmount;
    private BigDecimal remainingAmount;
    private int percent;
    private Integer monthsNeeded;
    private LocalDate estimatedCompletionDate;
}
