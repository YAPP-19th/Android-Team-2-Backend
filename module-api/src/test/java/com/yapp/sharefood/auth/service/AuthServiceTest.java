package com.yapp.sharefood.auth.service;

import com.yapp.sharefood.auth.dto.OAuthDto;
import com.yapp.sharefood.auth.dto.request.AuthCreationRequestDto;
import com.yapp.sharefood.auth.dto.request.AuthRequsetDto;
import com.yapp.sharefood.auth.manager.AuthenticationManager;
import com.yapp.sharefood.auth.token.TokenProvider;
import com.yapp.sharefood.external.exception.BadGatewayException;
import com.yapp.sharefood.external.kakao.dto.KakaoOAuthProfile;
import com.yapp.sharefood.oauth.exception.OAUthExistException;
import com.yapp.sharefood.oauth.exception.UserNotFoundException;
import com.yapp.sharefood.user.domain.OAuthType;
import com.yapp.sharefood.user.domain.User;
import com.yapp.sharefood.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.willReturn;
import static org.mockito.BDDMockito.willThrow;

@Transactional
@SpringBootTest
class AuthServiceTest {
    @Autowired
    AuthService authService;

    @Autowired
    TokenProvider tokenProvider;

    @Autowired
    UserRepository userRepository;

    @MockBean
    AuthenticationManager authenticationManager;

    @Test
    @DisplayName("kakao oauth 로그인 테스트")
    void kakaoAuthenticateTest() {
        // given
        AuthRequsetDto authRequsetDto = new AuthRequsetDto(OAuthType.KAKAO, "accessToken");
        LocalDateTime now = LocalDateTime.now();
        String nickname = "kkh";
        User savedUser = userRepository.save(User.builder()
                .oauthId("kakao_id")
                .nickname(nickname)
                .oAuthType(OAuthType.KAKAO)
                .name("kihwankim")
                .build());

        willReturn(KakaoOAuthProfile.of("kakao_id", now, nickname))
                .given(authenticationManager).requestOAuthUserInfo(any(OAuthType.class), anyString());

        // when
        OAuthDto authenticate = authService.authenticate(authRequsetDto);

        // then
        assertEquals(OAuthType.KAKAO, authenticate.getAuthType());
        assertEquals(tokenProvider.createToken(savedUser), authenticate.getToken());
    }

    @Test
    @DisplayName("kakao auth 요청 실패 케이스")
    void oauthBadRequestTest() throws Exception {
        // given
        AuthRequsetDto authRequsetDto = new AuthRequsetDto(OAuthType.KAKAO, "badAccessToken");
        willThrow(new BadGatewayException("bad gateway"))
                .given(authenticationManager).requestOAuthUserInfo(any(OAuthType.class), anyString());

        // when

        // then
        assertThrows(BadGatewayException.class, () -> authService.authenticate(authRequsetDto));
    }

    @Test
    @DisplayName("사용자 oauth type 이슈")
    void oauthOAuthTypeParameterTest() throws Exception {
        // given
        AuthRequsetDto authRequsetDto = new AuthRequsetDto(OAuthType.KAKAO, "accessToken");
        LocalDateTime now = LocalDateTime.now();
        willReturn(KakaoOAuthProfile.of("kakao_id", now, "kkh"))
                .given(authenticationManager).requestOAuthUserInfo(any(OAuthType.class), anyString());

        // when

        // then
        assertThrows(UserNotFoundException.class, () -> authService.authenticate(authRequsetDto));
    }

    @Test
    @DisplayName("회원 가입 성공")
    void singUpOAuthSuccess() throws Exception {
        // given
        AuthCreationRequestDto authCreationRequestDto = new AuthCreationRequestDto(OAuthType.KAKAO, "kkh", "accessToken");
        LocalDateTime now = LocalDateTime.now();
        willReturn(KakaoOAuthProfile.of("kakao_id", now, "kkh"))
                .given(authenticationManager).requestOAuthUserInfo(any(OAuthType.class), anyString());

        // when
        OAuthDto oAuthDto = authService.singUp(authCreationRequestDto);
        Optional<User> kakaSingUpUser = userRepository.findByOAuthIdAndOAuthType("kakao_id", OAuthType.KAKAO);

        // then
        User realuser = kakaSingUpUser.get();
        assertEquals(OAuthType.KAKAO, oAuthDto.getAuthType());
        assertEquals(realuser.getId(), oAuthDto.getUserId());
        assertEquals(tokenProvider.createToken(realuser), oAuthDto.getToken());
    }

    @Test
    void singUpFailCuzExistUserTest() throws Exception {
        // given
        AuthCreationRequestDto authCreationRequestDto = new AuthCreationRequestDto(OAuthType.KAKAO, "kkh", "accessToken");
        LocalDateTime now = LocalDateTime.now();
        userRepository.save(User.builder()
                .oauthId("kakao_id")
                .nickname("kkh")
                .oAuthType(OAuthType.KAKAO)
                .name("kihwankim")
                .build()); // 등록
        willReturn(KakaoOAuthProfile.of("kakao_id", now, "kkh"))
                .given(authenticationManager).requestOAuthUserInfo(any(OAuthType.class), anyString());

        // when

        // then
        assertThrows(OAUthExistException.class, () -> authService.singUp(authCreationRequestDto));
    }
}