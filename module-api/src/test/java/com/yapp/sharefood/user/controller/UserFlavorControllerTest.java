package com.yapp.sharefood.user.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yapp.sharefood.common.PreprocessController;
import com.yapp.sharefood.common.exception.ForbiddenException;
import com.yapp.sharefood.flavor.domain.FlavorType;
import com.yapp.sharefood.flavor.dto.FlavorDto;
import com.yapp.sharefood.flavor.dto.request.UserFlavorRequest;
import com.yapp.sharefood.flavor.dto.response.FlavorsResponse;
import com.yapp.sharefood.flavor.dto.response.UpdateUserFlavorResponse;
import com.yapp.sharefood.flavor.exception.FlavorNotFoundException;
import com.yapp.sharefood.flavor.service.FlavorService;
import com.yapp.sharefood.oauth.exception.UserNotFoundException;
import com.yapp.sharefood.user.domain.OAuthType;
import com.yapp.sharefood.user.domain.User;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.willReturn;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserFlavorController.class)
public class UserFlavorControllerTest extends PreprocessController {
    @Autowired
    MockMvc mockMvc;

    @MockBean
    FlavorService flavorService;

    ObjectMapper objectMapper = new ObjectMapper();

    @DisplayName("입맛 찾기 성공")
    @Test
    void findUserFlavorTest() throws Exception {
        //given
        FlavorsResponse expect = new FlavorsResponse();
        List<FlavorDto> flavors = new ArrayList<>();
        flavors.add(FlavorDto.of(1L, FlavorType.SPICY));
        flavors.add(FlavorDto.of(2L, FlavorType.SWEET));
        expect.setFlavors(flavors);

        willReturn(expect)
                .given(flavorService).findUserFlavors(any(User.class));

        //when
        ResultActions perform = mockMvc.perform(get(String.format("/api/v1/users/%d/flavors", authUserId))
                .header(HttpHeaders.AUTHORIZATION, "token"));

        //then
        FlavorsResponse response = objectMapper.readValue(perform.andExpect(status().isOk())
                .andDo(print())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8), new TypeReference<>() {
        });

        assertEquals(2, response.getFlavors().size());
        assertEquals(1L, response.getFlavors().get(0).getId());
        assertEquals("매운맛", response.getFlavors().get(0).getFlavorName());
        assertEquals(2L, response.getFlavors().get(1).getId());
        assertEquals("단 맛", response.getFlavors().get(1).getFlavorName());
    }

    @DisplayName("입맛 찾기 요청 중 유저 정보가 일치하지 않는 경우")
    @Test
    void findUserFlavorFailValidUserIdPathTest() throws Exception {
        //given
        willThrow(ForbiddenException.class)
                .given(flavorService).findUserFlavors(any(User.class));

        //when
        ResultActions perform = mockMvc.perform(get(String.format("/api/v1/users/%d/flavors", authUserId))
                .header(HttpHeaders.AUTHORIZATION, "token"));

        //then
        perform.andExpect(status().isForbidden())
                .andDo(print())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);
    }

    @DisplayName("입맛 찾기 요청 중 유저가 없는 경우")
    @Test
    void findUserFlavorFailUserNotFoundTest() throws Exception {
        //given
        willThrow(UserNotFoundException.class)
                .given(flavorService).findUserFlavors(any(User.class));

        //when
        ResultActions perform = mockMvc.perform(get(String.format("/api/v1/users/%d/flavors", authUserId))
                .header(HttpHeaders.AUTHORIZATION, "token"));

        //then
        perform.andExpect(status().isNotFound())
                .andDo(print())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);
    }

    @DisplayName("입맛 등록 성공")
    @Test
    void updateUserFlavorTest() throws Exception {
        //given
        UserFlavorRequest request = new UserFlavorRequest();
        List<FlavorDto> flavors = new ArrayList<>();
        flavors.add(FlavorDto.of(1L, FlavorType.SPICY));
        flavors.add(FlavorDto.of(2L, FlavorType.SWEET));
        request.setFlavors(flavors);

        willReturn(UpdateUserFlavorResponse.of(2))
                .given(flavorService).updateUserFlavors(any(User.class), any(UserFlavorRequest.class));

        //when
        ResultActions perform = mockMvc.perform(put(String.format("/api/v1/users/%d/flavors", authUserId))
                .header(HttpHeaders.AUTHORIZATION, "token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        //then
        UpdateUserFlavorResponse response = objectMapper.readValue(perform.andExpect(status().isOk())
                .andDo(print())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8), new TypeReference<>() {
        });

        assertEquals(2, response.getUpdateSuccessCount());
    }

    @DisplayName("입맛 등록 요청 중 유저 정보가 일치하지 않는 경우")
    @Test
    void updateUserFlavorFailValidUserIdPathTest() throws Exception {
        //given
        UserFlavorRequest request = new UserFlavorRequest();
        request.setFlavors(Collections.emptyList());

        willThrow(ForbiddenException.class)
                .given(flavorService).updateUserFlavors(any(User.class), any(UserFlavorRequest.class));

        //when
        ResultActions perform = mockMvc.perform(put(String.format("/api/v1/users/%d/flavors", authUserId))
                .header(HttpHeaders.AUTHORIZATION, "token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        //then
        perform.andExpect(status().isForbidden())
                .andDo(print())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);
    }

    @DisplayName("입맛 등록 요청 중 입맛 정보를 찾을 수 없는 경우")
    @Test
    void updateUserFlavorFailFlavorNotFoundTest() throws Exception {
        //given
        UserFlavorRequest request = new UserFlavorRequest();
        request.setFlavors(Collections.emptyList());

        willThrow(FlavorNotFoundException.class)
                .given(flavorService).updateUserFlavors(any(User.class), any(UserFlavorRequest.class));

        //when
        ResultActions perform = mockMvc.perform(put(String.format("/api/v1/users/%d/flavors", authUserId))
                .header(HttpHeaders.AUTHORIZATION, "token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        //then
        perform.andExpect(status().isNotFound())
                .andDo(print())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);
    }
}
