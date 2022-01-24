package com.yapp.sharefood.user.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.yapp.sharefood.common.controller.PreprocessController;
import com.yapp.sharefood.common.exception.ForbiddenException;
import com.yapp.sharefood.flavor.domain.FlavorType;
import com.yapp.sharefood.flavor.dto.FlavorDto;
import com.yapp.sharefood.flavor.dto.request.UserFlavorRequest;
import com.yapp.sharefood.flavor.dto.response.FlavorsResponse;
import com.yapp.sharefood.flavor.exception.FlavorNotFoundException;
import com.yapp.sharefood.oauth.exception.UserNotFoundException;
import com.yapp.sharefood.user.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.yapp.sharefood.common.controller.documentation.DocumentationUtils.documentIdentify;
import static com.yapp.sharefood.common.exception.ForbiddenException.FORBIDDEN_EXCEPTION_MSG;
import static com.yapp.sharefood.flavor.exception.FlavorNotFoundException.FLAVOR_NOT_FOUND_EXCEPTION_MSG;
import static com.yapp.sharefood.oauth.exception.UserNotFoundException.USER_NOT_FOUND_EXCEPTION_MSG;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.willReturn;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class UserFlavorControllerProcess extends PreprocessController {

    @BeforeEach
    void setUp() {
        loginMockSetup();
    }

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
        ResultActions perform = mockMvc.perform(get("/api/v1/users/me/flavors")
                .header(HttpHeaders.AUTHORIZATION, "token"));

        //then
        FlavorsResponse response = objectMapper.readValue(perform.andExpect(status().isOk())
                .andDo(documentIdentify("user-flavor/get/success"))
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8), new TypeReference<>() {
        });

        assertEquals(2, response.getFlavors().size());
        assertEquals(1L, response.getFlavors().get(0).getId());
        assertEquals("매운맛", response.getFlavors().get(0).getFlavorName());
        assertEquals(2L, response.getFlavors().get(1).getId());
        assertEquals("단맛", response.getFlavors().get(1).getFlavorName());
    }

    @DisplayName("입맛 찾기 요청 중 유저 정보가 일치하지 않는 경우")
    @Test
    void findUserFlavorFailValidUserIdPathTest() throws Exception {
        //given
        willThrow(new ForbiddenException())
                .given(flavorService).findUserFlavors(any(User.class));

        //when
        ResultActions perform = mockMvc.perform(get("/api/v1/users/me/flavors")
                .header(HttpHeaders.AUTHORIZATION, "token"));

        //then
        String errMsg = perform.andExpect(status().isForbidden())
                .andDo(documentIdentify("user-flavor/get/fail/forbidden"))
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        assertThat(errMsg)
                .isNotNull()
                .isNotEmpty()
                .isEqualTo(FORBIDDEN_EXCEPTION_MSG);
    }

    @DisplayName("입맛 찾기 요청 중 유저가 없는 경우")
    @Test
    void findUserFlavorFailUserNotFoundTest() throws Exception {
        //given
        willThrow(new UserNotFoundException())
                .given(flavorService).findUserFlavors(any(User.class));

        //when
        ResultActions perform = mockMvc.perform(get("/api/v1/users/me/flavors")
                .header(HttpHeaders.AUTHORIZATION, "token"));

        //then
        String errMsg = perform.andExpect(status().isNotFound())
                .andDo(documentIdentify("user-flavor/get/fail/userNotFound"))
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        assertThat(errMsg)
                .isNotNull()
                .isNotEmpty()
                .isEqualTo(USER_NOT_FOUND_EXCEPTION_MSG);
    }

    @DisplayName("입맛 등록 성공")
    @Test
    void updateUserFlavorTest() throws Exception {
        //given
        UserFlavorRequest request = UserFlavorRequest.of(List.of(FlavorType.SPICY.getFlavorName(), FlavorType.SWEET.getFlavorName()));
        request.setFlavors(List.of(FlavorType.SPICY.getFlavorName(), FlavorType.SWEET.getFlavorName()));

        willReturn(new FlavorsResponse(List.of(FlavorDto.of(1L, FlavorType.SPICY), FlavorDto.of(2L, FlavorType.SWEET))))
                .given(flavorService).updateUserFlavors(any(User.class), any(UserFlavorRequest.class));

        //when
        ResultActions perform = mockMvc.perform(put("/api/v1/users/me/flavors")
                .header(HttpHeaders.AUTHORIZATION, "token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        //then
        FlavorsResponse response = objectMapper.readValue(perform.andExpect(status().isOk())
                .andDo(documentIdentify("user-flavor/post/success"))
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8), new TypeReference<>() {
        });

        assertThat(response.getFlavors())
                .isNotNull()
                .hasSize(2)
                .extracting("flavorName")
                .containsExactlyInAnyOrderElementsOf(List.of("매운맛", "단맛"));
    }

    @DisplayName("입맛 등록 요청 중 유저 정보가 일치하지 않는 경우")
    @Test
    void updateUserFlavorFailValidUserIdPathTest() throws Exception {
        //given
        UserFlavorRequest request = UserFlavorRequest.of(Collections.emptyList());

        willThrow(new ForbiddenException())
                .given(flavorService).updateUserFlavors(any(User.class), any(UserFlavorRequest.class));

        //when
        ResultActions perform = mockMvc.perform(put("/api/v1/users/me/flavors")
                .header(HttpHeaders.AUTHORIZATION, "token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        //then
        String errMsg = perform.andExpect(status().isForbidden())
                .andDo(documentIdentify("user-flavor/post/fail/forbidden"))
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        assertThat(errMsg)
                .isNotNull()
                .isNotEmpty()
                .isEqualTo(FORBIDDEN_EXCEPTION_MSG);
    }

    @DisplayName("입맛 등록 요청 중 입맛 정보를 찾을 수 없는 경우")
    @Test
    void updateUserFlavorFailFlavorNotFoundTest() throws Exception {
        //given
        UserFlavorRequest request = UserFlavorRequest.of(Collections.emptyList());

        willThrow(new FlavorNotFoundException())
                .given(flavorService).updateUserFlavors(any(User.class), any(UserFlavorRequest.class));

        //when
        ResultActions perform = mockMvc.perform(put("/api/v1/users/me/flavors")
                .header(HttpHeaders.AUTHORIZATION, "token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        //then
        String errMsg = perform.andExpect(status().isNotFound())
                .andDo(documentIdentify("user-flavor/post/fail/flavorNotFound"))
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        assertThat(errMsg)
                .isNotNull()
                .isNotEmpty()
                .isEqualTo(FLAVOR_NOT_FOUND_EXCEPTION_MSG);
    }
}
