package com.yapp.sharefood.user.domain;

import lombok.Getter;

@Getter
public enum OAuthType {
    KAKAO("kakaoAuthStrategy");

    private final String oAuthProviderName;

    OAuthType(String type) {
        this.oAuthProviderName = type;
    }
}