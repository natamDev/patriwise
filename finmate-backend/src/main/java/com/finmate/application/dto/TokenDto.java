package com.finmate.application.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TokenDto {

    private String token;

    public TokenDto(String token) {
        this.token = token;
    }
}
