package com.finmate.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SavingStreak {
    private UUID userId;
    private int currentStreak;
    private int longestStreak;
}
