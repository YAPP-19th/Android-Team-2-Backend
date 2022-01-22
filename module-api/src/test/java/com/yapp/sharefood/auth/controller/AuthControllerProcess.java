package com.yapp.sharefood.auth.controller;

import com.yapp.sharefood.auth.dto.OAuthDto;
import com.yapp.sharefood.auth.dto.request.AuthCreationRequestDto;
import com.yapp.sharefood.auth.dto.request.AuthRequestDto;
import com.yapp.sharefood.common.controller.PreprocessController;
import com.yapp.sharefood.external.exception.BadGatewayException;
import com.yapp.sharefood.oauth.exception.OAUthExistException;
import com.yapp.sharefood.oauth.exception.TokenExpireExcetion;
import com.yapp.sharefood.oauth.exception.UserNotFoundException;
import com.yapp.sharefood.user.domain.OAuthType;
import com.yapp.sharefood.user.domain.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import java.nio.charset.StandardCharsets;
import java.security.InvalidParameterException;
import java.util.Optional;

import static com.yapp.sharefood.common.controller.documentation.DocumentationUtils.documentIdentify;
import static com.yapp.sharefood.oauth.exception.UserNotFoundException.USER_NOT_FOUND_EXCEPTION_MSG;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.willReturn;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuthControllerProcess extends PreprocessController {

    @Test
    @DisplayName("회원 인증성공")
    void authenticateSuccessTest() throws Exception {
        // given
        willReturn(OAuthDto.of(1L, "jwtToken", OAuthType.KAKAO))
                .given(authService).authenticate(any(AuthRequestDto.class));

        // when
        String requestBodyStr = objectMapper.writeValueAsString(new AuthRequestDto(OAuthType.KAKAO, "accessToken"));
        ResultActions perform = mockMvc.perform(post("/api/v1/users/auth")
                .content(requestBodyStr)
                .contentType(MediaType.APPLICATION_JSON));

        // then
        String emptyResponseMsg = perform.andExpect(status().isOk())
                .andExpect(header().exists("Authorization"))
                .andExpect(header().string("Authorization", "jwtToken"))
                .andDo(documentIdentify("auth/post/success"))
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        assertThat(emptyResponseMsg)
                .isNotNull()
                .isEmpty();
    }

    @Test
    @DisplayName("등록된 회원이 없을 경우")
    void authenticateEmptyUserTest() throws Exception {
        // given
        willThrow(new UserNotFoundException())
                .given(authService).authenticate(any(AuthRequestDto.class));

        // when
        String requestBodyStr = objectMapper.writeValueAsString(new AuthRequestDto(OAuthType.KAKAO, "accessToken"));

        ResultActions perform = mockMvc.perform(post("/api/v1/users/auth")
                .content(requestBodyStr)
                .contentType(MediaType.APPLICATION_JSON));

        // then
        String errorMsg = perform.andExpect(status().isNotFound())
                .andExpect(header().doesNotExist("Authorization"))
                .andDo(documentIdentify("auth/post/fail/userNotFound"))
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        assertThat(errorMsg)
                .isNotNull()
                .isNotEmpty()
                .isEqualTo(USER_NOT_FOUND_EXCEPTION_MSG);
    }

    @Test
    @DisplayName("Oauth 서버 다운으로 Gateway error 발생")
    void oauthBadGateExceptionHandlerTest() throws Exception {
        // given
        willThrow(new BadGatewayException("oauth 요청 타입 에러"))
                .given(authService).authenticate(any(AuthRequestDto.class));

        // when
        String requestBodyStr = objectMapper.writeValueAsString(new AuthRequestDto(OAuthType.KAKAO, "accessToken"));
        ResultActions perform = mockMvc.perform(post("/api/v1/users/auth")
                .content(requestBodyStr)
                .contentType(MediaType.APPLICATION_JSON_VALUE));

        // then
        String errorMsg = perform.andExpect(status().isBadGateway())
                .andExpect(header().doesNotExist("Authorization"))
                .andDo(documentIdentify("auth/post/fail/badGateway"))
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        assertThat(errorMsg)
                .isNotNull()
                .isNotEmpty();
    }

    @Test
    @DisplayName("OAuth type오류로 인한 이슈")
    void oauthTypeErrorInvalidExceptionHandlerTest() throws Exception {
        // given
        willThrow(new InvalidParameterException("oauth type 불일치 에러"))
                .given(authService).authenticate(any(AuthRequestDto.class));

        // when
        AuthRequestDto authRequestDto = new AuthRequestDto(OAuthType.KAKAO, "accessToken");
        String requestBodyStr = objectMapper.writeValueAsString(authRequestDto);
        ResultActions perform = mockMvc.perform(post("/api/v1/users/auth")
                .content(requestBodyStr)
                .contentType(MediaType.APPLICATION_JSON_VALUE));

        // then
        String errorMsg = perform.andExpect(status().isBadRequest())
                .andExpect(header().doesNotExist("Authorization"))
                .andDo(documentIdentify("auth/post/fail/invalidParameter"))
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        assertThat(errorMsg)
                .isNotNull()
                .isNotEmpty()
                .isEqualTo("oauth type 불일치 에러");
    }

    @Test
    @DisplayName("회원 가입 성공")
    void singUpSuccessTest() throws Exception {
        // given
        willReturn(OAuthDto.of(1L, "jwtToken", OAuthType.KAKAO))
                .given(authService).signUp(any(AuthCreationRequestDto.class));

        // when
        AuthCreationRequestDto authCreationRequestDto = new AuthCreationRequestDto(OAuthType.KAKAO, "kkh", "accessToken");
        String requestBodyStr = objectMapper.writeValueAsString(authCreationRequestDto);
        ResultActions perform = mockMvc.perform(post("/api/v1/users/creation")
                .content(requestBodyStr)
                .contentType(MediaType.APPLICATION_JSON));

        // then
        String createResponse = perform.andExpect(status().isCreated())
                .andExpect(header().exists("Authorization"))
                .andExpect(header().string("Authorization", "jwtToken"))
                .andDo(documentIdentify("auth-creation/post/success"))
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        assertThat(createResponse)
                .isNotNull()
                .isEmpty();
    }

    @Test
    @DisplayName("회원 가입시 기존 사용자가 있을 경우")
    void singUpErrorCuzOfExistDataTest() throws Exception {
        // given
        willThrow(new OAUthExistException("존재하는 사용자 입니다."))
                .given(authService).signUp(any(AuthCreationRequestDto.class));

        // when
        AuthCreationRequestDto authCreationRequestDto = new AuthCreationRequestDto(OAuthType.KAKAO, "kkh", "accessToken");
        String requestBodyStr = objectMapper.writeValueAsString(authCreationRequestDto);
        ResultActions perform = mockMvc.perform(post("/api/v1/users/creation")
                .content(requestBodyStr)
                .contentType(MediaType.APPLICATION_JSON));

        // then
        String createResponse = perform.andExpect(status().isConflict())
                .andExpect(header().doesNotExist("Authorization"))
                .andDo(documentIdentify("auth-creation/post/fail/oauthExist"))
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        assertThat(createResponse)
                .isNotNull()
                .isNotEmpty()
                .isEqualTo("존재하는 사용자 입니다.");
    }

    @Test
    @DisplayName("토큰 최신화 - 성공")
    void refreshToken_Success() throws Exception {
        // given
        User user = User.builder()
                .id(1L)
                .nickname("nickname")
                .oauthId("oauth_id")
                .oAuthType(OAuthType.KAKAO)
                .name("name")
                .build();

        willReturn(true)
                .given(tokenProvider).isValidToken(anyString());
        willReturn(1L)
                .given(tokenProvider).extractIdByToken(anyString());
        willReturn(Optional.of(user))
                .given(userRepository).findById(anyLong());
        willReturn("refreshToken")
                .given(authService).refreshToken(any(User.class));

        // when
        ResultActions perform = mockMvc.perform(post("/api/v1/users/auth/token")
                .header(HttpHeaders.AUTHORIZATION, "token"));

        // then
        perform.andExpect(status().isOk())
                .andExpect(header().exists("Authorization"))
                .andDo(documentIdentify("refresh-token/post/success"))
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);
    }

    @Test
    @DisplayName("토큰 최신화 실패 기간이 지난 token - 실패 401")
    void refreshToken_Fail_401InvalidToken() throws Exception {
        // given
        User user = User.builder()
                .id(1L)
                .nickname("nickname")
                .oauthId("oauth_id")
                .oAuthType(OAuthType.KAKAO)
                .name("name")
                .build();

        willThrow(TokenExpireExcetion.class)
                .given(tokenProvider).isValidToken(anyString());
        willReturn(1L)
                .given(tokenProvider).extractIdByToken(anyString());
        willReturn(Optional.of(user))
                .given(userRepository).findById(anyLong());

        // when
        ResultActions perform = mockMvc.perform(post("/api/v1/users/auth/token")
                .header(HttpHeaders.AUTHORIZATION, "token"));

        // then
        perform.andExpect(status().isUnauthorized())
                .andExpect(header().doesNotExist("Authorization"))
                .andDo(documentIdentify("refresh-token/post/fail/unauth"))
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);
    }

    @Test
    @DisplayName("토큰 최신화 실패 탈퇴한 회원 token - 실패 404")
    void refreshToken_Fail_404UserNotFound() throws Exception {
        // given
        willReturn(true)
                .given(tokenProvider).isValidToken(anyString());
        willReturn(1L)
                .given(tokenProvider).extractIdByToken(anyString());
        willThrow(UserNotFoundException.class)
                .given(userRepository).findById(anyLong());

        // when
        ResultActions perform = mockMvc.perform(post("/api/v1/users/auth/token")
                .header(HttpHeaders.AUTHORIZATION, "token"));

        // then
        perform.andExpect(status().isNotFound())
                .andExpect(header().doesNotExist("Authorization"))
                .andDo(documentIdentify("refresh-token/post/fail/unauth"))
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);
    }
}