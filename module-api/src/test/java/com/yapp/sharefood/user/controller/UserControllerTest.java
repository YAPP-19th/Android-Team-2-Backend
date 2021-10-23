package com.yapp.sharefood.user.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yapp.sharefood.common.PreprocessController;
import com.yapp.sharefood.oauth.exception.UserNotFoundException;
import com.yapp.sharefood.user.domain.OAuthType;
import com.yapp.sharefood.user.domain.User;
import com.yapp.sharefood.user.dto.OtherUserInfoDto;
import com.yapp.sharefood.user.dto.UserInfoDto;
import com.yapp.sharefood.user.dto.request.UserNicknameRequest;
import com.yapp.sharefood.user.dto.response.MyUserInfoResponse;
import com.yapp.sharefood.user.dto.response.OtherUserInfoResponse;
import com.yapp.sharefood.user.dto.response.UserNicknameResponse;
import com.yapp.sharefood.user.exception.UserNicknameExistException;
import com.yapp.sharefood.user.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
class UserControllerTest extends PreprocessController {
    @Autowired
    MockMvc mockMvc;
    @MockBean
    UserService userService;

    ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DisplayName("유니크한 nickname 반환 api 테스트")
    void findNotExistNickNameTest() throws Exception {
        willReturn("냠냠박사 unique닉네임")
                .given(userService).createUniqueNickname();

        // when
        ResultActions perform = mockMvc.perform(get("/api/v1/users/nickname"));

        // then
        UserNicknameResponse userNicknameResponse = objectMapper.readValue(
                perform.andExpect(status().isOk())
                        .andDo(print())
                        .andReturn()
                        .getResponse()
                        .getContentAsString(StandardCharsets.UTF_8),
                new TypeReference<>() {
                });

        assertEquals("냠냠박사 unique닉네임", userNicknameResponse.getNickname());
    }

