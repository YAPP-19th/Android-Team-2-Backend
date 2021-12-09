package com.yapp.sharefood.food.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yapp.sharefood.common.PreprocessController;
import com.yapp.sharefood.food.dto.FoodImageDto;
import com.yapp.sharefood.food.dto.FoodPageDto;
import com.yapp.sharefood.food.dto.request.FoodTopRankRequest;
import com.yapp.sharefood.food.dto.response.TopRankFoodResponse;
import com.yapp.sharefood.food.service.FoodImageService;
import com.yapp.sharefood.food.service.FoodService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static com.yapp.sharefood.common.documentation.DocumentationUtils.documentIdentify;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.willReturn;
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
                    .isBookmark(false)
                    .foodImages(List.of(new FoodImageDto(1L, "s3RealImageUrl.jpg", "음식사진" + i + ".jpg")))
                    .build());
        }

        willReturn(TopRankFoodResponse.of(mockFoodPageDtos))
                .given(foodService).findTopRankFoods(any(FoodTopRankRequest.class));

        // when
        ResultActions perform = mockMvc.perform(get("/api/v1/foods/rank")
                .param("top", "5")
                .param("rankDatePeriod", "7")
                .param("categoryName", "음식")
                .header(HttpHeaders.AUTHORIZATION, "token"));

        // then
        TopRankFoodResponse topRankFoodResponse = objectMapper
                .readValue(perform.andExpect(status().isOk())
                        .andDo(documentIdentify("food/get/success/rank"))
                        .andReturn()
                        .getResponse()
                        .getContentAsString(StandardCharsets.UTF_8), new TypeReference<>() {
                });

        assertThat(topRankFoodResponse.getTopRankingFoods())
                .hasSize(5)
                .extracting("foodTitle")
                .containsExactlyInAnyOrderElementsOf(List.of("title_0", "title_1", "title_2", "title_3", "title_4"));
    }

    @Test
    @DisplayName("rank를 최대 개수를 5개 미만인 경우 - 에러 발생")
    void findTopRankFoodFewFoodDataTest_Fail_BadRequest() throws Exception {
        // given

        // when
        ResultActions perform = mockMvc.perform(get("/api/v1/foods/rank")
                .header(HttpHeaders.AUTHORIZATION, "token")
                .param("top", "4")
                .param("rankDatePeriod", "7")
                .param("categoryName", "음식"));

        // then
        String errorMsg = perform.andExpect(status().isBadRequest())
                .andDo(documentIdentify("food/get/fail/badRequest"))
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        assertThat(errorMsg)
                .isNotNull();
    }

    @Test
    @DisplayName("rank를 최대 food 개수를 10개를 초과한 경우 - 에러 발생")
    void findTopRankTooMuchFood_Fail_BadRequest() throws Exception {
        // given

        // when
        ResultActions perform = mockMvc.perform(get("/api/v1/foods/rank")
                .header(HttpHeaders.AUTHORIZATION, "token")
                .param("top", "5")
                .param("rankDatePeriod", "11")
                .param("categoryName", "음식"));

        // then
        String errorMsg = perform.andExpect(status().isBadRequest())
                .andDo(documentIdentify("food/get/fail/badRequest"))
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        assertThat(errorMsg)
                .isNotNull();
    }
}
