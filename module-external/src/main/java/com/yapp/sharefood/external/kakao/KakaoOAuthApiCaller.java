package com.yapp.sharefood.external.kakao;

import com.yapp.sharefood.external.AuthProvider;
import com.yapp.sharefood.external.OAuthResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KakaoOAuthApiCaller implements AuthProvider {
    @Value("{oauth.kakao.url}")
    private String kakaoOAuthUrl;

    @Override
    public OAuthResponseDto getOAuthProfileInfo(String accessToken) {
        return null;
    }
}
