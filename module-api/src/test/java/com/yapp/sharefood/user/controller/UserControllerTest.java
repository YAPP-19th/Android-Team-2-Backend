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
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.ResultActions;

import java.nio.charset.StandardCharsets;

import static com.yapp.sharefood.common.documentation.DocumentationUtils.documentIdentify;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
class UserControllerTest extends PreprocessController {

    @MockBean
    UserService userService;

    ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DisplayName("유니크한 nickname 반환 api 테스트")
    void findNotExistNickNameTest() throws Exception {
        willReturn(new UserNicknameResponse("냠냠박사 unique닉네임"))
                .given(userService).createUniqueNickname();

        // when, then
        UserNicknameResponse userNicknameResponse = objectMapper.readValue(
                document()
                        .get("/api/v1/users/nickname")
                        .build()
                        .status(status().isOk())
                        .identifier("user/nickname/get/success")
                        .getResponse()
                        .getContentAsString(StandardCharsets.UTF_8), new TypeReference<UserNicknameResponse>() {
                }
        );

        String resultNickname = userNicknameResponse.getNickname();
        assertEquals("냠냠박사 unique닉네임", resultNickname);
    }

    @Test
    @DisplayName("새로 생성된 user nickname이 이미 존재할 경우 예외 테스트")
    void findExistUserNicknameExceptionTest() throws Exception {
        // given
        willThrow(new UserNicknameExistException("해당 nickname은 이미 존재합니다."))
                .given(userService).createUniqueNickname();

        // when, then
        String errMsg = document()
                .get("/api/v1/users/nickname")
                .build()
                .status(status().isConflict())
                .identifier("user/nickname/get/fail/alreadyExist")
                .getResponse().getContentAsString(StandardCharsets.UTF_8);

        assertThat(errMsg)
                .isNotNull()
                .isNotEmpty()
                .isEqualTo("해당 nickname은 이미 존재합니다.");
    }

