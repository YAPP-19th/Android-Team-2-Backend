package com.yapp.sharefood.auth.service;

import com.yapp.sharefood.auth.dto.OAuthDto;
import com.yapp.sharefood.auth.dto.request.AuthRequsetDto;
import com.yapp.sharefood.auth.manager.AuthenticationManager;
import com.yapp.sharefood.auth.token.TokenProvider;
import com.yapp.sharefood.external.kakao.dto.KakaoOAuthProfile;
import com.yapp.sharefood.user.domain.OAuthType;
import com.yapp.sharefood.user.domain.User;
import com.yapp.sharefood.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.willReturn;

@SpringBootTest
class AuthServiceTest {
    @Autowired
    AuthService authService;

    @Autowired
    TokenProvider tokenProvider;

    @MockBean
    AuthenticationManager authenticationManager;

    @MockBean
    UserRepository userRepository;

    @Test
    @DisplayName("kakao oauth 로그인 테스트")
    void kakaoAuthenticateTest() {
        // given
        String accessToken = "accessToken";
        AuthRequsetDto authRequsetDto = new AuthRequsetDto(OAuthType.KAKAO, accessToken);
        String oauthId = "kakao_id";
        LocalDateTime now = LocalDateTime.now();
        String nickname = "kkh";

        willReturn(
                Optional.of(User.builder()
                        .id(1L)
                        .nickname(nickname)
                        .oAuthType(OAuthType.KAKAO)
                        .name("kihwankim")
                        .build())
        )
                .given(userRepository).findByOAuthIdAndOAuthType(oauthId, OAuthType.KAKAO);

        willReturn(KakaoOAuthProfile.of(oauthId, now, nickname))
                .given(authenticationManager).requestOAuthUserInfo(OAuthType.KAKAO, accessToken);

        // when
        OAuthDto authenticate = authService.authenticate(authRequsetDto);
        User mockUser = userRepository.findByOAuthIdAndOAuthType(oauthId, OAuthType.KAKAO).get();

        // then
        assertEquals(authenticate.getAuthType(), OAuthType.KAKAO);
        assertEquals(authenticate.getToken(), tokenProvider.createToken(mockUser));
    }
}