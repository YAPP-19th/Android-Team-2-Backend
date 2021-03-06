package com.yapp.sharefood.food.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.yapp.sharefood.common.controller.PreprocessController;
import com.yapp.sharefood.common.exception.ForbiddenException;
import com.yapp.sharefood.food.domain.FoodIngredientType;
import com.yapp.sharefood.food.dto.FoodImageDto;
import com.yapp.sharefood.food.dto.FoodPageDto;
import com.yapp.sharefood.food.dto.FoodTagDto;
import com.yapp.sharefood.food.dto.request.FoodMinePageSearchRequest;
import com.yapp.sharefood.food.dto.request.FoodPageSearchRequest;
import com.yapp.sharefood.food.dto.request.FoodTopRankRequest;
import com.yapp.sharefood.food.dto.request.RecommendationFoodRequest;
import com.yapp.sharefood.food.dto.response.FoodDetailResponse;
import com.yapp.sharefood.food.dto.response.FoodPageResponse;
import com.yapp.sharefood.food.dto.response.RecommendationFoodResponse;
import com.yapp.sharefood.food.dto.response.TopRankFoodResponse;
import com.yapp.sharefood.food.exception.FoodNotFoundException;
import com.yapp.sharefood.user.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.ResultActions;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static com.yapp.sharefood.common.controller.documentation.DocumentationUtils.documentIdentify;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


class FoodControllerTest extends PreprocessController {

    @BeforeEach
    void setUp() {
        loginMockSetup();
    }

    @Test
    @DisplayName("food Top rank ?????? ??????")
    void findTopRankFoodTest_Success() throws Exception {
        // given
        List<FoodPageDto> mockFoodPageDtos = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            mockFoodPageDtos.add(FoodPageDto.builder()
                    .foodTitle("title_" + i)
                    .categoryName("????????????")
                    .price(1000 * i)
                    .numberOfLikes(10 - i)
                    .isMeBookmark(false)
                    .isMeLike(false)
                    .foodImages(List.of(new FoodImageDto(1L, "s3RealImageUrl.jpg", "????????????" + i + ".jpg")))
                    .build());
        }

        willReturn(TopRankFoodResponse.of(mockFoodPageDtos))
                .given(foodService).findTopRankFoods(any(FoodTopRankRequest.class), any(User.class));

        // when
        ResultActions perform = mockMvc.perform(get("/api/v1/foods/rank")
                .param("top", "5")
                .param("rankDatePeriod", "7")
                .param("categoryName", "??????")
                .header(HttpHeaders.AUTHORIZATION, "token"));

        // then
        TopRankFoodResponse topRankFoodResponse = objectMapper
                .readValue(perform.andExpect(status().isOk())
                        .andDo(documentIdentify("food-rank/get/success"))
                        .andReturn()
                        .getResponse()
                        .getContentAsString(StandardCharsets.UTF_8), new TypeReference<>() {
                });

