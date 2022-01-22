package com.yapp.sharefood.category.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.yapp.sharefood.category.dto.CategoryDto;
import com.yapp.sharefood.category.dto.response.CategoriesTreeResponse;
import com.yapp.sharefood.common.controller.PreprocessController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.ResultActions;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static com.yapp.sharefood.common.controller.documentation.DocumentationUtils.documentIdentify;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.willReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CategoryControllerProcess extends PreprocessController {

    @BeforeEach
    void setUp() {
        loginMockSetup();
    }

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
                        .andDo(documentIdentify("category/get/success"))
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