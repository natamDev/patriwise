package com.finmate.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Quiz {
    private UUID id;
    private String question;
    private List<String> options; // 4 options
    private int correctAnswer;    // index 0-3
    private String explanation;
}
