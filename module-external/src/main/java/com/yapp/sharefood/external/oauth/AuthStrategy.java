package com.yapp.sharefood.external.oauth;

public interface AuthStrategy {
    OAuthProfile getOAuthProfileInfo(String accessToken);
}
