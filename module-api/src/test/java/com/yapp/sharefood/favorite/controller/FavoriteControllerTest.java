package com.yapp.sharefood.favorite.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.yapp.sharefood.common.controller.PreprocessController;
import com.yapp.sharefood.favorite.dto.FavoriteFoodDto;
import com.yapp.sharefood.favorite.dto.request.FavoriteCreationRequest;
import com.yapp.sharefood.favorite.dto.response.FavoriteFoodResponse;
import com.yapp.sharefood.favorite.exception.TooManyFavoriteException;
import com.yapp.sharefood.food.exception.FoodNotFoundException;
import com.yapp.sharefood.oauth.exception.UserNotFoundException;
import com.yapp.sharefood.user.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultActions;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

import static com.yapp.sharefood.common.controller.documentation.DocumentationUtils.documentIdentify;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.willReturn;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


class FavoriteControllerTest extends PreprocessController {

    @BeforeEach
    void setUp() {
        loginMockSetup();
    }

    @Test
    @DisplayName("최애 생성 - 성공")
    void createFavorite_Success() throws Exception {
        //given
        willReturn(1L)
                .given(favoriteService).createFavorite(any(User.class), anyLong(), any(FavoriteCreationRequest.class));
        String requestBodyStr = objectMapper.writeValueAsString(FavoriteCreationRequest.of("음식"));

        //when
        RequestBuilder requestBuilder = post(String.format("/api/v1/foods/%d/favorite", 1L))
                .header(HttpHeaders.AUTHORIZATION, "token")
                .content(requestBodyStr)
                .contentType(MediaType.APPLICATION_JSON);

        ResultActions perform = mockMvc.perform(requestBuilder);

        //then
        perform.andExpect(status().isCreated())
                .andDo(documentIdentify("food-favorite/post/success"))
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);
    }

    @Test
    @DisplayName("최애 생성 실패 - 존재하지 않는 사용자")
    void createFavorite_Fail_UserNotFound() throws Exception {
        //given
        willThrow(new UserNotFoundException())
                .given(favoriteService).createFavorite(any(User.class), anyLong(), any(FavoriteCreationRequest.class));
        String requestBodyStr = objectMapper.writeValueAsString(FavoriteCreationRequest.of("음식"));

        //when
        RequestBuilder requestBuilder = post(String.format("/api/v1/foods/%d/favorite", 1L))
                .header(HttpHeaders.AUTHORIZATION, "token")
                .content(requestBodyStr)
                .contentType(MediaType.APPLICATION_JSON);

        ResultActions perform = mockMvc.perform(requestBuilder);

        //then
        perform.andExpect(status().isNotFound())
                .andDo(documentIdentify("food-favorite/post/fail/userNotFound"))
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);
    }

    @Test
    @DisplayName("최애 생성 실패 - 존재하지 않는 음식(게시글)")
    void createFavorite_Fail_FoodNotFound() throws Exception {
        //given
        willThrow(new FoodNotFoundException())
                .given(favoriteService).createFavorite(any(User.class), anyLong(), any(FavoriteCreationRequest.class));
        String requestBodyStr = objectMapper.writeValueAsString(FavoriteCreationRequest.of("음식"));

        //when
        RequestBuilder requestBuilder = post(String.format("/api/v1/foods/%d/favorite", 1L))
                .header(HttpHeaders.AUTHORIZATION, "token")
                .content(requestBodyStr)
                .contentType(MediaType.APPLICATION_JSON);

        ResultActions perform = mockMvc.perform(requestBuilder);

        //then
        perform.andExpect(status().isNotFound())
                .andDo(documentIdentify("food-favorite/post/fail/foodNotFound"))
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);
    }

    @Test
    @DisplayName("최애 생성 실패 - 존재하지 않는 음식(게시글)")
    void createFavoriteTest_Fail_TooManyFavorite() throws Exception {
        //given
        willThrow(new TooManyFavoriteException())
                .given(favoriteService).createFavorite(any(User.class), anyLong(), any(FavoriteCreationRequest.class));
        String requestBodyStr = objectMapper.writeValueAsString(FavoriteCreationRequest.of("음식"));

        //when
        RequestBuilder requestBuilder = post(String.format("/api/v1/foods/%d/favorite", 1L))
                .header(HttpHeaders.AUTHORIZATION, "token")
                .content(requestBodyStr)
                .contentType(MediaType.APPLICATION_JSON);

        ResultActions perform = mockMvc.perform(requestBuilder);

        //then
        perform.andExpect(status().isTooManyRequests())
                .andDo(documentIdentify("food-favorite/post/fail/tooManyFavorite"))
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);
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
                .andDo(documentIdentify("food-favorite/delete/success"))
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);
    }

    @Test
    @DisplayName("최애 삭제 실패 - 존재하지 않는 사용자")
    void deleteFavorite_Fail_UserNotFound() throws Exception {
        //given
        willThrow(new UserNotFoundException())
                .given(favoriteService).deleteFavorite(any(User.class), anyLong());

        //when
        RequestBuilder requestBuilder = delete(String.format("/api/v1/foods/%d/favorite", 1L))
                .header(HttpHeaders.AUTHORIZATION, "token");

        ResultActions perform = mockMvc.perform(requestBuilder);

        //then
        perform.andExpect(status().isNotFound())
                .andDo(documentIdentify("food-favorite/delete/fail/userNotFound"))
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);
    }

    @Test
    @DisplayName("최애 삭제 실패 - 존재하지 않는 음식(게시글)")
    void deleteFavorite_Fail_FoodNotFound() throws Exception {
        //given
        willThrow(new FoodNotFoundException())
                .given(favoriteService).deleteFavorite(any(User.class), anyLong());

        //when
        RequestBuilder requestBuilder = delete(String.format("/api/v1/foods/%d/favorite", 1L))
                .header(HttpHeaders.AUTHORIZATION, "token");

        ResultActions perform = mockMvc.perform(requestBuilder);

        //then
        perform.andExpect(status().isNotFound())
                .andDo(documentIdentify("food-favorite/delete/fail/foodNotFound"))
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);
    }

    @Test
    @DisplayName("최애 조회 성공")
    void findFavoriteTest_Success() throws Exception {
        //given

        FavoriteFoodDto dto1 = FavoriteFoodDto.of(1L, "title1", "샌드위치", 10000, 10L, true, Collections.emptyList());
        FavoriteFoodDto dto2 = FavoriteFoodDto.of(2L, "title2", "마라탕", 20000, 3L, true, Collections.emptyList());
        FavoriteFoodDto dto3 = FavoriteFoodDto.of(3L, "title3", "샐러드", 30000, 2L, true, Collections.emptyList());
        willReturn(FavoriteFoodResponse.of(List.of(dto1, dto2, dto3)))
                .given(favoriteService).findFavoriteFoods(any(User.class), anyString());

        //when
        RequestBuilder requestBuilder = get("/api/v1/foods/favorite")
                .header(HttpHeaders.AUTHORIZATION, "token")
                .param("categoryName", "음식");

        ResultActions perform = mockMvc.perform(requestBuilder);

        //then
        FavoriteFoodResponse resultResponse = objectMapper.readValue(
                perform.andExpect(status().isOk())
                        .andDo(documentIdentify("food-favorite/get/success"))
                        .andReturn()
                        .getResponse()
                        .getContentAsString(StandardCharsets.UTF_8), new TypeReference<>() {
                }
        );


        assertThat(resultResponse.getFavoriteFoods())
                .hasSize(3)
                .extracting("id", "foodTitle", "price", "numberOfLikes", "isMeFavorite")
                .containsExactlyInAnyOrderElementsOf(List.of(
                        tuple(1L, "title1", 10000, 10L, true),
                        tuple(2L, "title2", 20000, 3L, true),
                        tuple(3L, "title3", 30000, 2L, true)
                ));
    }

    @Test
    @DisplayName("최애 조회 실패 - 존재하지 않는 유저")
    void findFavoriteTest_Fail_UserNotFound() throws Exception {
        //given
        willThrow(new UserNotFoundException())
                .given(favoriteService).findFavoriteFoods(any(User.class), anyString());

        //when
        RequestBuilder requestBuilder = get("/api/v1/foods/favorite")
                .header(HttpHeaders.AUTHORIZATION, "token")
                .param("categoryName", "음식");

        ResultActions perform = mockMvc.perform(requestBuilder);

        //then
        perform.andExpect(status().isNotFound())
                .andDo(documentIdentify("food-favorite/get/fail/userNotFound"))
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);
    }
}