    @Test
    @DisplayName("새로 생성된 user nickname이 이미 존재할 경우 예외 테스트")
    void findExistUserNicknameExceptionTest() throws Exception {
        // given
        willThrow(new UserNicknameExistException("해당 nickname은 이미 존재합니다."))
                .given(userService).createUniqueNickname();

        // when
        ResultActions perform = mockMvc.perform(get("/api/v1/users/nickname"));

        // then
        String errMsg = perform.andExpect(status().isConflict())
                .andDo(print())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);
        assertThat(errMsg)
                .isNotNull()
                .isNotEmpty();
    }

    @Test
    @DisplayName("겹치는 nickname이 없을 경우")
    void checkNicknameNotDuplicateTest() throws Exception {
        // given
        willDoNothing()
                .given(userService).checkNicknameDuplicate(any(String.class));

        // when
        ResultActions perform = mockMvc.perform(
                get(String.format("/api/v1/users/%d/nickname/validation", authUserId))
                        .header(HttpHeaders.AUTHORIZATION, "token")
                        .param("nickname", "newNickname"));

        // then
        perform.andExpect(status().isOk())
                .andDo(print())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);
    }

    @Test
    @DisplayName("자신의 nickname이 아닌 다른 사람의 닉네임 검사를 할 경우")
    void checkNicknameNotMeTest() throws Exception {
        // given

        // when
        ResultActions perform = mockMvc.perform(
                get(String.format("/api/v1/users/%d/nickname/validation", 2L))
                        .header(HttpHeaders.AUTHORIZATION, "token")
                        .param("nickname", "newNickname"));

        // then
        perform.andExpect(status().isForbidden())
                .andDo(print())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);
    }

    @Test
    @DisplayName("nickname이 겹치는 경우")
    void checkNicknameDuplicateTest() throws Exception {
        // given
        willThrow(new UserNicknameExistException())
                .given(userService).checkNicknameDuplicate(any(String.class));

        // when
        ResultActions perform = mockMvc.perform(
                get(String.format("/api/v1/users/%d/nickname/validation", authUserId))
                        .header(HttpHeaders.AUTHORIZATION, "token")
                        .param("nickname", "newNickname"));

        // then
        perform.andExpect(status().isConflict())
                .andDo(print())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);
    }

    @Test
    @DisplayName("user nickname 수정 테스트")
    void userNicknameChangeTest() throws Exception {
        // given
        UserNicknameRequest request = new UserNicknameRequest("newNickname");
        willReturn("newNickname")
                .given(userService).changeUserNickname(anyLong(), any(UserNicknameRequest.class));

        // when
        ResultActions perform = mockMvc.perform(
                patch(String.format("/api/v1/users/%d/nickname", authUserId))
                        .header(HttpHeaders.AUTHORIZATION, "token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)));
        // then
        UserNicknameResponse response = objectMapper
                .readValue(perform.andExpect(status().isOk())
                        .andDo(print())
                        .andReturn()
                        .getResponse()
                        .getContentAsString(StandardCharsets.UTF_8), new TypeReference<>() {
                });

        assertEquals("newNickname", response.getNickname());
    }

    @Test
    @DisplayName("user nickname 수정 실패")
    void userNicknameChangeFailTest() throws Exception {
        // given
        UserNicknameRequest request = new UserNicknameRequest("newNickname");
        willThrow(UserNicknameExistException.class)
                .given(userService).changeUserNickname(anyLong(), any(UserNicknameRequest.class));

        // when
        ResultActions perform = mockMvc.perform(
                patch(String.format("/api/v1/users/%d/nickname", authUserId))
                        .header(HttpHeaders.AUTHORIZATION, "token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)));

        // then
        perform.andExpect(status().isConflict())
                .andDo(print())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);
    }

    @Test
    @DisplayName("권한 제한으로 인한 user nickname 수정 실패")
    void userNicknameChangeFailCuzForbiddenTest() throws Exception {
        // given
        UserNicknameRequest request = new UserNicknameRequest("newNickname");
        willReturn("newNickname")
                .given(userService).changeUserNickname(anyLong(), any(UserNicknameRequest.class));

        // when
        ResultActions perform = mockMvc.perform(
                patch(String.format("/api/v1/users/%d/nickname", 2L))
                        .header(HttpHeaders.AUTHORIZATION, "token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)));

        // then
        perform.andExpect(status().isForbidden())
                .andDo(print())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);
    }

    @Test
    @DisplayName("user 정보 조회 성공")
    void userInfoFindingTest() throws Exception {
        // given
        Long userId = authUserId;
        User user = User.builder()
                .id(userId)
                .name("kkh")
                .nickname("nickname")
                .oAuthType(OAuthType.KAKAO)
                .build();
        willReturn(UserInfoDto.of(user))
                .given(userService).findUserInfo(userId);

        // when
        ResultActions perform = mockMvc.perform(
                get("/api/v1/users/me")
                        .header(HttpHeaders.AUTHORIZATION, "token"));

        // then
        MyUserInfoResponse response = objectMapper
                .readValue(perform.andExpect(status().isOk())
                        .andDo(print())
                        .andReturn()
                        .getResponse()
                        .getContentAsString(StandardCharsets.UTF_8), new TypeReference<>() {
                });

        assertEquals(userId, response.getUserInfo().getId());
        assertEquals("nickname", response.getUserInfo().getNickname());
    }

    @Test
    @DisplayName("user info 조회 실패 -> 없는 사용자")
    void userInfoFindingFailTest() throws Exception {
        // given
        Long userId = authUserId;
        willThrow(UserNotFoundException.class)
                .given(userService).findUserInfo(userId);

        // when
        ResultActions perform = mockMvc.perform(
                get("/api/v1/users/me")
                        .header(HttpHeaders.AUTHORIZATION, "token"));

        // then
        perform.andExpect(status().isNotFound())
                .andDo(print())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);
    }

    @Test
    @DisplayName("다른 사용자 info 정보 확인 테스트")
    void findOtherUserInfoTest() throws Exception {
        User user = User.builder()
                .id(100L)
                .name("otherName")
                .nickname("othreNickname")
                .oAuthType(OAuthType.KAKAO)
                .build();
        willReturn(new OtherUserInfoResponse(OtherUserInfoDto.of(user.getId(), user.getNickname())))
                .given(userService).findOtherUserInfo(anyLong());

        // when
        ResultActions perform = mockMvc.perform(
                get(String.format("/api/v1/users/%d", 100L))
                        .header(HttpHeaders.AUTHORIZATION, "token"));

        // then
        OtherUserInfoResponse response = objectMapper
                .readValue(perform.andExpect(status().isOk())
                        .andDo(print())
                        .andReturn()
                        .getResponse()
                        .getContentAsString(StandardCharsets.UTF_8), new TypeReference<>() {
                });

        assertNotEquals(100L, authUserId);
        assertEquals(100L, response.getUserInfo().getId());
        assertEquals("othreNickname", response.getUserInfo().getNickname());
    }

    @Test
    @DisplayName("없는 다른 사용자 이름 검사 테스트")
    void findOtherUserInfoFailTest() throws Exception {
        willThrow(UserNotFoundException.class)
                .given(userService).findOtherUserInfo(anyLong());

        // when
        ResultActions perform = mockMvc.perform(
                get(String.format("/api/v1/users/%d", 100L))
                        .header(HttpHeaders.AUTHORIZATION, "token"));

        // then
        perform.andExpect(status().isNotFound())
                .andDo(print())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        assertNotEquals(100L, authUserId);
    }
}