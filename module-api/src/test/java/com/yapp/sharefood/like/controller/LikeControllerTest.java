package com.yapp.sharefood.like.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yapp.sharefood.common.PreprocessController;
import com.yapp.sharefood.common.exception.InvalidOperationException;
import com.yapp.sharefood.config.lock.UserlevelLock;
import com.yapp.sharefood.food.exception.FoodNotFoundException;
import com.yapp.sharefood.like.dto.request.LikeCreationRequest;
import com.yapp.sharefood.like.service.LikeService;
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

import static com.yapp.sharefood.common.documentation.DocumentationUtils.documentIdentify;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = LikeController.class)
class LikeControllerTest extends PreprocessController {
    @Autowired
    MockMvc mockMvc;

    @MockBean
    LikeService likeService;
    @MockBean
    UserlevelLock userlevelLock;

    ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DisplayName("게시글에 좋아요를 누른 경우")
    void createLikeTest_Success() throws Exception {
        // given
        willReturn(1L)
                .given(likeService).saveLike(any(User.class), anyLong(), anyString());
        willReturn(1L)
                .given(userlevelLock).executeWithLock(anyString(), anyInt(), any());

        // when
        String requestBodyStr = objectMapper.writeValueAsString(LikeCreationRequest.of("샌드위치"));
        ResultActions perform = mockMvc.perform(post(String.format("/api/v1/foods/%s/likes", 1L))
                .header(HttpHeaders.AUTHORIZATION, "token")
                .content(requestBodyStr)
                .contentType(MediaType.APPLICATION_JSON_VALUE));

        // then
        perform.andExpect(status().isCreated())
                .andDo(documentIdentify("food/like/post/success"))
                .andExpect(header().exists("Location"))
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);
    }

    @Test
    @DisplayName("이미 좋아요 누른 사람이 또 다시 좋아요 누른 케이스")
    void createLikeTest_DoubleLikePress_500InvalidOperationException() throws Exception {
        // given
        willThrow(InvalidOperationException.class)
                .given(likeService).saveLike(any(User.class), anyLong(), anyString());
        willThrow(InvalidOperationException.class)
                .given(userlevelLock).executeWithLock(anyString(), anyInt(), any());

        // when
        String requestBodyStr = objectMapper.writeValueAsString(LikeCreationRequest.of("샌드위치"));
        ResultActions perform = mockMvc.perform(post(String.format("/api/v1/foods/%s/likes", 1L))
                .header(HttpHeaders.AUTHORIZATION, "token")
                .content(requestBodyStr)
                .contentType(MediaType.APPLICATION_JSON_VALUE));

        // then
        perform.andExpect(status().isInternalServerError())
                .andDo(documentIdentify("food/like/post/invalidOperationException"))
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);
    }

    @Test
    @DisplayName("food category가 적절하지 않은 케이스")
    void createLikeTest_FoodNotExistInCategory_404FoodNotFound() throws Exception {
        willThrow(FoodNotFoundException.class)
                .given(likeService).saveLike(any(User.class), anyLong(), anyString());
        willThrow(FoodNotFoundException.class)
                .given(userlevelLock).executeWithLock(anyString(), anyInt(), any());

        // when
        String requestBodyStr = objectMapper.writeValueAsString(LikeCreationRequest.of("칵테일"));
        ResultActions perform = mockMvc.perform(post(String.format("/api/v1/foods/%s/likes", 1L))
                .header(HttpHeaders.AUTHORIZATION, "token")
                .content(requestBodyStr)
                .contentType(MediaType.APPLICATION_JSON_VALUE));

        // then
        perform.andExpect(status().isNotFound())
                .andDo(documentIdentify("food/like/post/foodNotFound"))
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);
    }

    @Test
    @DisplayName("좋아요 삭제하기 기능")
    void deleteLikeTest_Success() throws Exception {
        // given
        willDoNothing().given(likeService).deleteLike(any(User.class), anyLong(), anyString());
        willReturn(null).given(userlevelLock).executeWithLock(anyString(), anyInt(), any());

        // when
        ResultActions perform = mockMvc.perform(delete(String.format("/api/v1/foods/%s/likes", 1L))
                .header(HttpHeaders.AUTHORIZATION, "token")
                .param("categoryName", "샌드위치"));

        // then
        perform.andExpect(status().isOk())
                .andDo(documentIdentify("food/like/delete/success"));
    }

    @Test
    @DisplayName("이미 좋아요를 삭제한 게시글에 다시 삭제 요청")
    void deleteLikeTest_500InvalidateOperation() throws Exception {
        // given
        willThrow(InvalidOperationException.class)
                .given(likeService).deleteLike(any(User.class), anyLong(), anyString());
        willThrow(InvalidOperationException.class)
                .given(userlevelLock).executeWithLock(anyString(), anyInt(), any());

        // when
        ResultActions perform = mockMvc.perform(delete(String.format("/api/v1/foods/%s/likes", 1L))
                .header(HttpHeaders.AUTHORIZATION, "token")
                .param("categoryName", "샌드위치"));

        // then
        perform.andExpect(status().isInternalServerError())
                .andDo(documentIdentify("food/like/delete/invalidOperationException"));
    }

    @Test
    @DisplayName("이미 좋아요를 삭제한 게시글에 다시 삭제 요청")
    void deleteLikeTest_FoodNotExistInCategory_404FoodNotFoundException() throws Exception {
        // given
        willThrow(FoodNotFoundException.class)
                .given(likeService).deleteLike(any(User.class), anyLong(), anyString());
        willThrow(FoodNotFoundException.class)
                .given(userlevelLock).executeWithLock(anyString(), anyInt(), any());

        // when
        ResultActions perform = mockMvc.perform(delete(String.format("/api/v1/foods/%s/likes", 1L))
                .header(HttpHeaders.AUTHORIZATION, "token")
                .param("categoryName", "칵테일"));

        // then
        perform.andExpect(status().isNotFound())
                .andDo(documentIdentify("food/like/delete/foodNotFound"));
    }
}