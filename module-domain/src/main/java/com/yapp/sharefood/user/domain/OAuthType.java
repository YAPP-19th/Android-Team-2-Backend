package com.yapp.sharefood.user.domain;

import com.yapp.sharefood.oauth.exception.OAuthTypeNotFoundException;
import lombok.Getter;

import java.util.Arrays;

@Getter
public enum OAuthType {
    KAKAO("kakaoAuthProvider");

    private final String oAuthProviderName;

    OAuthType(String type) {
        this.oAuthProviderName = type;
    }

    public static OAuthType findByTypeName(String oAuthTypeName) {
        return Arrays.stream(OAuthType.values())
                .filter(oauth -> oauth.name().equals(oAuthTypeName.toUpperCase()))
                .findFirst()
                .orElseThrow(OAuthTypeNotFoundException::new);
    }
}