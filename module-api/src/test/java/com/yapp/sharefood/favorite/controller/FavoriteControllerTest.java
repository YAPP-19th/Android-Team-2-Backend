package com.yapp.sharefood.favorite.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yapp.sharefood.common.PreprocessController;
import com.yapp.sharefood.favorite.service.FavoriteService;
import com.yapp.sharefood.food.exception.FoodNotFoundException;
import com.yapp.sharefood.oauth.exception.UserNotFoundException;
import com.yapp.sharefood.user.domain.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultActions;

import java.nio.charset.StandardCharsets;

import static com.yapp.sharefood.common.documentation.DocumentationUtils.documentIdentify;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.willReturn;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(controllers = FavoriteController.class)
class FavoriteControllerTest extends PreprocessController {
    @MockBean
    FavoriteService favoriteService;

    ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DisplayName("최애 생성 성공")
    void createFavorite_Success() throws Exception {
        //given
        willReturn(1L)
                .given(favoriteService).createFavorite(any(User.class), anyLong());

        //when
        RequestBuilder requestBuilder = post(String.format("/api/v1/foods/%d/favorite", 1L))
                .header(HttpHeaders.AUTHORIZATION, "token");

        ResultActions perform = mockMvc.perform(requestBuilder);

        //then
        perform.andExpect(status().isCreated())
                .andDo(documentIdentify("foods/favorite/post/success"))
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);
    }

    @Test
    @DisplayName("최애 생성 실패 - 존재하지 않는 사용자")
    void createFavorite_Fail_UserNotFound() throws Exception{
        //given
        willThrow(new UserNotFoundException())
                .given(favoriteService).createFavorite(any(User.class), anyLong());

        //when
        RequestBuilder requestBuilder = post(String.format("/api/v1/foods/%d/favorite", 1L))
                .header(HttpHeaders.AUTHORIZATION, "token");

        ResultActions perform = mockMvc.perform(requestBuilder);

        //then
        String errMsg = perform.andExpect(status().isNotFound())
                .andDo(documentIdentify("foods/favorite/post/fail/userNotFound"))
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        assertThat(errMsg)
                .isNotNull()
                .isNotEmpty()
                .isEqualTo(UserNotFoundException.USER_NOT_FOUND_EXCEPTION_MSG);
    }

    @Test
    @DisplayName("최애 생성 실패 - 존재하지 않는 음식(게시글)")
    void createFavorite_Fail_FoodNotFound() throws Exception{
        //given
        willThrow(new FoodNotFoundException())
                .given(favoriteService).createFavorite(any(User.class), anyLong());

        //when
        RequestBuilder requestBuilder = post(String.format("/api/v1/foods/%d/favorite", 1L))
                .header(HttpHeaders.AUTHORIZATION, "token");

        ResultActions perform = mockMvc.perform(requestBuilder);

        //then
        String errMsg = perform.andExpect(status().isNotFound())
                .andDo(documentIdentify("foods/favorite/post/fail/userNotFound"))
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        assertThat(errMsg)
                .isNotNull()
                .isNotEmpty()
                .isEqualTo(FoodNotFoundException.FOOD_NOT_FOUND_EXCEPTION_MSG);
    }

    @Test
    @DisplayName("최애 삭제 성공")
    void deleteFavorite_Success() throws Exception {
        //given

        //when
        RequestBuilder requestBuilder = delete(String.format("/api/v1/foods/%d/favorite", 1L))
                .header(HttpHeaders.AUTHORIZATION, "token");

        ResultActions perform = mockMvc.perform(requestBuilder);

        //then
        perform.andExpect(status().isOk())
                .andDo(documentIdentify("foods/favorite/delete/success"))
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);
    }

    @Test
    @DisplayName("최애 삭제 실패 - 존재하지 않는 사용자")
    void deleteFavorite_Fail_UserNotFound() throws Exception{
        //given
        willThrow(new UserNotFoundException())
                .given(favoriteService).deleteFavorite(any(User.class), anyLong());

        //when
        RequestBuilder requestBuilder = delete(String.format("/api/v1/foods/%d/favorite", 1L))
                .header(HttpHeaders.AUTHORIZATION, "token");

        ResultActions perform = mockMvc.perform(requestBuilder);

        //then
        String errMsg = perform.andExpect(status().isNotFound())
                .andDo(documentIdentify("foods/favorite/delete/fail/userNotFound"))
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        assertThat(errMsg)
                .isNotNull()
                .isNotEmpty()
                .isEqualTo(UserNotFoundException.USER_NOT_FOUND_EXCEPTION_MSG);
    }

    @Test
    @DisplayName("최애 삭제 실패 - 존재하지 않는 음식(게시글)")
    void deleteFavorite_Fail_FoodNotFound() throws Exception{
        //given
        willThrow(new FoodNotFoundException())
                .given(favoriteService).deleteFavorite(any(User.class), anyLong());

        //when
        RequestBuilder requestBuilder = delete(String.format("/api/v1/foods/%d/favorite", 1L))
                .header(HttpHeaders.AUTHORIZATION, "token");

        ResultActions perform = mockMvc.perform(requestBuilder);

        //then
        String errMsg = perform.andExpect(status().isNotFound())
                .andDo(documentIdentify("foods/favorite/delete/fail/userNotFound"))
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        assertThat(errMsg)
                .isNotNull()
                .isNotEmpty()
                .isEqualTo(FoodNotFoundException.FOOD_NOT_FOUND_EXCEPTION_MSG);
    }
}