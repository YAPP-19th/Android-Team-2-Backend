package com.yapp.sharefood.external;

public interface AuthProvider {
    OAuthProfile getOAuthProfileInfo(String accessToken);
}
