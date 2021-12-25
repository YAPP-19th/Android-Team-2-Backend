package com.yapp.sharefood.like.controller;

import com.yapp.sharefood.common.PreprocessController;
import com.yapp.sharefood.common.exception.ForbiddenException;
import com.yapp.sharefood.common.exception.InvalidOperationException;
import com.yapp.sharefood.config.lock.UserLevelLock;
import com.yapp.sharefood.food.exception.FoodNotFoundException;
import com.yapp.sharefood.like.service.LikeService;
import com.yapp.sharefood.user.domain.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.nio.charset.StandardCharsets;

import static com.yapp.sharefood.common.documentation.DocumentationUtils.documentIdentify;
import static org.assertj.core.api.Assertions.assertThat;
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
    UserLevelLock userlevelLock;

    @Test
    @DisplayName("게시글에 좋아요를 누른 경우")
    void createLikeTest_Success() throws Exception {
        // given
        willReturn(1L)
                .given(likeService).saveLike(any(User.class), anyLong());
        willReturn(1L)
                .given(userlevelLock).executeWithLock(anyString(), anyInt(), any());

        // when
        ResultActions perform = mockMvc.perform(post(String.format("/api/v1/foods/%s/likes", 1L))
                .header(HttpHeaders.AUTHORIZATION, "token"));

        // then
        perform.andExpect(status().isCreated())
                .andDo(documentIdentify("food-like/post/success"))
                .andExpect(header().exists("Location"))
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);
    }

    @Test
    @DisplayName("이미 좋아요 누른 사람이 또 다시 좋아요 누른 케이스")
    void createLikeTest_DoubleLikePress_500InvalidOperationException() throws Exception {
        // given
        willThrow(new InvalidOperationException("이미 like한 사용자 입니다."))
                .given(likeService).saveLike(any(User.class), anyLong());
        willThrow(new InvalidOperationException("이미 like한 사용자 입니다."))
                .given(userlevelLock).executeWithLock(anyString(), anyInt(), any());

        // when
        ResultActions perform = mockMvc.perform(post(String.format("/api/v1/foods/%s/likes", 1L))
                .header(HttpHeaders.AUTHORIZATION, "token"));

        // then
        String errorMsg = perform.andExpect(status().isInternalServerError())
                .andDo(documentIdentify("food-like/post/fail/invalidOperation"))
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        assertThat(errorMsg)
                .isNotNull()
                .isNotEmpty()
                .isEqualTo("이미 like한 사용자 입니다.");
    }

    @Test
    @DisplayName("food category가 적절하지 않은 케이스")
    void createLikeTest_FoodNotExistInCategory_404FoodNotFound() throws Exception {
        willThrow(new FoodNotFoundException())
                .given(likeService).saveLike(any(User.class), anyLong());
        willThrow(new FoodNotFoundException())
                .given(userlevelLock).executeWithLock(anyString(), anyInt(), any());

        // when
        ResultActions perform = mockMvc.perform(post(String.format("/api/v1/foods/%s/likes", 1L))
                .header(HttpHeaders.AUTHORIZATION, "token"));

        // then
        String errorMsg = perform.andExpect(status().isNotFound())
                .andDo(documentIdentify("food-like/post/fail/foodNotFound"))
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        assertThat(errorMsg)
                .isNotNull()
                .isNotEmpty()
                .isEqualTo(FoodNotFoundException.FOOD_NOT_FOUND_EXCEPTION_MSG);
    }

    @Test
    @DisplayName("좋아요 삭제하기 기능")
    void deleteLikeTest_Success() throws Exception {
        // given
        willDoNothing().given(likeService).deleteLike(any(User.class), anyLong());
        willReturn(null).given(userlevelLock).executeWithLock(anyString(), anyInt(), any());

        // when
        ResultActions perform = mockMvc.perform(delete(String.format("/api/v1/foods/%s/likes", 1L))
                .header(HttpHeaders.AUTHORIZATION, "token")
                .param("categoryName", "샌드위치"));

        // then
        perform.andExpect(status().isOk())
                .andDo(documentIdentify("food-like/delete/success"));
    }

    @Test
    @DisplayName("좋아요 등록되지 않는 food에 좋아요 삭제요청한 경우")
    void deleteLikeTest_403ForbiddenError() throws Exception {
        // given
        willThrow(new ForbiddenException())
                .given(likeService).deleteLike(any(User.class), anyLong());
        willThrow(new ForbiddenException())
                .given(userlevelLock).executeWithLock(anyString(), anyInt(), any());

        // when
        ResultActions perform = mockMvc.perform(delete(String.format("/api/v1/foods/%s/likes", 1L))
                .header(HttpHeaders.AUTHORIZATION, "token")
                .param("categoryName", "샌드위치"));

        // then
        String errorMsg = perform.andExpect(status().isForbidden())
                .andDo(documentIdentify("food-like/delete/fail/forbidden"))
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        assertThat(errorMsg)
                .isNotNull()
                .isNotEmpty()
                .isEqualTo(ForbiddenException.FORBIDDEN_EXCEPTION_MSG);
    }

    @Test
    @DisplayName("이미 좋아요를 삭제한 게시글에 다시 삭제 요청")
    void deleteLikeTest_FoodNotExistInCategory_404FoodNotFoundException() throws Exception {
        // given
        willThrow(new FoodNotFoundException())
                .given(likeService).deleteLike(any(User.class), anyLong());
        willThrow(new FoodNotFoundException())
                .given(userlevelLock).executeWithLock(anyString(), anyInt(), any());

        // when
        ResultActions perform = mockMvc.perform(delete(String.format("/api/v1/foods/%s/likes", 1L))
                .header(HttpHeaders.AUTHORIZATION, "token")
                .param("categoryName", "칵테일"));

        // then
        String errorMsg = perform.andExpect(status().isNotFound())
                .andDo(documentIdentify("food-like/delete/fail/foodNotFound"))
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        assertThat(errorMsg)
                .isNotNull()
                .isNotEmpty()
                .isEqualTo(FoodNotFoundException.FOOD_NOT_FOUND_EXCEPTION_MSG);
    }
}