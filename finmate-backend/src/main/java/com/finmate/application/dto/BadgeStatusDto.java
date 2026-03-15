package com.finmate.application.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class BadgeStatusDto {
    private String id;
    private String name;
    private String description;
    private String icon;
    private boolean unlocked;
    private LocalDateTime unlockedAt;
}
