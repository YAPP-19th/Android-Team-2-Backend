package com.yapp.sharefood.external.oauth;

public interface AuthProvider {
    OAuthProfile getOAuthProfileInfo(String accessToken);
}
