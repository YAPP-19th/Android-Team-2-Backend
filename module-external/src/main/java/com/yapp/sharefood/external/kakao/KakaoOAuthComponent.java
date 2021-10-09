package com.yapp.sharefood.external.kakao;

import com.yapp.sharefood.external.OAuthComponent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class KakaoOAuthComponent implements OAuthComponent {
    @Value("${oauth.kakao.profile-url}")
    private String kakaoOAuthUrl;

    @Override
    public String oauthUrl() {
        return this.kakaoOAuthUrl;
    }
}
