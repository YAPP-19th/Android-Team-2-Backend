package com.yapp.sharefood.food.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yapp.sharefood.common.PreprocessController;
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
import com.yapp.sharefood.food.service.FoodImageService;
import com.yapp.sharefood.food.service.FoodService;
import com.yapp.sharefood.user.domain.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static com.yapp.sharefood.common.documentation.DocumentationUtils.documentIdentify;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(controllers = FoodController.class)
public class FoodControllerTest extends PreprocessController {
    @Autowired
    MockMvc mockMvc;

    @MockBean
    FoodService foodService;
    @MockBean
    FoodImageService foodImageService;
    ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DisplayName("food Top rank 조회 기능")
    void findTopRankFoodTest_Success() throws Exception {
        // given
        List<FoodPageDto> mockFoodPageDtos = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            mockFoodPageDtos.add(FoodPageDto.builder()
                    .foodTitle("title_" + i)
                    .categoryName("샌드위치")
                    .price(1000 * i)
                    .numberOfLikes(10 - i)
                    .isMeBookmark(false)
                    .isMeLike(false)
                    .foodImages(List.of(new FoodImageDto(1L, "s3RealImageUrl.jpg", "음식사진" + i + ".jpg")))
                    .build());
        }

        willReturn(TopRankFoodResponse.of(mockFoodPageDtos))
                .given(foodService).findTopRankFoods(any(FoodTopRankRequest.class), any(User.class));

