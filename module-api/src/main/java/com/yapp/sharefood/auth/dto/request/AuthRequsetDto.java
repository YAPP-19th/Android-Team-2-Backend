package com.yapp.sharefood.auth.dto.request;

import com.yapp.sharefood.user.OAuthType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AuthRequsetDto {
    @NotNull
    private OAuthType oAuthType;
    @NotBlank
    private String code;
    private String state;
}
