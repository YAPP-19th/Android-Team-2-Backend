package com.yapp.sharefood.auth.manager;

import com.yapp.sharefood.external.oauth.AuthProvider;
import com.yapp.sharefood.external.oauth.OAuthProfile;
import com.yapp.sharefood.user.domain.OAuthType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.security.InvalidParameterException;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthenticationManager {
    private final Map<String, AuthProvider> authProviderMap;

    public OAuthProfile requestOAuthUserInfo(OAuthType type, String accessToken) {
        AuthProvider authProvider = extractProviderByType(type);
        return authProvider.getOAuthProfileInfo(accessToken);
    }

    private AuthProvider extractProviderByType(OAuthType type) {
        validateOAuthType(type);
        return authProviderMap.get(type.getOAuthProviderName());
    }

    private void validateOAuthType(OAuthType type) {
        if (Objects.isNull(type)) {
            log.info("현재 제공되지 않는 OAuth type을 사용 : {}", type);
            throw new InvalidParameterException("현재 제공되지 않는 OAuth 형태를 요청하였습니다.");
        }
        String providerName = type.getOAuthProviderName();
        if (!authProviderMap.containsKey(providerName)) {
            log.info("OAuth Provider와 bean과의 이름이 일치 하지 않습니다. input type : {}", providerName);
            throw new InvalidParameterException(String.format("해당 OAuth는 사용자에게 제공되지 않습니다 input : %s", providerName));
        }
    }
}