        // when
        ResultActions perform = mockMvc.perform(get("/api/v1/foods/rank")
                .param("top", "5")
                .param("rankDatePeriod", "7")
                .param("categoryName", "음식")
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
    @ParameterizedTest(name = "food like rank 조회 top parameter 최소 최대를 넘는 이슈 케이스 테스트")
    void findTopRankFoodTopParameterIssueTest_Fail_BadRequest(int top) throws Exception {
        // given

        // when
        ResultActions perform = mockMvc.perform(get("/api/v1/foods/rank")
                .header(HttpHeaders.AUTHORIZATION, "token")
                .param("top", Integer.toString(top))
                .param("rankDatePeriod", "7")
                .param("categoryName", "음식"));

        // then
        String errorMsg = perform.andExpect(status().isBadRequest())
                .andDo(documentIdentify("food-rank/get/fail/badRequest-top"))
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        assertThat(errorMsg)
                .isNotNull();
    }

    static Stream<Arguments> findTopRankFoodTopParameterIssueTest_Fail_BadRequest() {
        return Stream.of(
                Arguments.of(4),
                Arguments.of(11)
        );
    }

    @MethodSource
    @ParameterizedTest(name = "food like rank 조회 rankDatePeriod parameter 최소 최대를 넘는 이슈 케이스 테스트")
    void findTopRankFoodRankDatePeriodParameterIssueTest_Fail_BadRequest(int rankDatePeriod) throws Exception {
        // given

        // when
        ResultActions perform = mockMvc.perform(get("/api/v1/foods/rank")
                .header(HttpHeaders.AUTHORIZATION, "token")
                .param("top", "5")
                .param("rankDatePeriod", Integer.toString(rankDatePeriod))
                .param("categoryName", "음식"));

        // then
        String errorMsg = perform.andExpect(status().isBadRequest())
                .andDo(documentIdentify("food-rank/get/fail/badRequest-datePeriod"))
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        assertThat(errorMsg)
                .isNotNull();
    }

    static Stream<Arguments> findTopRankFoodRankDatePeriodParameterIssueTest_Fail_BadRequest() {
        return Stream.of(
                Arguments.of(2),
                Arguments.of(11)
        );
    }

    @Test
    @DisplayName("food 추천 기능 user flavor가 설정 되어 있어야 함")
    void recommendationFoodTest_Success() throws Exception {
        // given
        List<FoodPageDto> mockFoodPageDtos = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            mockFoodPageDtos.add(FoodPageDto.builder()
                    .foodTitle("title_" + i)
                    .categoryName("샌드위치")
                    .price(1000 * i)
                    .numberOfLikes(10 - i)
                    .isMeBookmark(false)
                    .isMeLike(false)
                    .foodImages(List.of(new FoodImageDto(1L, "s3RealImageUrl.jpg", "음식사진" + i + ".jpg")))
                    .build());
        }
        mockFoodPageDtos.sort((o1, o2) -> (int) (-o1.getNumberOfLikes() + o2.getNumberOfLikes()));
        willReturn(new RecommendationFoodResponse(mockFoodPageDtos))
                .given(foodService).findFoodRecommendation(any(RecommendationFoodRequest.class), any(User.class));

        // when
        ResultActions perform = mockMvc.perform(get("/api/v1/foods/recommendation")
                .param("top", "5")
                .param("rankDatePeriod", "7")
                .param("categoryName", "음식")
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
    @ParameterizedTest(name = "food 추천 조회 top parameter 최소 최대를 넘는 이슈 케이스 테스트")
    void recommendationFoodTopParameterTest_Fail_BadRequest(int top) throws Exception {
        // given

        // when
        ResultActions perform = mockMvc.perform(get("/api/v1/foods/recommendation")
                .header(HttpHeaders.AUTHORIZATION, "token")
                .param("top", Integer.toString(top))
                .param("rankDatePeriod", "7")
                .param("categoryName", "음식"));

        // then
        String errorMsg = perform.andExpect(status().isBadRequest())
                .andDo(documentIdentify("food-recommendation/get/fail/badRequest-top"))
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        assertThat(errorMsg)
                .isNotNull();
    }

    static Stream<Arguments> recommendationFoodTopParameterTest_Fail_BadRequest() {
        return Stream.of(
                Arguments.of(4),
                Arguments.of(11)
        );
    }

    @MethodSource
    @ParameterizedTest(name = "food 추천 조회 rankDatePeriod parameter 최소 최대를 넘는 이슈 케이스 테스트")
    void recommendationFoodRankDatePeriodIssueTest_Fail_BadRequest(int rankDatePeriod) throws Exception {
        // given

        // when
        ResultActions perform = mockMvc.perform(get("/api/v1/foods/recommendation")
                .header(HttpHeaders.AUTHORIZATION, "token")
                .param("top", "5")
                .param("rankDatePeriod", Integer.toString(rankDatePeriod))
                .param("categoryName", "음식"));

        // then
        String errorMsg = perform.andExpect(status().isBadRequest())
                .andDo(documentIdentify("food-recommendation/get/fail/badRequest-datePeriod"))
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        assertThat(errorMsg)
                .isNotNull();
    }

    static Stream<Arguments> recommendationFoodRankDatePeriodIssueTest_Fail_BadRequest() {
        return Stream.of(
                Arguments.of(2),
                Arguments.of(11)
        );
    }

    @Test
    @DisplayName("음식 조회 - 성공")
    void findFoodTest_Success() throws Exception {
        // given
        willReturn(FoodDetailResponse.builder()
                .id(1L)
                .foodTitle("title")
                .price(1000)
                .numberOfLike(2000)
                .reviewDetail("review Msg")
                .isMeLike(false)
                .isMeBookmark(false)
                .categoryName("샌드위치")
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
    @DisplayName("음식 조회 - 에러 not found")
    void findFoodTest_Exception_NotFound() throws Exception {
        // given
        willThrow(new FoodNotFoundException())
                .given(foodService).findFoodDetailById(any(User.class), anyLong());

        // when
        ResultActions perform = mockMvc.perform(get("/api/v1/foods/1")
                .header(HttpHeaders.AUTHORIZATION, "token"));

        // then
        String errorMsg = perform
                .andExpect(status().isNotFound())
                .andDo(documentIdentify("food-detail/get/fail/foodNotFound"))
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        assertThat(errorMsg)
                .isNotNull()
                .isEqualTo(FoodNotFoundException.FOOD_NOT_FOUND_EXCEPTION_MSG);
    }

    @Test
    @DisplayName("음식 삭제 기능 - 성공")
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
    @DisplayName("음식 내가 작성하지 않은 음식 삭제 - 에러")
    void deleteFoodTest_Fail_Forbidden() throws Exception {
        // given
        willThrow(new ForbiddenException())
                .given(foodService).deleteFood(anyLong(), any(User.class));

        // when
        ResultActions perform = mockMvc.perform(delete("/api/v1/foods/1")
                .header(HttpHeaders.AUTHORIZATION, "token"));

        // then
        String errorMsg = perform
                .andExpect(status().isForbidden())
                .andDo(documentIdentify("food/delete/fail/forbidden"))
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        assertThat(errorMsg)
                .isNotNull()
                .isEqualTo(ForbiddenException.FORBIDDEN_EXCEPTION_MSG);
    }

    @Test
    @DisplayName("이미 삭제된 음식을 삭제할 경우 - 에러")
    void deleteFoodTest_Fail_NotFound() throws Exception {
        // given
        willThrow(new FoodNotFoundException())
                .given(foodService).deleteFood(anyLong(), any(User.class));

        // when
        ResultActions perform = mockMvc.perform(delete("/api/v1/foods/1")
                .header(HttpHeaders.AUTHORIZATION, "token"));

        // then
        String errorMsg = perform
                .andExpect(status().isNotFound())
                .andDo(documentIdentify("food/delete/fail/foodNotFound"))
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        assertThat(errorMsg)
                .isNotNull()
                .isEqualTo(FoodNotFoundException.FOOD_NOT_FOUND_EXCEPTION_MSG);
    }

    @Test
    @DisplayName("음식 page 조회 기능 - 성공")
    void foodPageSearch_Success() throws Exception {
        // given
        List<FoodPageDto> foodpageContent = List.of(
                FoodPageDto.builder().
                        id(1L)
                        .foodTitle("title_1")
                        .categoryName("샌드위치")
                        .price(1000)
                        .numberOfLikes(12)
                        .isMeBookmark(false)
                        .isMeLike(false)
                        .foodImages(List.of(new FoodImageDto(1L, "imageUrl1.jpg", "realImageName1.jpg")))
                        .build(),
                FoodPageDto.builder().
                        id(2L)
                        .foodTitle("title_2")
                        .categoryName("샌드위치")
                        .price(2000)
                        .numberOfLikes(121)
                        .isMeBookmark(false)
                        .isMeLike(false)
                        .foodImages(List.of(new FoodImageDto(2L, "imageUrl2.jpg", "realImageName2.jpg")))
                        .build(),
                FoodPageDto.builder().
                        id(3L)
                        .foodTitle("title_3")
                        .categoryName("샌드위치")
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
                .param("flavors", "단맛", "짠맛")
                .param("sort", "like")
                .param("order", "asc")
                .param("categoryName", "샌드위치")
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
    @DisplayName("내가 작성한 food 조회")
    void findMineFoodController_Success() throws Exception {
        // given
        List<FoodPageDto> foodpageContent = List.of(
                FoodPageDto.builder().
                        id(1L)
                        .foodTitle("title_1")
                        .categoryName("샌드위치")
                        .price(1000)
                        .numberOfLikes(12)
                        .isMeBookmark(false)
                        .isMeLike(false)
                        .foodImages(List.of(new FoodImageDto(1L, "imageUrl1.jpg", "realImageName1.jpg")))
                        .build(),
                FoodPageDto.builder().
                        id(2L)
                        .foodTitle("title_2")
                        .categoryName("샌드위치")
                        .price(2000)
                        .numberOfLikes(121)
                        .isMeBookmark(false)
                        .isMeLike(false)
                        .foodImages(List.of(new FoodImageDto(2L, "imageUrl2.jpg", "realImageName2.jpg")))
                        .build(),
                FoodPageDto.builder().
                        id(3L)
                        .foodTitle("title_3")
                        .categoryName("샌드위치")
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
                .param("flavors", "단맛", "짠맛")
                .param("sort", "like")
                .param("order", "asc")
                .param("categoryName", "샌드위치")
                .param("firstSearchTime", "2021-12-25T12:12:12")
                .param("offset", "0")
                .param("mineFoodType", "MYFOOD")
                .param("pageSize", "3")
                .header(HttpHeaders.AUTHORIZATION, "token"));

        // then
        FoodPageResponse foodPageResponse = objectMapper
                .readValue(perform.andExpect(status().isOk())
                        .andDo(documentIdentify("food-me/get/success"))
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
    @DisplayName("내가 작성한 food 조회 최상위 category로 조회")
    void findMineFoodControllerRootCategory_Success() throws Exception {
        // given
        List<FoodPageDto> foodpageContent = List.of(
                FoodPageDto.builder().
                        id(1L)
                        .foodTitle("title_1")
                        .categoryName("샌드위치")
                        .price(1000)
                        .numberOfLikes(12)
                        .isMeBookmark(false)
                        .isMeLike(false)
                        .foodImages(List.of(new FoodImageDto(1L, "imageUrl1.jpg", "realImageName1.jpg")))
                        .build(),
                FoodPageDto.builder().
                        id(2L)
                        .foodTitle("title_2")
                        .categoryName("떡볶이")
                        .price(2000)
                        .numberOfLikes(121)
                        .isMeBookmark(false)
                        .isMeLike(false)
                        .foodImages(List.of(new FoodImageDto(2L, "imageUrl2.jpg", "realImageName2.jpg")))
                        .build(),
                FoodPageDto.builder().
                        id(3L)
                        .foodTitle("title_3")
                        .categoryName("샐러드")
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
                .param("flavors", "단맛", "짠맛")
                .param("sort", "like")
                .param("order", "asc")
                .param("categoryName", "음식")
                .param("firstSearchTime", "2021-12-25T12:12:12")
                .param("offset", "0")
                .param("mineFoodType", "MYFOOD")
                .param("pageSize", "3")
                .header(HttpHeaders.AUTHORIZATION, "token"));

        // then
        FoodPageResponse foodPageResponse = objectMapper
                .readValue(perform.andExpect(status().isOk())
                        .andDo(documentIdentify("food-me2/get/success"))
                        .andReturn()
                        .getResponse()
                        .getContentAsString(StandardCharsets.UTF_8), new TypeReference<>() {
                });

        assertThat(foodPageResponse.getFoods())
                .hasSize(3)
                .extracting("foodTitle", "categoryName")
                .containsExactlyInAnyOrderElementsOf(
                        List.of(tuple("title_1", "샌드위치"),
                                tuple("title_2", "떡볶이"),
                                tuple("title_3", "샐러드")));

        assertEquals(foodPageResponse.getPageSize(), 3);
        assertEquals(foodPageResponse.getOffset(), 0);
    }

    @Test
    @DisplayName("Bookmark한 food 조회")
    void findBookmarkeFoodController_Success() throws Exception {
        // given
        List<FoodPageDto> foodpageContent = List.of(
                FoodPageDto.builder().
                        id(1L)
                        .foodTitle("title_1")
                        .categoryName("샌드위치")
                        .price(1000)
                        .numberOfLikes(12)
                        .isMeBookmark(true)
                        .isMeLike(false)
                        .foodImages(List.of(new FoodImageDto(1L, "imageUrl1.jpg", "realImageName1.jpg")))
                        .build(),
                FoodPageDto.builder().
                        id(2L)
                        .foodTitle("title_2")
                        .categoryName("샌드위치")
                        .price(2000)
                        .numberOfLikes(121)
                        .isMeBookmark(true)
                        .isMeLike(false)
                        .foodImages(List.of(new FoodImageDto(2L, "imageUrl2.jpg", "realImageName2.jpg")))
                        .build(),
                FoodPageDto.builder().
                        id(3L)
                        .foodTitle("title_3")
                        .categoryName("샌드위치")
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
                .param("flavors", "단맛", "짠맛")
                .param("sort", "like")
                .param("order", "asc")
                .param("categoryName", "샌드위치")
                .param("firstSearchTime", "2021-12-25T12:12:12")
                .param("offset", "0")
                .param("mineFoodType", "BOOKMARK")
                .param("pageSize", "3")
                .header(HttpHeaders.AUTHORIZATION, "token"));

        // then
        FoodPageResponse foodPageResponse = objectMapper
                .readValue(perform.andExpect(status().isOk())
                        .andDo(documentIdentify("food-me3/get/success"))
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
