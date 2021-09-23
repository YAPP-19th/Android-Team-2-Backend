package com.yapp.sharefood.external;

public interface AuthProvider {
    OAuthResponseDto getOAuthProfileInfo(String accessToken);
}
