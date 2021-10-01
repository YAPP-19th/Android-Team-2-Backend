package com.yapp.sharefood.user.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yapp.sharefood.user.dto.response.UserNicknameResponseDto;
import com.yapp.sharefood.user.exception.UserNicknameExistException;
import com.yapp.sharefood.user.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.willReturn;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
class UserControllerTest {
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
        UserNicknameResponseDto userNicknameResponseDto = objectMapper.readValue(
                perform.andExpect(status().isOk())
                        .andDo(print())
                        .andReturn()
                        .getResponse()
                        .getContentAsString(StandardCharsets.UTF_8),
                new TypeReference<>() {
                });

        Assertions.assertEquals("냠냠박사 unique닉네임", userNicknameResponseDto.getNickname());
    }

    @Test
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
}