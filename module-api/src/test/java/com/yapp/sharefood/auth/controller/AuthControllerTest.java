package com.yapp.sharefood.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yapp.sharefood.auth.dto.OAuthDto;
import com.yapp.sharefood.auth.dto.request.AuthRequsetDto;
import com.yapp.sharefood.auth.service.AuthService;
import com.yapp.sharefood.external.exception.BadGatewayException;
import com.yapp.sharefood.oauth.exception.UserNotFoundException;
import com.yapp.sharefood.user.domain.OAuthType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.nio.charset.StandardCharsets;
import java.security.InvalidParameterException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.willReturn;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AuthController.class)
class AuthControllerTest {

    @Autowired
    MockMvc mockMvc;
    @MockBean
    AuthService authService;

    ObjectMapper objectMapper = new ObjectMapper();


    @Test
    @DisplayName("회원 인증성공")
    void authenticateSuccessTest() throws Exception {
        // given
        AuthRequsetDto authRequsetDto = new AuthRequsetDto(OAuthType.KAKAO, "accessToken");
        willReturn(OAuthDto.of(1L, "socialToken", OAuthType.KAKAO))
                .given(authService).authenticate(authRequsetDto);

        // when
        String requestBodyStr = objectMapper.writeValueAsString(new AuthRequsetDto(OAuthType.KAKAO, "accessToken"));
        ResultActions perform = mockMvc.perform(post("/api/v1/auth")
                .content(requestBodyStr)
                .contentType(MediaType.APPLICATION_JSON));

        // then
        perform.andExpect(status().isOk())
                .andExpect(header().exists("Authorization"))
                .andExpect(header().string("Authorization", "socialToken"));
    }


    @Test
    @DisplayName("등록된 회원이 없을 경우")
    void authenticateEmptyUserTest() throws Exception {
        // given
        AuthRequsetDto authRequsetDto = new AuthRequsetDto(OAuthType.KAKAO, "accessToken");
        willThrow(new UserNotFoundException())
                .given(authService).authenticate(authRequsetDto);

        // when
        String requestBodyStr = objectMapper.writeValueAsString(new AuthRequsetDto(OAuthType.KAKAO, "accessToken"));
        ResultActions perform = mockMvc.perform(post("/api/v1/auth")
                .content(requestBodyStr)
                .contentType(MediaType.APPLICATION_JSON));

        // then
        String errorMsg = perform.andExpect(status().is(404))
                .andExpect(header().doesNotExist("Authorization"))
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        assertThat(errorMsg).isNotNull();
        assertThat(errorMsg).isNotEmpty();
    }

    @Test
    @DisplayName("Oauth 서버 다운으로 Gateway error 발생")
    void oauthBadGateExceptionHandlerTest() throws Exception {
        // given
        AuthRequsetDto authRequsetDto = new AuthRequsetDto(OAuthType.KAKAO, "accessToken");
        willThrow(new BadGatewayException("oauth 요청 타입 에러"))
                .given(authService).authenticate(authRequsetDto);

        // when
        String requestBodyStr = objectMapper.writeValueAsString(new AuthRequsetDto(OAuthType.KAKAO, "accessToken"));
        ResultActions perform = mockMvc.perform(post("/api/v1/auth")
                .content(requestBodyStr)
                .contentType(MediaType.APPLICATION_JSON_VALUE));

        // then
        String errorMsg = perform.andExpect(status().is(502))
                .andExpect(header().doesNotExist("Authorization"))
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        assertThat(errorMsg).isNotNull();
        assertThat(errorMsg).isNotEmpty();
    }

    @Test
    @DisplayName("OAuth type오류로 인한 이슈")
    void oauthTypeErrorInvalidExceptionHandlerTest() throws Exception {
        // given
        AuthRequsetDto authRequsetDto = new AuthRequsetDto(OAuthType.KAKAO, "accessToken");
        willThrow(new InvalidParameterException("oauth type 불일치 에러"))
                .given(authService).authenticate(authRequsetDto);

        // when
        String requestBodyStr = objectMapper.writeValueAsString(new AuthRequsetDto(OAuthType.KAKAO, "accessToken"));
        ResultActions perform = mockMvc.perform(post("/api/v1/auth")
                .content(requestBodyStr)
                .contentType(MediaType.APPLICATION_JSON_VALUE));

        // then
        String errorMsg = perform.andExpect(status().is(400))
                .andExpect(header().doesNotExist("Authorization"))
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        assertThat(errorMsg).isNotNull();
        assertThat(errorMsg).isNotEmpty();
    }

    @Test
    void signUp() {
    }
}