        assertThat(topRankFoodResponse.getTopRankingFoods())
                .hasSize(5)
                .extracting("foodTitle")
                .containsExactlyInAnyOrderElementsOf(List.of("title_0", "title_1", "title_2", "title_3", "title_4"));
    }

    @MethodSource
    @ParameterizedTest(name = "food like rank ?????? top parameter ?????? ????????? ?????? ?????? ????????? ?????????")
    void findTopRankFoodTopParameterIssueTest_Fail_BadRequest(int testCaseIndex, int top) throws Exception {
        // given

        // when
        ResultActions perform = mockMvc.perform(get("/api/v1/foods/rank")
                .header(HttpHeaders.AUTHORIZATION, "token")
                .param("top", Integer.toString(top))
                .param("rankDatePeriod", "7")
                .param("categoryName", "??????"));

        // then
        perform.andExpect(status().isBadRequest())
                .andDo(documentIdentify(String.format("food-rank/get/fail/badRequest-top/%d", testCaseIndex)))
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);
    }

    static Stream<Arguments> findTopRankFoodTopParameterIssueTest_Fail_BadRequest() {
        return Stream.of(
                Arguments.of(1, 4),
                Arguments.of(2, 11)
        );
    }

    @MethodSource
    @ParameterizedTest(name = "food like rank ?????? rankDatePeriod parameter ?????? ????????? ?????? ?????? ????????? ?????????")
    void findTopRankFoodRankDatePeriodParameterIssueTest_Fail_BadRequest(int testCaseIndex, int rankDatePeriod) throws Exception {
        // given

        // when
        ResultActions perform = mockMvc.perform(get("/api/v1/foods/rank")
                .header(HttpHeaders.AUTHORIZATION, "token")
                .param("top", "5")
                .param("rankDatePeriod", Integer.toString(rankDatePeriod))
                .param("categoryName", "??????"));

        // then
        perform.andExpect(status().isBadRequest())
                .andDo(documentIdentify(String.format("food-rank/get/fail/badRequest-datePeriod/%d", testCaseIndex)))
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);
    }

    static Stream<Arguments> findTopRankFoodRankDatePeriodParameterIssueTest_Fail_BadRequest() {
        return Stream.of(
                Arguments.of(1, 2),
                Arguments.of(2, 11)
        );
    }

    @Test
    @DisplayName("food ?????? ?????? user flavor??? ?????? ?????? ????????? ???")
    void recommendationFoodTest_Success() throws Exception {
        // given
        List<FoodPageDto> mockFoodPageDtos = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            mockFoodPageDtos.add(FoodPageDto.builder()
                    .foodTitle("title_" + i)
                    .categoryName("????????????")
                    .price(1000 * i)
                    .numberOfLikes(10 - i)
                    .isMeBookmark(false)
                    .isMeLike(false)
                    .foodImages(List.of(new FoodImageDto(1L, "s3RealImageUrl.jpg", "????????????" + i + ".jpg")))
                    .build());
        }
        mockFoodPageDtos.sort((o1, o2) -> (int) (-o1.getNumberOfLikes() + o2.getNumberOfLikes()));
        willReturn(new RecommendationFoodResponse(mockFoodPageDtos))
                .given(foodService).findFoodRecommendation(any(RecommendationFoodRequest.class), any(User.class));

        // when
        ResultActions perform = mockMvc.perform(get("/api/v1/foods/recommendation")
                .param("top", "5")
                .param("rankDatePeriod", "7")
                .param("categoryName", "??????")
                .header(HttpHeaders.AUTHORIZATION, "token"));

        // then
        RecommendationFoodResponse recommendationFoodResponse = objectMapper
                .readValue(perform.andExpect(status().isOk())
                        .andDo(documentIdentify("food-recommendation/get/success"))
                        .andReturn()
                        .getResponse()
                        .getContentAsString(StandardCharsets.UTF_8), new TypeReference<>() {
                });

        assertThat(recommendationFoodResponse.getRecommendationFoods())
                .hasSize(5)
                .extracting("foodTitle")
                .containsExactlyInAnyOrderElementsOf(List.of("title_0", "title_1", "title_2", "title_3", "title_4"));
    }

    @MethodSource
    @ParameterizedTest(name = "food ?????? ?????? top parameter ?????? ????????? ?????? ?????? ????????? ?????????")
    void recommendationFoodTopParameterTest_Fail_BadRequest(int testCaseIndex, int top) throws Exception {
        // given

        // when
        ResultActions perform = mockMvc.perform(get("/api/v1/foods/recommendation")
                .header(HttpHeaders.AUTHORIZATION, "token")
                .param("top", Integer.toString(top))
                .param("rankDatePeriod", "7")
                .param("categoryName", "??????"));

        // then
        perform.andExpect(status().isBadRequest())
                .andDo(documentIdentify(String.format("food-recommendation/get/fail/badRequest-top/%d", testCaseIndex)))
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);
    }

    static Stream<Arguments> recommendationFoodTopParameterTest_Fail_BadRequest() {
        return Stream.of(
                Arguments.of(1, 4),
                Arguments.of(2, 11)
        );
    }

    @MethodSource
    @ParameterizedTest(name = "food ?????? ?????? rankDatePeriod parameter ?????? ????????? ?????? ?????? ????????? ?????????")
    void recommendationFoodRankDatePeriodIssueTest_Fail_BadRequest(int testCaseIndex, int rankDatePeriod) throws Exception {
        // given

        // when
        ResultActions perform = mockMvc.perform(get("/api/v1/foods/recommendation")
                .header(HttpHeaders.AUTHORIZATION, "token")
                .param("top", "5")
                .param("rankDatePeriod", Integer.toString(rankDatePeriod))
                .param("categoryName", "??????"));

        // then
        perform.andExpect(status().isBadRequest())
                .andDo(documentIdentify(String.format("food-recommendation/get/fail/badRequest-datePeriod/%d", testCaseIndex)))
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);
    }

    static Stream<Arguments> recommendationFoodRankDatePeriodIssueTest_Fail_BadRequest() {
        return Stream.of(
                Arguments.of(1, 2),
                Arguments.of(2, 11)
        );
    }

    @Test
    @DisplayName("?????? ?????? - ??????")
    void findFoodTest_Success() throws Exception {
        // given
        willReturn(FoodDetailResponse.builder()
                .id(1L)
                .foodTitle("title")
                .price(1000)
                .numberOfLike(2000)
                .reviewDetail("review Msg")
                .isMyFood(false)
                .isMeLike(false)
                .isMeBookmark(false)
                .categoryName("????????????")
                .writerName("writerName")
                .foodTags(List.of(FoodTagDto.of(1L, "tag1", FoodIngredientType.MAIN), FoodTagDto.of(2L, "tag2", FoodIngredientType.ADD), FoodTagDto.of(3L, "tag3", FoodIngredientType.EXTRACT)))
                .foodImages(List.of(new FoodImageDto(1L, "imageUrl1.jpg", "realImageName1.jpg"), new FoodImageDto(2L, "imageUrl2.jpg", "realImageName2.jpg")))
                .build()
        ).given(foodService).findFoodDetailById(any(User.class), anyLong());

        // when
        ResultActions perform = mockMvc.perform(get("/api/v1/foods/1")
                .header(HttpHeaders.AUTHORIZATION, "token"));

        // then
        FoodDetailResponse foodDetailResponse = objectMapper
                .readValue(perform
                        .andExpect(status().isOk())
                        .andDo(documentIdentify("food-detail/get/success"))
                        .andReturn()
                        .getResponse()
                        .getContentAsString(StandardCharsets.UTF_8), new TypeReference<>() {
                });

        assertEquals("title", foodDetailResponse.getFoodTitle());
        assertEquals("review Msg", foodDetailResponse.getReviewDetail());
        assertThat(foodDetailResponse.getFoodTags())
                .isNotNull()
                .hasSize(3)
                .extracting("name")
                .containsExactlyInAnyOrderElementsOf(List.of("tag1", "tag2", "tag3"));
    }

    @Test
    @DisplayName("?????? ?????? - ?????? not found")
    void findFoodTest_Exception_NotFound() throws Exception {
        // given
        willThrow(new FoodNotFoundException())
                .given(foodService).findFoodDetailById(any(User.class), anyLong());

        // when
        ResultActions perform = mockMvc.perform(get("/api/v1/foods/1")
                .header(HttpHeaders.AUTHORIZATION, "token"));

        // then
        perform.andExpect(status().isNotFound())
                .andDo(documentIdentify("food-detail/get/fail/foodNotFound"))
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);
    }

    @Test
    @DisplayName("?????? ?????? ?????? - ??????")
    void deleteFoodTest_Success() throws Exception {
        // given
        willDoNothing()
                .given(foodService).deleteFood(anyLong(), any(User.class));

        // when
        ResultActions perform = mockMvc.perform(delete("/api/v1/foods/1")
                .header(HttpHeaders.AUTHORIZATION, "token"));

        // then
        perform
                .andExpect(status().isOk())
                .andDo(documentIdentify("food/delete/success"))
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);
    }

    @Test
    @DisplayName("?????? ?????? ???????????? ?????? ?????? ?????? - ??????")
    void deleteFoodTest_Fail_Forbidden() throws Exception {
        // given
        willThrow(new ForbiddenException())
                .given(foodService).deleteFood(anyLong(), any(User.class));

        // when
        ResultActions perform = mockMvc.perform(delete("/api/v1/foods/1")
                .header(HttpHeaders.AUTHORIZATION, "token"));

        // then
        perform.andExpect(status().isForbidden())
                .andDo(documentIdentify("food/delete/fail/forbidden"))
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);
    }

    @Test
    @DisplayName("?????? ????????? ????????? ????????? ?????? - ??????")
    void deleteFoodTest_Fail_NotFound() throws Exception {
        // given
        willThrow(new FoodNotFoundException())
                .given(foodService).deleteFood(anyLong(), any(User.class));

        // when
        ResultActions perform = mockMvc.perform(delete("/api/v1/foods/1")
                .header(HttpHeaders.AUTHORIZATION, "token"));

        // then
        perform.andExpect(status().isNotFound())
                .andDo(documentIdentify("food/delete/fail/foodNotFound"))
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);
    }

    @Test
    @DisplayName("?????? page ?????? ?????? - ??????")
    void foodPageSearch_Success() throws Exception {
        // given
        List<FoodPageDto> foodpageContent = List.of(
                FoodPageDto.builder().
                        id(1L)
                        .foodTitle("title_1")
                        .categoryName("????????????")
                        .price(1000)
                        .numberOfLikes(12)
                        .isMeBookmark(false)
                        .isMeLike(false)
                        .foodImages(List.of(new FoodImageDto(1L, "imageUrl1.jpg", "realImageName1.jpg")))
                        .build(),
                FoodPageDto.builder().
                        id(2L)
                        .foodTitle("title_2")
                        .categoryName("????????????")
                        .price(2000)
                        .numberOfLikes(121)
                        .isMeBookmark(false)
                        .isMeLike(false)
                        .foodImages(List.of(new FoodImageDto(2L, "imageUrl2.jpg", "realImageName2.jpg")))
                        .build(),
                FoodPageDto.builder().
                        id(3L)
                        .foodTitle("title_3")
                        .categoryName("????????????")
                        .price(3000)
                        .numberOfLikes(1123)
                        .isMeBookmark(false)
                        .isMeLike(false)
                        .foodImages(List.of(new FoodImageDto(3L, "imageUrl3.jpg", "realImageName3.jpg")))
                        .build());
        willReturn(FoodPageResponse.ofPureDto(foodpageContent, 3, 0L))
                .given(foodService).searchFoodsPage(any(FoodPageSearchRequest.class), any(User.class));

        // when
        ResultActions perform = mockMvc.perform(get("/api/v1/foods")
                .param("minPrice", "0")
                .param("maxPrice", "100000")
                .param("tags", "a", "b")
                .param("flavors", "??????", "??????")
                .param("sort", "like")
                .param("order", "asc")
                .param("categoryName", "????????????")
                .param("firstSearchTime", "2021-12-25T12:12:12")
                .param("offset", "0")
                .param("pageSize", "3")
                .header(HttpHeaders.AUTHORIZATION, "token"));

        // then
        FoodPageResponse foodPageResponse = objectMapper
                .readValue(perform.andExpect(status().isOk())
                        .andDo(documentIdentify("food/get/success"))
                        .andReturn()
                        .getResponse()
                        .getContentAsString(StandardCharsets.UTF_8), new TypeReference<>() {
                });

        assertThat(foodPageResponse.getFoods())
                .hasSize(3)
                .extracting("foodTitle")
                .containsExactlyInAnyOrderElementsOf(List.of("title_1", "title_2", "title_3"));
        assertEquals(foodPageResponse.getPageSize(), 3);
        assertEquals(foodPageResponse.getOffset(), 0);
    }

    @Test
    @DisplayName("?????? ????????? food ??????")
    void findMineFoodController_Success() throws Exception {
        // given
        List<FoodPageDto> foodpageContent = List.of(
                FoodPageDto.builder().
                        id(1L)
                        .foodTitle("title_1")
                        .categoryName("????????????")
                        .price(1000)
                        .numberOfLikes(12)
                        .isMeBookmark(false)
                        .isMeLike(false)
                        .foodImages(List.of(new FoodImageDto(1L, "imageUrl1.jpg", "realImageName1.jpg")))
                        .build(),
                FoodPageDto.builder().
                        id(2L)
                        .foodTitle("title_2")
                        .categoryName("????????????")
                        .price(2000)
                        .numberOfLikes(121)
                        .isMeBookmark(false)
                        .isMeLike(false)
                        .foodImages(List.of(new FoodImageDto(2L, "imageUrl2.jpg", "realImageName2.jpg")))
                        .build(),
                FoodPageDto.builder().
                        id(3L)
                        .foodTitle("title_3")
                        .categoryName("????????????")
                        .price(3000)
                        .numberOfLikes(1123)
                        .isMeBookmark(false)
                        .isMeLike(false)
                        .foodImages(List.of(new FoodImageDto(3L, "imageUrl3.jpg", "realImageName3.jpg")))
                        .build());
        willReturn(FoodPageResponse.ofPureDto(foodpageContent, 3, 0L))
                .given(foodService).findOnlyMineFoods(any(User.class), any(FoodMinePageSearchRequest.class));

        // when
        ResultActions perform = mockMvc.perform(get("/api/v1/foods/me")
                .param("minPrice", "0")
                .param("maxPrice", "100000")
                .param("flavors", "??????", "??????")
                .param("sort", "like")
                .param("order", "asc")
                .param("categoryName", "????????????")
                .param("firstSearchTime", "2021-12-25T12:12:12")
                .param("offset", "0")
                .param("mineFoodType", "MYFOOD")
                .param("pageSize", "3")
                .header(HttpHeaders.AUTHORIZATION, "token"));

        // then
        FoodPageResponse foodPageResponse = objectMapper
                .readValue(perform.andExpect(status().isOk())
                        .andDo(documentIdentify("food-me/get/success/1"))
                        .andReturn()
                        .getResponse()
                        .getContentAsString(StandardCharsets.UTF_8), new TypeReference<>() {
                });

        assertThat(foodPageResponse.getFoods())
                .hasSize(3)
                .extracting("foodTitle")
                .containsExactlyInAnyOrderElementsOf(List.of("title_1", "title_2", "title_3"));
        assertEquals(foodPageResponse.getPageSize(), 3);
        assertEquals(foodPageResponse.getOffset(), 0);
    }

    @Test
    @DisplayName("?????? ????????? food ?????? ????????? category??? ??????")
    void findMineFoodControllerRootCategory_Success() throws Exception {
        // given
        List<FoodPageDto> foodpageContent = List.of(
                FoodPageDto.builder().
                        id(1L)
                        .foodTitle("title_1")
                        .categoryName("????????????")
                        .price(1000)
                        .numberOfLikes(12)
                        .isMeBookmark(false)
                        .isMeLike(false)
                        .foodImages(List.of(new FoodImageDto(1L, "imageUrl1.jpg", "realImageName1.jpg")))
                        .build(),
                FoodPageDto.builder().
                        id(2L)
                        .foodTitle("title_2")
                        .categoryName("?????????")
                        .price(2000)
                        .numberOfLikes(121)
                        .isMeBookmark(false)
                        .isMeLike(false)
                        .foodImages(List.of(new FoodImageDto(2L, "imageUrl2.jpg", "realImageName2.jpg")))
                        .build(),
                FoodPageDto.builder().
                        id(3L)
                        .foodTitle("title_3")
                        .categoryName("?????????")
                        .price(3000)
                        .numberOfLikes(1123)
                        .isMeBookmark(false)
                        .isMeLike(false)
                        .foodImages(List.of(new FoodImageDto(3L, "imageUrl3.jpg", "realImageName3.jpg")))
                        .build());
        willReturn(FoodPageResponse.ofPureDto(foodpageContent, 3, 0L))
                .given(foodService).findOnlyMineFoods(any(User.class), any(FoodMinePageSearchRequest.class));

        // when
        ResultActions perform = mockMvc.perform(get("/api/v1/foods/me")
                .param("minPrice", "0")
                .param("maxPrice", "100000")
                .param("flavors", "??????", "??????")
                .param("sort", "like")
                .param("order", "asc")
                .param("categoryName", "??????")
                .param("firstSearchTime", "2021-12-25T12:12:12")
                .param("offset", "0")
                .param("mineFoodType", "MYFOOD")
                .param("pageSize", "3")
                .param("status", "MINE")
                .header(HttpHeaders.AUTHORIZATION, "token"));

        // then
        FoodPageResponse foodPageResponse = objectMapper
                .readValue(perform.andExpect(status().isOk())
                        .andDo(documentIdentify("food-me/get/success/2"))
                        .andReturn()
                        .getResponse()
                        .getContentAsString(StandardCharsets.UTF_8), new TypeReference<>() {
                });

        assertThat(foodPageResponse.getFoods())
                .hasSize(3)
                .extracting("foodTitle", "categoryName")
                .containsExactlyInAnyOrderElementsOf(
                        List.of(tuple("title_1", "????????????"),
                                tuple("title_2", "?????????"),
                                tuple("title_3", "?????????")));

        assertEquals(foodPageResponse.getPageSize(), 3);
        assertEquals(foodPageResponse.getOffset(), 0);
    }

    @Test
    @DisplayName("Bookmark??? food ??????")
    void findBookmarkeFoodController_Success() throws Exception {
        // given
        List<FoodPageDto> foodpageContent = List.of(
                FoodPageDto.builder().
                        id(1L)
                        .foodTitle("title_1")
                        .categoryName("????????????")
                        .price(1000)
                        .numberOfLikes(12)
                        .isMeBookmark(true)
                        .isMeLike(false)
                        .foodImages(List.of(new FoodImageDto(1L, "imageUrl1.jpg", "realImageName1.jpg")))
                        .build(),
                FoodPageDto.builder().
                        id(2L)
                        .foodTitle("title_2")
                        .categoryName("????????????")
                        .price(2000)
                        .numberOfLikes(121)
                        .isMeBookmark(true)
                        .isMeLike(false)
                        .foodImages(List.of(new FoodImageDto(2L, "imageUrl2.jpg", "realImageName2.jpg")))
                        .build(),
                FoodPageDto.builder().
                        id(3L)
                        .foodTitle("title_3")
                        .categoryName("????????????")
                        .price(3000)
                        .numberOfLikes(1123)
                        .isMeBookmark(true)
                        .isMeLike(false)
                        .foodImages(List.of(new FoodImageDto(3L, "imageUrl3.jpg", "realImageName3.jpg")))
                        .build());
        willReturn(FoodPageResponse.ofPureDto(foodpageContent, 3, 0L))
                .given(foodService).findOnlyMineFoods(any(User.class), any(FoodMinePageSearchRequest.class));

        // when
        ResultActions perform = mockMvc.perform(get("/api/v1/foods/me")
                .param("minPrice", "0")
                .param("maxPrice", "100000")
                .param("flavors", "??????", "??????")
                .param("sort", "like")
                .param("order", "asc")
                .param("categoryName", "????????????")
                .param("firstSearchTime", "2021-12-25T12:12:12")
                .param("offset", "0")
                .param("mineFoodType", "BOOKMARK")
                .param("pageSize", "3")
                .param("status", "SHARED")
                .header(HttpHeaders.AUTHORIZATION, "token"));

        // then
        FoodPageResponse foodPageResponse = objectMapper
                .readValue(perform.andExpect(status().isOk())
                        .andDo(documentIdentify("food-me/get/success/3"))
                        .andReturn()
                        .getResponse()
                        .getContentAsString(StandardCharsets.UTF_8), new TypeReference<>() {
                });

        assertThat(foodPageResponse.getFoods())
                .hasSize(3)
                .extracting("foodTitle")
                .containsExactlyInAnyOrderElementsOf(List.of("title_1", "title_2", "title_3"));
        assertEquals(foodPageResponse.getPageSize(), 3);
        assertEquals(foodPageResponse.getOffset(), 0);
    }
}
