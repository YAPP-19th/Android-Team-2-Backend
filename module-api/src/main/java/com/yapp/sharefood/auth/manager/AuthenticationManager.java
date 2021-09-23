package com.yapp.sharefood.auth.manager;

import com.yapp.sharefood.external.AuthProvider;
import com.yapp.sharefood.external.OAuthProfile;
import com.yapp.sharefood.user.OAuthType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;


@Component
@RequiredArgsConstructor
public class AuthenticationManager {
    private final Map<String, AuthProvider> authProviderMap;

    public OAuthProfile requestOAuthUserInfo(OAuthType oAuthType, String accessToken) {
        AuthProvider authProvider = authProviderMap.get(oAuthType.getOAuthProviderName());
        return authProvider.getOAuthProfileInfo(accessToken);
    }
}
