package com.yapp.sharefood.category.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yapp.sharefood.category.dto.CategoryDto;
import com.yapp.sharefood.category.dto.response.CategoriesTreeResponse;
import com.yapp.sharefood.category.service.CategoryService;
import com.yapp.sharefood.common.PreprocessController;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.willReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = CategoryController.class)
class CategoryControllerTest extends PreprocessController {
    @Autowired
    MockMvc mockMvc;
    @MockBean
    CategoryService categoryService;

    ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DisplayName("category 가져오기")
    void findAllTreeCategoriesTest() throws Exception {
        // given
        willReturn(new CategoriesTreeResponse(List.of(CategoryDto.of(1L, "category1", new ArrayList<>()), CategoryDto.of(2L, "category2", new ArrayList<>()))))
                .given(categoryService).findAllTreeCategories();
        // when
        ResultActions perform = mockMvc.perform(get("/api/v1/categories"));

        // then
        CategoriesTreeResponse categoriesTreeResponse = objectMapper
                .readValue(perform
                        .andExpect(status().isOk())
                        .andDo(print())
                        .andReturn()
                        .getResponse()
                        .getContentAsString(StandardCharsets.UTF_8), new TypeReference<>() {
                });

        assertEquals(2, categoriesTreeResponse.getCategories().size());
        assertThat(categoriesTreeResponse.getCategories())
                .extracting("categoryName")
                .contains("category1", "category2");
    }
}