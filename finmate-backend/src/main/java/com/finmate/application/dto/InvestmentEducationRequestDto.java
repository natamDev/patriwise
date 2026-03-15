package com.finmate.application.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class InvestmentEducationRequestDto {

    @NotBlank
    private String topic;
}
