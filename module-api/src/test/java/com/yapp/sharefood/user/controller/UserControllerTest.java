package com.yapp.sharefood.user.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.yapp.sharefood.common.controller.PreprocessController;
import com.yapp.sharefood.oauth.exception.UserNotFoundException;
import com.yapp.sharefood.report.exception.ReportNotDefineException;
import com.yapp.sharefood.user.domain.Grade;
import com.yapp.sharefood.user.domain.OAuthType;
import com.yapp.sharefood.user.domain.User;
import com.yapp.sharefood.user.domain.UserReportType;
import com.yapp.sharefood.user.dto.OtherUserInfoDto;
import com.yapp.sharefood.user.dto.UserInfoDto;
import com.yapp.sharefood.user.dto.request.UserNicknameRequest;
import com.yapp.sharefood.user.dto.request.UserReportRequest;
import com.yapp.sharefood.user.dto.response.MyUserInfoResponse;
import com.yapp.sharefood.user.dto.response.OtherUserInfoResponse;
import com.yapp.sharefood.user.dto.response.UserNicknameResponse;
import com.yapp.sharefood.user.exception.UserNicknameExistException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultActions;

import java.nio.charset.StandardCharsets;
import java.util.stream.Stream;

import static com.yapp.sharefood.common.controller.documentation.DocumentationUtils.documentIdentify;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UserControllerTest extends PreprocessController {

    @BeforeEach
    void setUp() {
        loginMockSetup();
    }

    @Test
    @DisplayName("???????????? nickname ?????? api ?????????")
    void findNotExistNickNameTest() throws Exception {
        willReturn(new UserNicknameResponse("???????????? unique?????????"))
                .given(userService).createUniqueNickname();

        // when
        RequestBuilder requestBuilder = get("/api/v1/users/nickname");

        ResultActions perform = mockMvc.perform(requestBuilder);

        // then
        UserNicknameResponse userNicknameResponse = objectMapper.readValue(
                perform.andExpect(status().isOk())
                        .andDo(documentIdentify("user-nickname/get/success"))
                        .andReturn()
                        .getResponse()
                        .getContentAsString(StandardCharsets.UTF_8), new TypeReference<>() {
                }
        );

        String resultNickname = userNicknameResponse.getNickname();
        assertEquals("???????????? unique?????????", resultNickname);
    }

    @Test
    @DisplayName("?????? ????????? user nickname??? ?????? ????????? ?????? ?????? ?????????")
    void findExistUserNicknameExceptionTest() throws Exception {
        // given
        willThrow(new UserNicknameExistException())
                .given(userService).createUniqueNickname();

        //when
        RequestBuilder requestBuilder = get("/api/v1/users/nickname");

        ResultActions perform = mockMvc.perform(requestBuilder);

        //then
        perform.andExpect(status().isConflict())
                .andDo(documentIdentify("user-nickname/get/fail/alreadyExist"))
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);
    }

    @Test
    @DisplayName("????????? nickname??? ?????? ??????")
    void checkNicknameNotDuplicateTest() throws Exception {
        // given
        willDoNothing()
                .given(userService).checkNicknameDuplicate(any(String.class));

        //when
        RequestBuilder requestBuilder = get("/api/v1/users/me/nickname/validation")
                .header(HttpHeaders.AUTHORIZATION, "token")
                .param("nickname", "newNickname");

        ResultActions perform = mockMvc.perform(requestBuilder);

        //then
        perform.andExpect(status().isOk())
                .andDo(documentIdentify("user-nickname-validation/get/success"))
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);
    }

    @Test
    @DisplayName("nickname??? ????????? ??????")
    void checkNicknameDuplicateTest() throws Exception {
        // given
        willThrow(new UserNicknameExistException())
                .given(userService).checkNicknameDuplicate(any(String.class));

        //when
        RequestBuilder requestBuilder = get("/api/v1/users/me/nickname/validation")
                .header(HttpHeaders.AUTHORIZATION, "token")
                .param("nickname", "newNickname");

        ResultActions perform = mockMvc.perform(requestBuilder);

        //then
        perform.andExpect(status().isConflict())
                .andDo(documentIdentify("user-nickname-validation/get/fail/alreadyExist"))
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);
    }

    @Test
    @DisplayName("user nickname ?????? ?????????")
    void userNicknameChangeTest() throws Exception {
        // given
        UserNicknameRequest request = new UserNicknameRequest("newNickname");
        willReturn(new UserNicknameResponse("newNickname"))
                .given(userService).changeUserNickname(anyLong(), any(UserNicknameRequest.class));

        //when
        RequestBuilder requestBuilder = patch("/api/v1/users/me/nickname")
                .header(HttpHeaders.AUTHORIZATION, "token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request));

        ResultActions perform = mockMvc.perform(requestBuilder);

        //then
        UserNicknameResponse response = objectMapper.readValue(perform.andExpect(status().isOk())
                .andDo(documentIdentify("user-nickname/patch/success"))
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8), new TypeReference<>() {
        });

        assertEquals("newNickname", response.getNickname());
    }

    @Test
    @DisplayName("user nickname ?????? ??????")
    void userNicknameChangeFailTest() throws Exception {
        // given
        UserNicknameRequest request = new UserNicknameRequest("newNickname");
        willThrow(new UserNicknameExistException())
                .given(userService).changeUserNickname(anyLong(), any(UserNicknameRequest.class));

        //when
        RequestBuilder requestBuilder = patch("/api/v1/users/me/nickname")
                .header(HttpHeaders.AUTHORIZATION, "token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request));

        ResultActions perform = mockMvc.perform(requestBuilder);

        //then
        perform.andExpect(status().isConflict())
                .andDo(documentIdentify("user-nickname/patch/fail/alreadyExist"))
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);
    }


    @MethodSource
    @ParameterizedTest(name = "user ?????? ?????? ?????? grade ?????? ??????")
    void userInfoFindingTest_Success(long userId, Grade userGrade, Integer point) throws Exception {
        // given
        willReturn(new MyUserInfoResponse(UserInfoDto.of(userId, "nickname" + userId, userGrade, point)))
                .given(userService).findUserInfo(anyLong());

        //when
        RequestBuilder requestBuilder = get("/api/v1/users/me")
                .header(HttpHeaders.AUTHORIZATION, "token");

        ResultActions perform = mockMvc.perform(requestBuilder);

        //then
        MyUserInfoResponse response = objectMapper.readValue(perform.andExpect(status().isOk())
                .andDo(documentIdentify(String.format("user-me/get/success/%d", userId)))
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8), new TypeReference<>() {
        });

        String expectedNickname = "nickname" + userId;
        UserInfoDto responseInfo = response.getUserInfo();
        assertEquals(userId, responseInfo.getId());
        assertEquals(expectedNickname, responseInfo.getNickname());
        assertEquals(userGrade, responseInfo.getGrade());
    }

    static Stream<Arguments> userInfoFindingTest_Success() {
        return Stream.of(
                Arguments.of(1L, Grade.STUDENT, 0),
                Arguments.of(2L, Grade.BACHELOR, 300),
                Arguments.of(3L, Grade.MASTER, 700),
                Arguments.of(4L, Grade.EXPERT, 1200),
                Arguments.of(5L, Grade.PROFESSOR, 1800)
        );
    }

    @Test
    @DisplayName("user info ?????? ?????? -> ?????? ?????????")
    void userInfoFindingFailTest() throws Exception {
        // given
        willThrow(new UserNotFoundException())
                .given(userService).findUserInfo(anyLong());

        //when
        RequestBuilder requestBuilder = get("/api/v1/users/me")
                .header(HttpHeaders.AUTHORIZATION, "token");

        ResultActions perform = mockMvc.perform(requestBuilder);

        //then
        perform.andExpect(status().isNotFound())
                .andDo(documentIdentify("user-me/get/fail/userNotFound"))
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);
    }

    @Test
    @DisplayName("user ?????? - ??????")
    void withdrawUserTest() throws Exception {
        // given
        willDoNothing()
                .given(userService).withdrawUserMembership(any(User.class));

        // when
        ResultActions perform = mockMvc.perform(delete("/api/v1/users/me")
                .header(HttpHeaders.AUTHORIZATION, "token"));
        perform.andExpect(status().isOk())
                .andDo(documentIdentify("user-me/delete/success"))
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);
        // then
    }

    @Test
    @DisplayName("user ?????? ????????? ?????? ?????? ?????? - ??????")
    void withdrawUser_Fail_UserNotFoundException() throws Exception {
        // given
        willThrow(new UserNotFoundException())
                .given(userService).withdrawUserMembership(any(User.class));

        // when
        ResultActions perform = mockMvc.perform(delete("/api/v1/users/me")
                .header(HttpHeaders.AUTHORIZATION, "token"));

        // then
        perform.andExpect(status().isNotFound())
                .andDo(documentIdentify("user-me/delete/fail/userNotFound"))
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);
    }

    @Test
    @DisplayName("?????? ????????? info ?????? ?????? ?????????")
    void findOtherUserInfoTest() throws Exception {
        User givenUser = User.builder()
                .id(100L)
                .name("otherName")
                .nickname("othreNickname")
                .oAuthType(OAuthType.KAKAO)
                .build();

        willReturn(new OtherUserInfoResponse(OtherUserInfoDto.of(givenUser.getId(), givenUser.getNickname())))
                .given(userService).findOtherUserInfo(anyLong());

        //when
        RequestBuilder requestBuilder = get(String.format("/api/v1/users/%d", 100L))
                .header(HttpHeaders.AUTHORIZATION, "token");

        ResultActions perform = mockMvc.perform(requestBuilder);

        //then


        // when, then
        OtherUserInfoResponse response = objectMapper.readValue(perform.andExpect(status().isOk())
                .andDo(documentIdentify("user-other/get/success"))
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8), new TypeReference<>() {
        });

        assertNotEquals(100L, loginUserId);
        assertEquals(100L, response.getUserInfo().getId());
        assertEquals("othreNickname", response.getUserInfo().getNickname());
    }

    @Test
    @DisplayName("?????? ?????? ????????? ?????? ?????? ?????????")
    void findOtherUserInfoFailTest() throws Exception {
        willThrow(new UserNotFoundException())
                .given(userService).findOtherUserInfo(anyLong());

        //when
        ResultActions perform = mockMvc.perform(get(String.format("/api/v1/users/%d", 100L))
                .header(HttpHeaders.AUTHORIZATION, "token"));

        //then
        perform.andDo(documentIdentify("user-other/get/fail/userNotFound"))
                .andExpect(status().isNotFound())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);
    }

    @Test
    @DisplayName("?????? ?????? ??????")
    void userReport_Success() throws Exception {
        //given
        UserReportRequest request = UserReportRequest.builder().reportMessage(UserReportType.POSTING_NO_RELATION_USER.getMessage()).build();

        //when
        RequestBuilder requestBuilder = post(String.format("/api/v1/users/%d/report", 1L))
                .header(HttpHeaders.AUTHORIZATION, "token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request));

        ResultActions perform = mockMvc.perform(requestBuilder);

        //then
        perform.andExpect(status().isOk())
                .andDo(documentIdentify("user-report/post/success"))
                .andReturn()
                .getResponse();
    }

    @Test
    @DisplayName("?????? ?????? ?????? - ?????? ?????? ????????? ?????? ??? ??????")
    void userReport_Fail_User_Not_Found() throws Exception {
        //given
        willThrow(new UserNotFoundException())
                .given(userService).createUserReport(anyLong(), any(UserReportRequest.class));
        UserReportRequest request = UserReportRequest.builder().reportMessage(UserReportType.POSTING_NO_RELATION_USER.getMessage()).build();

        //when
        RequestBuilder requestBuilder = post(String.format("/api/v1/users/%d/report", 1L))
                .header(HttpHeaders.AUTHORIZATION, "token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request));

        ResultActions perform = mockMvc.perform(requestBuilder);

        //then
        perform.andExpect(status().isNotFound())
                .andDo(documentIdentify("user-report/post/fail/userNotFound"))
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);
    }

    @Test
    @DisplayName("?????? ?????? ?????? - ???????????? ?????? ?????? ??????")
    void userReport_Fail_Report_Not_Define() throws Exception {
        //given
        willThrow(new ReportNotDefineException())
                .given(userService).createUserReport(anyLong(), any(UserReportRequest.class));
        UserReportRequest request = UserReportRequest.builder().reportMessage("???????????? ?????? ?????? ??????").build();

        //when
        RequestBuilder requestBuilder = post(String.format("/api/v1/users/%d/report", 1L))
                .header(HttpHeaders.AUTHORIZATION, "token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request));

        ResultActions perform = mockMvc.perform(requestBuilder);

        //then
        perform.andExpect(status().isBadRequest())
                .andDo(documentIdentify("user-report/post/fail/reportNotDefine"))
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);
    }
}