package com.yapp.sharefood.food.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.yapp.sharefood.category.exception.CategoryNotFoundException;
import com.yapp.sharefood.common.controller.PreprocessController;
import com.yapp.sharefood.common.exception.BadRequestException;
import com.yapp.sharefood.common.exception.InvalidOperationException;
import com.yapp.sharefood.flavor.domain.FlavorType;
import com.yapp.sharefood.flavor.dto.FlavorDto;
import com.yapp.sharefood.food.domain.FoodIngredientType;
import com.yapp.sharefood.food.domain.FoodStatus;
import com.yapp.sharefood.food.dto.FoodImageDto;
import com.yapp.sharefood.food.dto.FoodTagDto;
import com.yapp.sharefood.food.dto.request.FoodCreationRequest;
import com.yapp.sharefood.food.dto.request.FoodUpdateRequest;
import com.yapp.sharefood.food.dto.response.FoodDetailResponse;
import com.yapp.sharefood.food.dto.response.FoodImageCreateResponse;
import com.yapp.sharefood.user.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static com.yapp.sharefood.common.controller.documentation.DocumentationUtils.documentIdentify;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.willReturn;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class FoodSaveControllerTest extends PreprocessController {

    @BeforeEach
    void setUp() {
        loginMockSetup();
    }

    @Test
    @DisplayName("food ?????? ??????-??????")
    void saveFood_Success() throws Exception {
        // given
        willReturn(1L)
                .given(foodSaveFacade).saveFoodFacade(any(User.class), any(FoodCreationRequest.class));

        FoodCreationRequest foodCreationRequest = FoodCreationRequest.builder()
                .categoryName("????????????")
                .title("title")
                .price(10000)
                .flavors(List.of(FlavorType.SWEET.getFlavorName(), FlavorType.REFRESH_DETAIL.getFlavorName()))
                .tags(List.of(FoodTagDto.of(1L, "?????????", FoodIngredientType.MAIN), FoodTagDto.of(2L, "??????", FoodIngredientType.ADD)))
                .reviewMsg("review msg")
                .foodStatus(FoodStatus.SHARED)
                .build();

        // when
        String requestBodyStr = objectMapper.writeValueAsString(foodCreationRequest);
        ResultActions perform = mockMvc.perform(post("/api/v1/foods")
                .header(HttpHeaders.AUTHORIZATION, "token")
                .content(requestBodyStr)
                .contentType(MediaType.APPLICATION_JSON_VALUE));

        // then
        perform.andExpect(status().isCreated())
                .andDo(documentIdentify("food/post/success"))
                .andExpect(header().exists("Location"))
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);
    }

    @Test
    @DisplayName("food ?????? ?????? mine?????? ??????-??????")
    void saveMineFood_Success() throws Exception {
        // given
        willReturn(1L)
                .given(foodSaveFacade).saveFoodFacade(any(User.class), any(FoodCreationRequest.class));

        FoodCreationRequest foodCreationRequest = FoodCreationRequest.builder()
                .categoryName("????????????")
                .title("title")
                .price(10000)
                .flavors(List.of(FlavorType.SWEET.getFlavorName(), FlavorType.REFRESH_DETAIL.getFlavorName()))
                .tags(List.of(FoodTagDto.of(1L, "?????????", FoodIngredientType.MAIN), FoodTagDto.of(2L, "??????", FoodIngredientType.ADD)))
                .reviewMsg("review msg")
                .foodStatus(FoodStatus.MINE)
                .build();

        // when
        String requestBodyStr = objectMapper.writeValueAsString(foodCreationRequest);
        ResultActions perform = mockMvc.perform(post("/api/v1/foods")
                .header(HttpHeaders.AUTHORIZATION, "token")
                .content(requestBodyStr)
                .contentType(MediaType.APPLICATION_JSON_VALUE));

        // then
        perform.andExpect(status().isCreated())
                .andDo(documentIdentify("food-mine/post/success"))
                .andExpect(header().exists("Location"))
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);
    }

    @Test
    @DisplayName("category??? ???????????? ?????? ??????")
    void saveFood_CategoryNotFound_Exception() throws Exception {
        // given
        willThrow(new CategoryNotFoundException())
                .given(foodSaveFacade).saveFoodFacade(any(User.class), any(FoodCreationRequest.class));

        FoodCreationRequest foodCreationRequest = FoodCreationRequest.builder()
                .categoryName("notExistCategory")
                .title("title")
                .price(10000)
                .flavors(List.of(FlavorType.SWEET.getFlavorName(), FlavorType.REFRESH_DETAIL.getFlavorName()))
                .tags(List.of(FoodTagDto.of(1L, "?????????", FoodIngredientType.MAIN), FoodTagDto.of(2L, "??????", FoodIngredientType.ADD)))
                .reviewMsg("review msg")
                .foodStatus(FoodStatus.SHARED)
                .build();

        // when
        String requestBodyStr = objectMapper.writeValueAsString(foodCreationRequest);
        ResultActions perform = mockMvc.perform(post("/api/v1/foods")
                .header(HttpHeaders.AUTHORIZATION, "token")
                .content(requestBodyStr)
                .contentType(MediaType.APPLICATION_JSON_VALUE));

        // then
        perform.andExpect(status().isNotFound())
                .andDo(documentIdentify("food/post/fail/categoryNotFound"))
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);
    }

    @Test
    @DisplayName("tag ?????? ???????????? ????????? ??????")
    void saveFood_DuplicatedTags_Exception() throws Exception {
        willThrow(new InvalidOperationException("?????? ????????? tag?????????."))
                .given(foodSaveFacade).saveFoodFacade(any(User.class), any(FoodCreationRequest.class));

        FoodCreationRequest foodCreationRequest = FoodCreationRequest.builder()
                .categoryName("????????????")
                .title("title")
                .price(10000)
                .flavors(List.of(FlavorType.SWEET.getFlavorName(), FlavorType.REFRESH_DETAIL.getFlavorName()))
                .tags(List.of(FoodTagDto.of(1L, "?????????", FoodIngredientType.MAIN), FoodTagDto.of(1L, "?????????", FoodIngredientType.ADD)))
                .reviewMsg("review msg")
                .foodStatus(FoodStatus.SHARED)
                .build();

        // when
        String requestBodyStr = objectMapper.writeValueAsString(foodCreationRequest);
        ResultActions perform = mockMvc.perform(post("/api/v1/foods")
                .header(HttpHeaders.AUTHORIZATION, "token")
                .content(requestBodyStr)
                .contentType(MediaType.APPLICATION_JSON_VALUE));

        // then
        perform.andExpect(status().isInternalServerError())
                .andDo(documentIdentify("food/post/fail/invalidOperation1"))
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);
    }

    @Test
    @DisplayName("?????? flavor ????????? ??????")
    void saveFood_DuplicatedFlavor_Exception() throws Exception {
        willThrow(new InvalidOperationException("?????? ????????? flavor?????????."))
                .given(foodSaveFacade).saveFoodFacade(any(User.class), any(FoodCreationRequest.class));

        FoodCreationRequest foodCreationRequest = FoodCreationRequest.builder()
                .categoryName("????????????")
                .title("title")
                .price(10000)
                .flavors(List.of(FlavorType.SWEET.getFlavorName(), FlavorType.SWEET.getFlavorName()))
                .tags(List.of(FoodTagDto.of(1L, "?????????", FoodIngredientType.MAIN), FoodTagDto.of(null, "??????", FoodIngredientType.ADD)))
                .reviewMsg("review msg")
                .foodStatus(FoodStatus.SHARED)
                .build();

        // when
        String requestBodyStr = objectMapper.writeValueAsString(foodCreationRequest);
        ResultActions perform = mockMvc.perform(post("/api/v1/foods")
                .header(HttpHeaders.AUTHORIZATION, "token")
                .content(requestBodyStr)
                .contentType(MediaType.APPLICATION_JSON_VALUE));

        // then
        perform.andExpect(status().isInternalServerError())
                .andDo(documentIdentify("food/post/fail/invalidOperation2"))
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);
    }

    @Test
    @DisplayName("Main??? ?????? ?????? ?????? ?????? - ??????")
    void foodSaveTest_MainNotExist_Exception() throws Exception {
        willThrow(new BadRequestException())
                .given(foodSaveFacade).saveFoodFacade(any(User.class), any(FoodCreationRequest.class));

        FoodCreationRequest foodCreationRequest = FoodCreationRequest.builder()
                .categoryName("????????????")
                .title("title")
                .price(10000)
                .flavors(List.of(FlavorType.SWEET.getFlavorName(), FlavorType.BITTER.getFlavorName()))
                .tags(List.of(FoodTagDto.of(1L, "?????????", FoodIngredientType.EXTRACT), FoodTagDto.of(2L, "??????", FoodIngredientType.ADD)))
                .reviewMsg("review msg")
                .foodStatus(FoodStatus.SHARED)
                .build();

        // when
        String requestBodyStr = objectMapper.writeValueAsString(foodCreationRequest);
        ResultActions perform = mockMvc.perform(post("/api/v1/foods")
                .header(HttpHeaders.AUTHORIZATION, "token")
                .content(requestBodyStr)
                .contentType(MediaType.APPLICATION_JSON_VALUE));

        // then
        perform.andExpect(status().isBadRequest())
                .andDo(documentIdentify("food/post/fail/badRequest"))
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);
    }

    private List<MockMultipartFile> getFiles(int index) {
        List<MockMultipartFile> files = new ArrayList<>();
        for (int i = 0; i < index; i++) {
            files.add(new MockMultipartFile("images", "originalFilename" + i, "text/plain", new byte[]{}));
        }

        return files;
    }

    private List<FoodImageDto> getMockFoodImageDto(List<MockMultipartFile> mockFiles) {
        List<FoodImageDto> foodImageDtos = new ArrayList<>();
        long index = 0L;
        for (MockMultipartFile file : mockFiles) {
            foodImageDtos.add(new FoodImageDto(index, file.getName(), file.getOriginalFilename()));
            index++;
        }

        return foodImageDtos;
    }

    @Test
    @DisplayName("????????? Food??? Image ???????????? ??????")
    void saveFoodImageTest_Success() throws Exception {
        // given
        List<MockMultipartFile> foodImages = getFiles(3);
        FoodImageCreateResponse foodImageCreateResponse = new FoodImageCreateResponse(getMockFoodImageDto(foodImages));
        willReturn(foodImageCreateResponse)
                .given(foodImageService).saveImages(anyLong(), anyList());

        // when
        MockMultipartHttpServletRequestBuilder multipartRequest = multipart(String.format("/api/v1/foods/%s/images", 1));
        for (MockMultipartFile file : foodImages) {
            multipartRequest.file(file);
        }

        ResultActions perform = mockMvc.perform(multipartRequest
                .header(HttpHeaders.AUTHORIZATION, "token"));

        // then
        FoodImageCreateResponse saveImageResponse = objectMapper.readValue(
                perform.andExpect(status().isCreated())
                        .andDo(documentIdentify("food-images/post/success"))
                        .andReturn()
                        .getResponse()
                        .getContentAsString(StandardCharsets.UTF_8), new TypeReference<>() {
                }
        );

        assertThat(saveImageResponse.getImages())
                .isNotNull()
                .hasSize(3)
                .extracting("realImageName")
                .containsExactlyInAnyOrderElementsOf(List.of("originalFilename0", "originalFilename1", "originalFilename2"));
    }

    @Test
    @DisplayName("????????? Food??? Image 0?????? ???????????? ????????? - ?????? ?????????")
    void saveFoodImageZeroTest_400_BadRequest() throws Exception {
        // given
        List<MockMultipartFile> foodImages = getFiles(0);
        FoodImageCreateResponse foodImageCreateResponse = new FoodImageCreateResponse(getMockFoodImageDto(foodImages));
        willReturn(foodImageCreateResponse)
                .given(foodImageService).saveImages(anyLong(), anyList());

        // when
        MockMultipartHttpServletRequestBuilder multipartRequest = multipart(String.format("/api/v1/foods/%s/images", 1));
        for (MockMultipartFile file : foodImages) {
            multipartRequest.file(file);
        }

        ResultActions perform = mockMvc.perform(multipartRequest
                .header(HttpHeaders.AUTHORIZATION, "token"));

        // then
        perform.andExpect(status().isBadRequest())
                .andDo(documentIdentify("food-images/post/badRequest"))
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);
    }

    @Test
    @DisplayName("?????? ?????? update ?????? ?????? - ??????")
    void updateFood_Success() throws Exception {
        // given
        FoodDetailResponse foodDetailResponse = FoodDetailResponse.builder()
                .id(1L)
                .foodTitle("title1")
                .reviewDetail("reviewDetail")
                .price(1000)
                .numberOfLike(10)
                .writerName("writer")
                .isMyFood(true)
                .isMeLike(false)
                .isMeBookmark(false)
                .categoryName("????????????")
                .foodTags(List.of(FoodTagDto.of(1L, "??????1", FoodIngredientType.MAIN),
                        FoodTagDto.of(2L, "??????2", FoodIngredientType.ADD),
                        FoodTagDto.of(3L, "??????3", FoodIngredientType.EXTRACT)))
                .foodFlavors(List.of(FlavorDto.of(1L, FlavorType.SWEET), FlavorDto.of(2L, FlavorType.SOUR), FlavorDto.of(3L, FlavorType.PLAIN_DETAIL)))
                .build();

        willReturn(foodDetailResponse)
                .given(foodSaveFacade).updateFoodFacade(any(User.class), anyLong(), any(FoodUpdateRequest.class));
        FoodUpdateRequest foodCreationRequest = FoodUpdateRequest.builder()
                .categoryName("??????")
                .title("title1")
                .reviewMsg("reviewDetail")
                .price(1000)
                .foodStatus(FoodStatus.SHARED)
                .tags(List.of(FoodTagDto.of(1L, "??????1", FoodIngredientType.MAIN),
                        FoodTagDto.of(2L, "??????2", FoodIngredientType.ADD),
                        FoodTagDto.of(3L, "??????3", FoodIngredientType.EXTRACT)))
                .flavors(List.of(FlavorDto.of(1L, FlavorType.SWEET), FlavorDto.of(2L, FlavorType.SOUR), FlavorDto.of(3L, FlavorType.PLAIN_DETAIL)))
                .build();

        // when
        String requestBodyStr = objectMapper.writeValueAsString(foodCreationRequest);
        ResultActions perform = mockMvc.perform(put("/api/v1/foods/1")
                .header(HttpHeaders.AUTHORIZATION, "token")
                .content(requestBodyStr)
                .contentType(MediaType.APPLICATION_JSON_VALUE));

        // then
        FoodDetailResponse detailData = objectMapper
                .readValue(perform.andExpect(status().isOk())
                        .andDo(documentIdentify("food/put/success"))
                        .andReturn()
                        .getResponse()
                        .getContentAsString(StandardCharsets.UTF_8), new TypeReference<>() {
                });

        assertEquals(detailData.getFoodTitle(), "title1");
        assertEquals(detailData.getReviewDetail(), "reviewDetail");
        assertEquals(detailData.getPrice(), 1000);
        assertThat(detailData.getFoodTags())
                .hasSize(3)
                .extracting("name")
                .containsExactlyInAnyOrderElementsOf(List.of("??????1", "??????2", "??????3"));

        assertThat(detailData.getFoodFlavors())
                .hasSize(3)
                .extracting("flavorName")
                .containsExactlyInAnyOrderElementsOf(List.of(FlavorType.SWEET.getFlavorName(),
                        FlavorType.SOUR.getFlavorName(), FlavorType.PLAIN_DETAIL.getFlavorName()));
    }
}