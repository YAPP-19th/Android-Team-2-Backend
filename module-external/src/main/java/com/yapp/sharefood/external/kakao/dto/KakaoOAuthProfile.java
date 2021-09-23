package com.yapp.sharefood.external.kakao.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.yapp.sharefood.external.OAuthProfile;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class KakaoOAuthProfile implements OAuthProfile {
    private String id;
    private LocalDateTime connectedAt;
    private Properties properties;

    @Getter
    private static class Properties {
        private String nickname;
    }

    @Getter
    private static class KakaoAccount {
        private boolean profileNicknameNeedsAgreement;
        private Profile profile;
    }

    @Getter
    public static class Profile {
        private String nickname;
    }
}
