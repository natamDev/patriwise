package com.finmate.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GoalContribution {
    private UUID id;
    private UUID goalId;
    private BigDecimal amount;
    private LocalDate contributionDate;
}
