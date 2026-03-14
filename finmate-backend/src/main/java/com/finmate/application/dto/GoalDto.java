package com.finmate.application.dto;

import com.finmate.domain.model.Goal.GoalType;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
public class GoalDto {
    private UUID id;
    private String goalName;
    private GoalType goalType;
    private BigDecimal targetAmount;
    private LocalDate targetDate;
    private BigDecimal monthlyContribution;
    private LocalDateTime createdAt;
}
