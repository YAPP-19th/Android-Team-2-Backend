package com.yapp.sharefood.food.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yapp.sharefood.category.exception.CategoryNotFoundException;
import com.yapp.sharefood.common.PreprocessController;
import com.yapp.sharefood.common.exception.BadRequestException;
import com.yapp.sharefood.common.exception.InvalidOperationException;
import com.yapp.sharefood.flavor.domain.FlavorType;
import com.yapp.sharefood.flavor.dto.FlavorDto;
import com.yapp.sharefood.food.domain.FoodIngredientType;
import com.yapp.sharefood.food.domain.FoodStatus;
import com.yapp.sharefood.food.dto.FoodImageDto;
import com.yapp.sharefood.food.dto.FoodTagDto;
import com.yapp.sharefood.food.dto.request.FoodCreationRequest;
import com.yapp.sharefood.food.dto.response.FoodImageCreateResponse;
import com.yapp.sharefood.food.service.FoodImageService;
import com.yapp.sharefood.food.service.FoodService;
import com.yapp.sharefood.tag.service.TagService;
import com.yapp.sharefood.user.domain.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static com.yapp.sharefood.common.documentation.DocumentationUtils.documentIdentify;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.willReturn;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = FoodSaveController.class)
class FoodSaveControllerTest extends PreprocessController {
    @Autowired
    MockMvc mockMvc;

    @MockBean
    FoodService foodService;
    @MockBean
    TagService tagService;
    @MockBean
    FoodImageService foodImageService;
    ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DisplayName("food 저장 기능-성공")
    void saveFood_Success() throws Exception {
        // given
        willReturn(1L)
                .given(foodService).saveFood(any(User.class), any(FoodCreationRequest.class), anyList());

        FoodCreationRequest foodCreationRequest = FoodCreationRequest.builder()
                .categoryName("샌드위치")
                .title("title")
                .price(10000)
                .flavors(List.of(FlavorDto.of(1L, FlavorType.SWEET), FlavorDto.of(2L, FlavorType.REFRESH_DETAIL)))
                .tags(List.of(FoodTagDto.of(1L, "샷추가", FoodIngredientType.MAIN), FoodTagDto.of(2L, "커피", FoodIngredientType.ADD)))
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
        String errorMsg = perform.andExpect(status().isCreated())
                .andDo(documentIdentify("food/post/success"))
                .andExpect(header().exists("Location"))
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        assertThat(errorMsg)
                .isNotNull();
    }

    @Test
    @DisplayName("category를 검색하지 못한 경우")
    void saveFood_CategoryNotFound_Exception() throws Exception {
        // given
        willThrow(new CategoryNotFoundException())
                .given(foodService).saveFood(any(User.class), any(FoodCreationRequest.class), anyList());

        FoodCreationRequest foodCreationRequest = FoodCreationRequest.builder()
                .categoryName("notExistCategory")
                .title("title")
                .price(10000)
                .flavors(List.of(FlavorDto.of(1L, FlavorType.SWEET), FlavorDto.of(2L, FlavorType.REFRESH_DETAIL)))
                .tags(List.of(FoodTagDto.of(1L, "샷추가", FoodIngredientType.MAIN), FoodTagDto.of(2L, "커피", FoodIngredientType.ADD)))
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
        String errorMsg = perform.andExpect(status().isNotFound())
                .andDo(documentIdentify("food/post/fail/categoryNotFound"))
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        assertThat(errorMsg)
                .isNotNull();
    }

    @Test
    @DisplayName("tag 정보 중복으로 추가할 경우")
    void saveFood_DuplicatedTags_Exception() throws Exception {
        willThrow(new InvalidOperationException("이미 등록된 tag입니다."))
                .given(foodService).saveFood(any(User.class), any(FoodCreationRequest.class), anyList());

        FoodCreationRequest foodCreationRequest = FoodCreationRequest.builder()
                .categoryName("샌드위치")
                .title("title")
                .price(10000)
                .flavors(List.of(FlavorDto.of(1L, FlavorType.SWEET), FlavorDto.of(2L, FlavorType.REFRESH_DETAIL)))
                .tags(List.of(FoodTagDto.of(1L, "샷추가", FoodIngredientType.MAIN), FoodTagDto.of(1L, "샷추가", FoodIngredientType.ADD)))
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
        String errorMsg = perform.andExpect(status().isInternalServerError())
                .andDo(documentIdentify("food/post/fail/invalidOperationException"))
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        assertThat(errorMsg)
                .isNotNull();
    }

    @Test
    @DisplayName("중복 flavor 입력시 에러")
    void saveFood_DuplicatedFlavor_Exception() throws Exception {
        willThrow(new InvalidOperationException("이미 등록된 flavor입니다."))
                .given(foodService).saveFood(any(User.class), any(FoodCreationRequest.class), anyList());

        FoodCreationRequest foodCreationRequest = FoodCreationRequest.builder()
                .categoryName("샌드위치")
                .title("title")
                .price(10000)
                .flavors(List.of(FlavorDto.of(1L, FlavorType.SWEET), FlavorDto.of(1L, FlavorType.SWEET)))
                .tags(List.of(FoodTagDto.of(1L, "샷추가", FoodIngredientType.MAIN), FoodTagDto.of(null, "커피", FoodIngredientType.ADD)))
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
        String errorMsg = perform.andExpect(status().isInternalServerError())
                .andDo(documentIdentify("food/post/fail/invalidOperationException"))
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        assertThat(errorMsg)
                .isNotNull();
    }

    @Test
    @DisplayName("Main을 추가 할지 않는 경우 - 실패")
    void foodSaveTest_MainNotExist_Exception() throws Exception {
        willThrow(new BadRequestException())
                .given(foodService).saveFood(any(User.class), any(FoodCreationRequest.class), anyList());

        FoodCreationRequest foodCreationRequest = FoodCreationRequest.builder()
                .categoryName("샌드위치")
                .title("title")
                .price(10000)
                .flavors(List.of(FlavorDto.of(1L, FlavorType.SWEET), FlavorDto.of(2L, FlavorType.BITTER)))
                .tags(List.of(FoodTagDto.of(1L, "샷추가", FoodIngredientType.EXTRACT), FoodTagDto.of(2L, "커피", FoodIngredientType.ADD)))
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
        String errorMsg = perform.andExpect(status().isBadRequest())
                .andDo(documentIdentify("food/post/fail/badRequest"))
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        assertThat(errorMsg)
                .isNotNull();
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
    @DisplayName("저장된 Food에 Image 저장하는 기능")
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
                        .andDo(documentIdentify("food/images/post/success"))
                        .andReturn()
                        .getResponse()
                        .getContentAsString(StandardCharsets.UTF_8), new TypeReference<FoodImageCreateResponse>() {
                }
        );

        assertThat(saveImageResponse.getImages())
                .isNotNull()
                .hasSize(3)
                .extracting("realImageName")
                .containsExactlyInAnyOrderElementsOf(List.of("originalFilename0", "originalFilename1", "originalFilename2"));
    }

    @Test
    @DisplayName("저장된 Food에 Image 0개를 추가하는 케이스 - 예외 케이스")
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
        String result = perform.andExpect(status().isBadRequest())
                .andDo(documentIdentify("food/images/post/badRequest"))
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        assertThat(result)
                .isNotNull();
    }
}