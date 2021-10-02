package com.yapp.sharefood.external.kakao.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.yapp.sharefood.external.OAuthProfile;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class KakaoOAuthProfile implements OAuthProfile {
    private String id;
    private LocalDateTime connectedAt;
    private Properties properties;
    private KakaoAccount kakaoAccount;

    @Override
    public String getOauthId() {
        return this.id;
    }

    @Override
    public String oauthNickname() {
        return this.properties.getNickname();
    }

    public static KakaoOAuthProfile of(String socialId, LocalDateTime connectedAt, String nickname) {
        return new KakaoOAuthProfile(socialId, connectedAt,
                new Properties(nickname),
                new KakaoAccount(true, new Profile(nickname)));
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    private static class Properties {
        private String nickname;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    private static class KakaoAccount {
        private boolean profileNicknameNeedsAgreement;
        private Profile profile;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    private static class Profile {
        private String nickname;
    }
}
