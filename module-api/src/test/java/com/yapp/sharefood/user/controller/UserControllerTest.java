package com.yapp.sharefood.user.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yapp.sharefood.common.PreprocessController;
import com.yapp.sharefood.oauth.exception.UserNotFoundException;
import com.yapp.sharefood.report.exception.ReportNotDefineException;
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
import com.yapp.sharefood.user.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultActions;

import java.nio.charset.StandardCharsets;

import static com.yapp.sharefood.common.documentation.DocumentationUtils.documentIdentify;
import static com.yapp.sharefood.oauth.exception.UserNotFoundException.USER_NOT_FOUND_EXCEPTION_MSG;
import static com.yapp.sharefood.user.exception.UserNicknameExistException.NICKNAME_EXIST_EXCEPTION_MSG;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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

        // when
        RequestBuilder requestBuilder = get("/api/v1/users/nickname");

        ResultActions perform = mockMvc.perform(requestBuilder);

        // then
        UserNicknameResponse userNicknameResponse = objectMapper.readValue(
                perform.andExpect(status().isOk())
                        .andDo(documentIdentify("user-nickname/get/success"))
                        .andReturn()
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
        willThrow(new UserNicknameExistException())
                .given(userService).createUniqueNickname();

        //when
        RequestBuilder requestBuilder = get("/api/v1/users/nickname");

        ResultActions perform = mockMvc.perform(requestBuilder);

        //then
        String errMsg = perform.andExpect(status().isConflict())
                .andDo(documentIdentify("user-nickname/get/fail/alreadyExist"))
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        assertThat(errMsg)
                .isNotNull()
                .isNotEmpty()
                .isEqualTo(NICKNAME_EXIST_EXCEPTION_MSG);
    }

    @Test
    @DisplayName("겹치는 nickname이 없을 경우")
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
    @DisplayName("nickname이 겹치는 경우")
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
        String errMsg = perform.andExpect(status().isConflict())
                .andDo(documentIdentify("user-nickname-validation/get/fail/alreadyExist"))
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        assertThat(errMsg)
                .isNotNull()
                .isNotEmpty()
                .isEqualTo(NICKNAME_EXIST_EXCEPTION_MSG);
    }

    @Test
    @DisplayName("user nickname 수정 테스트")
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
                .getContentAsString(StandardCharsets.UTF_8), new TypeReference<UserNicknameResponse>() {
        });

        assertEquals("newNickname", response.getNickname());
    }

    @Test
    @DisplayName("user nickname 수정 실패")
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
        String errMsg = perform.andExpect(status().isConflict())
                .andDo(documentIdentify("user-nickname/patch/fail/alreadyExist"))
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        assertThat(errMsg)
                .isNotNull()
                .isNotEmpty()
                .isEqualTo(NICKNAME_EXIST_EXCEPTION_MSG);
    }

    @Test
    @DisplayName("user 정보 조회 성공")
    void userInfoFindingTest() throws Exception {
        // given
        Long givenUserId = user.getId();
        willReturn(new MyUserInfoResponse(UserInfoDto.of(user)))
                .given(userService).findUserInfo(user.getId());

        //when
        RequestBuilder requestBuilder = get("/api/v1/users/me")
                .header(HttpHeaders.AUTHORIZATION, "token");

        ResultActions perform = mockMvc.perform(requestBuilder);

        //then
        MyUserInfoResponse response = objectMapper.readValue(perform.andExpect(status().isOk())
                .andDo(documentIdentify("user-me/get/success"))
                .andReturn()
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
        willThrow(new UserNotFoundException())
                .given(userService).findUserInfo(userId);

        //when
        RequestBuilder requestBuilder = get("/api/v1/users/me")
                .header(HttpHeaders.AUTHORIZATION, "token");

        ResultActions perform = mockMvc.perform(requestBuilder);

        //then
        String errMsg = perform.andExpect(status().isNotFound())
                .andDo(documentIdentify("user-me/get/fail/userNotFound"))
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        assertThat(errMsg)
                .isNotNull()
                .isNotEmpty()
                .isEqualTo(USER_NOT_FOUND_EXCEPTION_MSG);
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
                .getContentAsString(StandardCharsets.UTF_8), new TypeReference<OtherUserInfoResponse>() {
        });

        assertNotEquals(100L, authUserId);
        assertEquals(100L, response.getUserInfo().getId());
        assertEquals("othreNickname", response.getUserInfo().getNickname());
    }

    @Test
    @DisplayName("없는 다른 사용자 이름 검사 테스트")
    void findOtherUserInfoFailTest() throws Exception {
        willThrow(new UserNotFoundException())
                .given(userService).findOtherUserInfo(anyLong());

        //when
        ResultActions perform = mockMvc.perform(get(String.format("/api/v1/users/%d", 100L))
                .header(HttpHeaders.AUTHORIZATION, "token"));

        //then
        String errMsg = perform.andDo(documentIdentify("user-other/get/fail/userNotFound"))
                .andExpect(status().isNotFound())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        assertThat(errMsg)
                .isNotNull()
                .isNotEmpty()
                .isEqualTo(USER_NOT_FOUND_EXCEPTION_MSG);
    }

    @Test
    @DisplayName("유저 신고 성공")
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
                .andDo(documentIdentify("user-report/success"))
                .andReturn()
                .getResponse();
    }

    @Test
    @DisplayName("유저 신고 실패 - 신고 대상 유저를 찾을 수 없음")
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
        String errMsg = perform.andExpect(status().isNotFound())
                .andDo(documentIdentify("user-report/fail/userNotFound"))
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        assertThat(errMsg)
                .isNotNull()
                .isNotEmpty()
                .isEqualTo(USER_NOT_FOUND_EXCEPTION_MSG);
    }

    @Test
    @DisplayName("유저 신고 실패 - 정의되지 않은 신고 사유")
    void userReport_Fail_Report_Not_Define() throws Exception {
        //given
        willThrow(new ReportNotDefineException())
                .given(userService).createUserReport(anyLong(), any(UserReportRequest.class));
        UserReportRequest request = UserReportRequest.builder().reportMessage(UserReportType.POSTING_NO_RELATION_USER.getMessage()).build();

        //when
        RequestBuilder requestBuilder = post(String.format("/api/v1/users/%d/report", 1L))
                .header(HttpHeaders.AUTHORIZATION, "token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request));

        ResultActions perform = mockMvc.perform(requestBuilder);

        //then
        String errMsg = perform.andExpect(status().isBadRequest())
                .andDo(documentIdentify("user-report/fail/reportNotDefine"))
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        assertThat(errMsg)
                .isNotNull()
                .isNotEmpty()
                .isEqualTo(ReportNotDefineException.NOT_DEFINE_REPORT_EXCEPTION_MSG);
    }
}