package com.yapp.sharefood.auth.dto.request;

import com.yapp.sharefood.user.domain.OAuthType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AuthRequestDto {
    @NotNull
    private OAuthType oauthType;
    @NotBlank
    private String accessToken;
}
