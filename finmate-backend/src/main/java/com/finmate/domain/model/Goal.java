package com.finmate.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Goal {

    private UUID id;
    private UUID userId;
    private String goalName;
    private GoalType goalType;
    private BigDecimal targetAmount;
    private LocalDate targetDate;
    private BigDecimal monthlyContribution;
    private LocalDateTime createdAt;

    public enum GoalType {
        TRAVEL, EMERGENCY_FUND, INVESTMENT, PURCHASE, OTHER
    }
}
