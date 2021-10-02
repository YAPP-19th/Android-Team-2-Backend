package com.yapp.sharefood.auth.dto;

import com.yapp.sharefood.user.domain.OAuthType;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class OAuthDto {
    private Long userId;
    private String token;
    private OAuthType authType;

    private OAuthDto(Long userId, String token, OAuthType oAuthType) {
        this.userId = userId;
        this.token = token;
        this.authType = oAuthType;
    }

    public static OAuthDto of(Long userId, String token, OAuthType oAuthType) {
        return new OAuthDto(userId, token, oAuthType);
    }
}
