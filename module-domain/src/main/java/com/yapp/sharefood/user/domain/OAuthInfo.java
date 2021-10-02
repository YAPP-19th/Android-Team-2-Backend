package com.yapp.sharefood.user.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OAuthInfo {
    @Column(nullable = false, length = 200)
    private String oauthId;

    @Column(nullable = false, length = 80)
    private String name;

    @Column(nullable = false, length = 30)
    @Enumerated(EnumType.STRING)
    private OAuthType oauthType;

    private OAuthInfo(String oauthId, String name, OAuthType oauthType) {
        this.oauthId = oauthId;
        this.name = name;
        this.oauthType = oauthType;
    }

    public static OAuthInfo of(String oauthId, String name, OAuthType oAuthType) {
        return new OAuthInfo(oauthId, name, oAuthType);
    }
}
