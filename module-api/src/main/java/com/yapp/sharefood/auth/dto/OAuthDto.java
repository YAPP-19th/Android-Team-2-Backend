package com.yapp.sharefood.auth.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class OAuthDto {
    private String token;

    public OAuthDto(String token) {
        this.token = token;
    }
}
