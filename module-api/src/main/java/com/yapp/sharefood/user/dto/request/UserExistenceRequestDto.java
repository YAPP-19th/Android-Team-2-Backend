package com.yapp.sharefood.user.dto.request;

import com.yapp.sharefood.user.domain.OAuthType;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class UserExistenceRequestDto {
    @NotNull
    @NotBlank
    private String oauthId;

    @NotNull
    private OAuthType type;
}
