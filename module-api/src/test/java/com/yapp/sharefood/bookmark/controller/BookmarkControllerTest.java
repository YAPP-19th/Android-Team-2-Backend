package com.yapp.sharefood.bookmark.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yapp.sharefood.bookmark.exception.BookmarkAlreadyExistException;
import com.yapp.sharefood.bookmark.exception.BookmarkNotFoundException;
import com.yapp.sharefood.bookmark.service.BookmarkService;
import com.yapp.sharefood.common.PreprocessController;
import com.yapp.sharefood.food.exception.FoodNotFoundException;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.willReturn;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookmarkController.class)
class BookmarkControllerTest extends PreprocessController {
    @MockBean
    BookmarkService bookmarkService;

    ObjectMapper objectMapper = new ObjectMapper();

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
                .andDo(documentIdentify("bookmark/post/success"))
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);
    }

    @DisplayName("음식이 없어서 추가에 실패한 경우")
    @Test
    void createBookmarkTest_Fail_FoodNotFound() throws Exception {
        //given
        willThrow(FoodNotFoundException.class)
                .given(bookmarkService).saveBookmark(any(User.class), anyLong());

        //when
        RequestBuilder requestBuilder =
                post(String.format("/api/v1/foods/%d/bookmark", 1L))
                        .header(HttpHeaders.AUTHORIZATION, "token");

        ResultActions perform = mockMvc.perform(requestBuilder);

        //then
        perform.andExpect(status().isNotFound())
                .andDo(documentIdentify("bookmark/post/fail/notFound"))
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);
    }

    @DisplayName("북마크를 추가했는데 또 추가하는 경우")
    @Test
    void createBookmarkTest_Fail_BookmarkAlreadyExist() throws Exception {
        //given
        willThrow(BookmarkAlreadyExistException.class)
                .given(bookmarkService).saveBookmark(any(User.class), anyLong());

        //when
        RequestBuilder requestBuilder =
                post(String.format("/api/v1/foods/%d/bookmark", 1L))
                        .header(HttpHeaders.AUTHORIZATION, "token");

        ResultActions perform = mockMvc.perform(requestBuilder);

        //then
        perform.andExpect(status().isConflict())
                .andDo(documentIdentify("bookmark/post/fail/alreadyExist"))
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);
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
                .andDo(documentIdentify("bookmark/delete/success"))
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);
    }

    @DisplayName("음식이 없어서 삭제에 실패한 경우")
    @Test
    void deleteBookmarkTest_Fail_FoodNotFound() throws Exception {
        //given
        willThrow(FoodNotFoundException.class)
                .given(bookmarkService).deleteBookmark(any(User.class), anyLong());

        //when
        RequestBuilder requestBuilder =
                delete(String.format("/api/v1/foods/%d/bookmark", 1L))
                        .header(HttpHeaders.AUTHORIZATION, "token");

        ResultActions perform = mockMvc.perform(requestBuilder);

        //then
        perform.andExpect(status().isNotFound())
                .andDo(documentIdentify("bookmark/delete/fail/notFound/food"))
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);
    }

    @DisplayName("북마크가 없는데 또 삭제를 요청한 경우")
    @Test
    void deleteBookmarkTest_Fail_BookmarkNotFound() throws Exception {
        //given
        willThrow(BookmarkNotFoundException.class)
                .given(bookmarkService).deleteBookmark(any(User.class), anyLong());

        //when
        RequestBuilder requestBuilder =
                delete(String.format("/api/v1/foods/%d/bookmark", 1L))
                        .header(HttpHeaders.AUTHORIZATION, "token");

        ResultActions perform = mockMvc.perform(requestBuilder);

        //then
        perform.andExpect(status().isNotFound())
                .andDo(documentIdentify("bookmark/delete/fail/notFound/bookmark"))
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);
    }

}