    @Test
    @DisplayName("겹치는 nickname이 없을 경우")
    void checkNicknameNotDuplicateTest() throws Exception {
        // given
        willDoNothing()
                .given(userService).checkNicknameDuplicate(any(String.class));

        // when, then
        document()
                .get(String.format("/api/v1/users/%d/nickname/validation?nickname=%s", authUserId, "newNickname"))
                .auth("token")
                .build()
                .status(status().isOk())
                .identifier("user/nickname-validation/get/success")
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);
    }

    @Test
    @DisplayName("자신의 nickname이 아닌 다른 사람의 닉네임 검사를 할 경우")
    void checkNicknameNotMeTest() throws Exception {
        // given

        // when, then
        document()
                .get(String.format("/api/v1/users/%d/nickname/validation?nickname=%s", 2L, "newNickname"))
                .auth("token")
                .build()
                .status(status().isForbidden())
                .identifier("user/nickname-validation/get/fail/forbidden")
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);
    }

    @Test
    @DisplayName("nickname이 겹치는 경우")
    void checkNicknameDuplicateTest() throws Exception {
        // given
        willThrow(new UserNicknameExistException())
                .given(userService).checkNicknameDuplicate(any(String.class));

        // when, then
        document()
                .get(String.format("/api/v1/users/%d/nickname/validation?nickname=%s", authUserId, "newNickname"))
                .auth("token")
                .build()
                .status(status().isConflict())
                .identifier("user/nickname-validation/get/fail/alreadyExist")
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);
    }

    @Test
    @DisplayName("user nickname 수정 테스트")
    void userNicknameChangeTest() throws Exception {
        // given
        UserNicknameRequest request = new UserNicknameRequest("newNickname");
        willReturn(new UserNicknameResponse("newNickname"))
                .given(userService).changeUserNickname(anyLong(), any(UserNicknameRequest.class));

        // when, then
        UserNicknameResponse response = objectMapper.readValue(document()
                .patch(String.format("/api/v1/users/%d/nickname", authUserId), request)
                .auth("token")
                .build()
                .status(status().isOk())
                .identifier("user/nickname/patch/success")
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8), new TypeReference<UserNicknameResponse>() {
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

        // when, then
        document()
                .patch(String.format("/api/v1/users/%d/nickname", authUserId), request)
                .auth("token")
                .build()
                .status(status().isConflict())
                .identifier("user/nickname/patch/fail/alreadyExist")
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);
    }

    @Test
    @DisplayName("권한 제한으로 인한 user nickname 수정 실패")
    void userNicknameChangeFailCuzForbiddenTest() throws Exception {
        // given
        UserNicknameRequest request = new UserNicknameRequest("newNickname");
        willReturn(new UserNicknameResponse("newNickname"))
                .given(userService).changeUserNickname(anyLong(), any(UserNicknameRequest.class));

        // when, then
        document()
                .patch(String.format("/api/v1/users/%d/nickname", 2L), request)
                .auth("token")
                .build()
                .status(status().isForbidden())
                .identifier("user/nickname/patch/fail/forbidden")
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);
    }

    @Test
    @DisplayName("user 정보 조회 성공")
    void userInfoFindingTest() throws Exception {
        // given
        Long givenUserId = user.getId();
        willReturn(new MyUserInfoResponse(UserInfoDto.of(user)))
                .given(userService).findUserInfo(user.getId());

        // when, then
        MyUserInfoResponse response = objectMapper.readValue(document()
                .get("/api/v1/users/me")
                .auth("token")
                .build()
                .status(status().isOk())
                .identifier("user/me/get/success")
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8), new TypeReference<MyUserInfoResponse>() {
        });

        Long resultUserId = response.getUserInfo().getId();
        String resultUserNickname = response.getUserInfo().getNickname();
        assertEquals(givenUserId, resultUserId);
        assertEquals("nickname", resultUserNickname);
    }

    @Test
    @DisplayName("user info 조회 실패 -> 없는 사용자")
    void userInfoFindingFailTest() throws Exception {
        // given
        Long userId = authUserId;
        willThrow(UserNotFoundException.class)
                .given(userService).findUserInfo(userId);

        // when, then
        document()
                .get("/api/v1/users/me")
                .auth("token")
                .build()
                .status(status().isNotFound())
                .identifier("user/me/get/fail/notFound")
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);
    }

    @Test
    @DisplayName("다른 사용자 info 정보 확인 테스트")
    void findOtherUserInfoTest() throws Exception {
        User givenUser = User.builder()
                .id(100L)
                .name("otherName")
                .nickname("othreNickname")
                .oAuthType(OAuthType.KAKAO)
                .build();

        willReturn(new OtherUserInfoResponse(OtherUserInfoDto.of(givenUser.getId(), givenUser.getNickname())))
                .given(userService).findOtherUserInfo(anyLong());

        // when, then
        OtherUserInfoResponse response = objectMapper.readValue(document()
                .get(String.format("/api/v1/users/%d", 100L))
                .auth("token")
                .build()
                .status(status().isOk())
                .identifier("user/other/get/success")
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8), new TypeReference<OtherUserInfoResponse>() {
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

//        // when, then
//        document()
//                .get(String.format("/api/v1/users/%d", 100L))
//                .auth("token")
//                .build()
//                .status(status().isNotFound())
//                .identifier("users-other/get/fail/notFound")
//                .getResponse()
//                .getContentAsString(StandardCharsets.UTF_8);
//        assertNotEquals(100L, authUserId);

        //when
        ResultActions perform = mockMvc.perform(get(String.format("/api/v1/uesrs/%d", 100L))
                .header(HttpHeaders.AUTHORIZATION, "token"));

        //then
        perform.andDo(documentIdentify("user/other/get/fail/notFound"))
                .andExpect(status().isNotFound())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);
    }
}