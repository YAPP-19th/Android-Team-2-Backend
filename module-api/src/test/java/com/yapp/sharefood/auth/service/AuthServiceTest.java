package com.yapp.sharefood.auth.service;

import com.yapp.sharefood.auth.dto.OAuthDto;
import com.yapp.sharefood.auth.dto.request.AuthCreationRequestDto;
import com.yapp.sharefood.auth.dto.request.AuthRequestDto;
import com.yapp.sharefood.common.service.IntegrationService;
import com.yapp.sharefood.external.exception.BadGatewayException;
import com.yapp.sharefood.external.oauth.kakao.dto.KakaoOAuthProfile;
import com.yapp.sharefood.oauth.exception.OAUthExistException;
import com.yapp.sharefood.oauth.exception.UserNotFoundException;
import com.yapp.sharefood.user.domain.OAuthType;
import com.yapp.sharefood.user.domain.User;
import com.yapp.sharefood.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.willReturn;
import static org.mockito.BDDMockito.willThrow;

@Transactional
class AuthServiceTest extends IntegrationService {
    @Autowired
    AuthService authService;
    @Autowired
    UserRepository userRepository;

    @Test
    @DisplayName("kakao oauth 로그인 테스트")
    void kakaoAuthenticateTest() {
        // given
        AuthRequestDto authRequestDto = new AuthRequestDto(OAuthType.KAKAO, "accessToken");
        LocalDateTime now = LocalDateTime.now();
        String nickname = "kkh";
        userRepository.save(User.builder()
                .oauthId("kakao_id")
                .nickname(nickname)
                .oAuthType(OAuthType.KAKAO)
                .name("kihwankim")
                .build());
        willReturn(KakaoOAuthProfile.of("kakao_id", now, nickname))
                .given(authenticationManager).requestOAuthUserInfo(any(OAuthType.class), anyString());
        willReturn("mock_token_data")
                .given(tokenProvider).createToken(any(User.class));

        entityManager.flush();
        entityManager.clear();

        // when
        OAuthDto authenticate = authService.authenticate(authRequestDto);

        // then
        assertEquals(OAuthType.KAKAO, authenticate.getAuthType());
        assertEquals("mock_token_data", authenticate.getToken());
    }

    @Test
    @DisplayName("kakao auth 요청 실패 케이스")
    void oauthBadRequestTest() throws Exception {
        // given
        AuthRequestDto authRequestDto = new AuthRequestDto(OAuthType.KAKAO, "badAccessToken");
        willThrow(new BadGatewayException("bad gateway"))
                .given(authenticationManager).requestOAuthUserInfo(any(OAuthType.class), anyString());

        // when

        // then
        assertThrows(BadGatewayException.class, () -> authService.authenticate(authRequestDto));
    }

    @Test
    @DisplayName("사용자 oauth type 이슈")
    void oauthOAuthTypeParameterTest() throws Exception {
        // given
        AuthRequestDto authRequestDto = new AuthRequestDto(OAuthType.KAKAO, "accessToken");
        LocalDateTime now = LocalDateTime.now();
        willReturn(KakaoOAuthProfile.of("kakao_id", now, "kkh"))
                .given(authenticationManager).requestOAuthUserInfo(any(OAuthType.class), anyString());

        // when

        // then
        assertThrows(UserNotFoundException.class, () -> authService.authenticate(authRequestDto));
    }

    @Test
    @DisplayName("회원 가입 성공")
    void singUpOAuthSuccess() throws Exception {
        // given
        AuthCreationRequestDto authCreationRequestDto = new AuthCreationRequestDto(OAuthType.KAKAO, "kkh", "accessToken");
        LocalDateTime now = LocalDateTime.now();
        willReturn(KakaoOAuthProfile.of("kakao_id", now, "kkh"))
                .given(authenticationManager).requestOAuthUserInfo(any(OAuthType.class), anyString());
        willReturn("mock_token_data")
                .given(tokenProvider).createToken(any(User.class));

        // when
        OAuthDto oAuthDto = authService.signUp(authCreationRequestDto);
        Optional<User> kakaSingUpUser = userRepository.findByOAuthIdAndOAuthType("kakao_id", OAuthType.KAKAO);

        // then

        assertTrue(kakaSingUpUser.isPresent());
        User realuser = kakaSingUpUser.get();
        assertEquals(OAuthType.KAKAO, oAuthDto.getAuthType());
        assertEquals(realuser.getId(), oAuthDto.getUserId());
        assertEquals("mock_token_data", oAuthDto.getToken());
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
        assertThrows(OAUthExistException.class, () -> authService.signUp(authCreationRequestDto));
    }
}