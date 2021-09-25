package com.yapp.sharefood.auth.dto.request;

import com.yapp.sharefood.user.domain.OAuthType;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AuthCreationRequestDto {
    @NotNull
    private OAuthType authType;
    @NotNull
    private String nickName;
    @NotBlank
    private String accessToken;
}
