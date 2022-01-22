package com.yapp.sharefood.bookmark.controller;

import com.yapp.sharefood.bookmark.exception.BookmarkAlreadyExistException;
import com.yapp.sharefood.bookmark.exception.BookmarkNotFoundException;
import com.yapp.sharefood.common.controller.PreprocessController;
import com.yapp.sharefood.food.exception.FoodNotFoundException;
import com.yapp.sharefood.user.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultActions;

import java.nio.charset.StandardCharsets;

import static com.yapp.sharefood.common.controller.documentation.DocumentationUtils.documentIdentify;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.willReturn;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class BookmarkControllerTest extends PreprocessController {

    @BeforeEach
    void setUp() {
        loginMockSetup();
    }

    @DisplayName("북마크 추가 성공")
    @Test
    void createBookmarkTest_Success() throws Exception {
        //given
        willReturn(1L)
                .given(bookmarkService).saveBookmark(any(User.class), anyLong());

        //when
        RequestBuilder requestBuilder =
                post(String.format("/api/v1/foods/%d/bookmark", 1L))
                        .header(HttpHeaders.AUTHORIZATION, "token");

        ResultActions perform = mockMvc.perform(requestBuilder);

        //then
        perform.andExpect(status().isCreated())
                .andDo(documentIdentify("food-bookmark/post/success"))
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);
    }

    @DisplayName("음식이 없어서 추가에 실패한 경우")
    @Test
    void createBookmarkTest_Fail_FoodNotFound() throws Exception {
        //given
        willThrow(new FoodNotFoundException())
                .given(bookmarkService).saveBookmark(any(User.class), anyLong());

        //when
        RequestBuilder requestBuilder =
                post(String.format("/api/v1/foods/%d/bookmark", 1L))
                        .header(HttpHeaders.AUTHORIZATION, "token");

        ResultActions perform = mockMvc.perform(requestBuilder);

        //then
        String errMsg = perform.andExpect(status().isNotFound())
                .andDo(documentIdentify("food-bookmark/post/fail/foodNotFound"))
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        String expectMsg = FoodNotFoundException.FOOD_NOT_FOUND_EXCEPTION_MSG;
        assertThat(errMsg)
                .isNotNull()
                .isNotEmpty()
                .isEqualTo(expectMsg);
    }

    @DisplayName("북마크를 추가했는데 또 추가하는 경우")
    @Test
    void createBookmarkTest_Fail_BookmarkAlreadyExist() throws Exception {
        //given

        willThrow(new BookmarkAlreadyExistException())
                .given(bookmarkService).saveBookmark(any(User.class), anyLong());

        //when
        RequestBuilder requestBuilder =
                post(String.format("/api/v1/foods/%d/bookmark", 1L))
                        .header(HttpHeaders.AUTHORIZATION, "token");

        ResultActions perform = mockMvc.perform(requestBuilder);

        //then
        String errMsg = perform.andExpect(status().isConflict())
                .andDo(documentIdentify("food-bookmark/post/fail/alreadyExist"))
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        String expectMsg = BookmarkAlreadyExistException.BOOKMARK_ALREADY_EXIST_MSG;
        assertThat(errMsg)
                .isNotNull()
                .isNotEmpty()
                .isEqualTo(expectMsg);
    }

    @DisplayName("북마크 제거 성공")
    @Test
    void deleteBookmarkTest_Success() throws Exception {
        //given

        //when
        RequestBuilder requestBuilder =
                delete(String.format("/api/v1/foods/%d/bookmark", 1L))
                        .header(HttpHeaders.AUTHORIZATION, "token");

        ResultActions perform = mockMvc.perform(requestBuilder);

        //then
        perform.andExpect(status().isOk())
                .andDo(documentIdentify("food-bookmark/delete/success"))
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);
    }

    @DisplayName("음식이 없어서 삭제에 실패한 경우")
    @Test
    void deleteBookmarkTest_Fail_FoodNotFound() throws Exception {
        //given
        willThrow(new FoodNotFoundException())
                .given(bookmarkService).deleteBookmark(any(User.class), anyLong());

        //when
        RequestBuilder requestBuilder =
                delete(String.format("/api/v1/foods/%d/bookmark", 1L))
                        .header(HttpHeaders.AUTHORIZATION, "token");

        ResultActions perform = mockMvc.perform(requestBuilder);

        //then
        String errMsg = perform.andExpect(status().isNotFound())
                .andDo(documentIdentify("food-bookmark/delete/fail/foodNotFound"))
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        String expectMsg = FoodNotFoundException.FOOD_NOT_FOUND_EXCEPTION_MSG;
        assertThat(errMsg)
                .isNotNull()
                .isNotEmpty()
                .isEqualTo(expectMsg);
    }

    @DisplayName("북마크가 없는데 또 삭제를 요청한 경우")
    @Test
    void deleteBookmarkTest_Fail_BookmarkNotFound() throws Exception {
        //given
        willThrow(new BookmarkNotFoundException())
                .given(bookmarkService).deleteBookmark(any(User.class), anyLong());

        //when
        RequestBuilder requestBuilder =
                delete(String.format("/api/v1/foods/%d/bookmark", 1L))
                        .header(HttpHeaders.AUTHORIZATION, "token");

        ResultActions perform = mockMvc.perform(requestBuilder);

        //then
        String errMsg = perform.andExpect(status().isNotFound())
                .andDo(documentIdentify("food-bookmark/delete/fail/bookmarkNotFound"))
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        String expectMsg = BookmarkNotFoundException.BOOKMARK_NOT_FOUND_EXCEPTION_MSG;
        assertThat(errMsg)
                .isNotNull()
                .isNotEmpty()
                .isEqualTo(expectMsg);
    }

}
