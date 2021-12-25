package com.yapp.sharefood.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yapp.sharefood.auth.dto.OAuthDto;
import com.yapp.sharefood.auth.dto.request.AuthCreationRequestDto;
import com.yapp.sharefood.auth.dto.request.AuthRequestDto;
import com.yapp.sharefood.auth.service.AuthService;
import com.yapp.sharefood.auth.token.TokenProvider;
import com.yapp.sharefood.common.DocumentTest;
import com.yapp.sharefood.external.exception.BadGatewayException;
import com.yapp.sharefood.oauth.exception.OAUthExistException;
import com.yapp.sharefood.oauth.exception.UserNotFoundException;
import com.yapp.sharefood.user.domain.OAuthType;
import com.yapp.sharefood.user.repository.UserRepository;
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

import static com.yapp.sharefood.common.documentation.DocumentationUtils.documentIdentify;
import static com.yapp.sharefood.oauth.exception.UserNotFoundException.USER_NOT_FOUND_EXCEPTION_MSG;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.willReturn;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AuthController.class)
class AuthControllerTest extends DocumentTest {

    @Autowired
    MockMvc mockMvc;
    @MockBean
    UserRepository userRepository;
    @MockBean
    TokenProvider tokenProvider;

    @MockBean
    AuthService authService;

    ObjectMapper objectMapper = new ObjectMapper();


    @Test
    @DisplayName("회원 인증성공")
    void authenticateSuccessTest() throws Exception {
        // given
        willReturn(OAuthDto.of(1L, "jwtToken", OAuthType.KAKAO))
                .given(authService).authenticate(any(AuthRequestDto.class));

        // when
        String requestBodyStr = objectMapper.writeValueAsString(new AuthRequestDto(OAuthType.KAKAO, "accessToken"));
        ResultActions perform = mockMvc.perform(post("/api/v1/auth")
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

        ResultActions perform = mockMvc.perform(post("/api/v1/auth")
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
        ResultActions perform = mockMvc.perform(post("/api/v1/auth")
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
                .isNotEmpty()
                .isEqualTo("oauth 요청 타입 에러");
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
        ResultActions perform = mockMvc.perform(post("/api/v1/auth")
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
        ResultActions perform = mockMvc.perform(post("/api/v1/auth/creation")
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
        ResultActions perform = mockMvc.perform(post("/api/v1/auth/creation")
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
}