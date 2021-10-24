package com.yapp.sharefood.auth.manager;

import com.yapp.sharefood.external.oauth.OAuthProfile;
import com.yapp.sharefood.external.oauth.kakao.KakaoAuthProvider;
import com.yapp.sharefood.external.oauth.kakao.dto.KakaoOAuthProfile;
import com.yapp.sharefood.user.domain.OAuthType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.security.InvalidParameterException;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.willReturn;

@SpringBootTest
class AuthenticationManagerTest {
    @MockBean
    KakaoAuthProvider kakaoAuthProvider;

    @Autowired
    AuthenticationManager authenticationManager;


    @Test
    @DisplayName("oauth 정보 잘 가져오는 경우")
    void oauthUserInfoExtractTest() throws Exception {
        // given
        willReturn(KakaoOAuthProfile.of("kakao_socail", LocalDateTime.now(), "kkh_nickname"))
                .given(kakaoAuthProvider).getOAuthProfileInfo(anyString());

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
        willReturn(KakaoOAuthProfile.of("kakao_socail", LocalDateTime.now(), "kkh_nickname"))
                .given(kakaoAuthProvider).getOAuthProfileInfo(anyString());

        // when

        // then
        assertThrows(InvalidParameterException.class, () -> authenticationManager.requestOAuthUserInfo(null, "accessToken"));
    }
}