package com.yapp.sharefood.auth.manager;

import com.yapp.sharefood.external.oauth.AuthStrategy;
import com.yapp.sharefood.external.oauth.OAuthProfile;
import com.yapp.sharefood.external.oauth.kakao.KakaoAuthStrategy;
import com.yapp.sharefood.external.oauth.kakao.dto.KakaoOAuthProfile;
import com.yapp.sharefood.user.domain.OAuthType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.security.InvalidParameterException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class AuthenticationManagerTest {
    AuthenticationManager authenticationManager;

    @Mock
    KakaoAuthStrategy kakaoAuthStrategy;

    @BeforeEach
    void setUp() {
        final Map<String, AuthStrategy> strategy = new HashMap<>();
        strategy.put("kakaoAuthStrategy", kakaoAuthStrategy);
        authenticationManager = new AuthenticationManager(strategy);
    }

    @Test
    @DisplayName("oauth 정보 잘 가져오는 경우")
    void oauthUserInfoExtractTest() throws Exception {
        // given
        given(kakaoAuthStrategy.getOAuthProfileInfo(anyString()))
                .willReturn(KakaoOAuthProfile.of("kakao_socail", LocalDateTime.now(), "kkh_nickname"));

        // when
        OAuthProfile profile = authenticationManager.requestOAuthUserInfo(OAuthType.KAKAO, "accessToken");

        // then
        assertEquals("kakao_socail", profile.getOauthId());
        assertEquals("kkh_nickname", profile.oauthNickname());
    }

    @Test
    @DisplayName("oauth type이 null이거나 다른 값일 때")
    void invalidOauthTypeTest() throws Exception {
        // given

        // when

        // then
        assertThrows(InvalidParameterException.class, () -> authenticationManager.requestOAuthUserInfo(null, "accessToken"));
    }
}