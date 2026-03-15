package com.finmate.application.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RiskEducationRequestDto {

    @NotBlank
    private String topic;
}
