package com.yapp.sharefood.category.service;

import com.yapp.sharefood.category.dto.response.CategoriesTreeResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class CategoryServiceTest {
    @Autowired
    CategoryService categoryService;

    @Test
    @DisplayName("0개의 category")
    void findAllTreeCategoriesZeroDataTest() {
        // given

        // when
        CategoriesTreeResponse allTreeCategories = categoryService.findAllTreeCategories();

        // then
        assertEquals(0, allTreeCategories.getCategories().size());
    }

    @Test
    @DisplayName("dept 1, category 1개 인경우")
    void findAllTreeCategoryOneDataTest() throws Exception {
        // given

        // when

        // then
    }

    @Test
    @DisplayName("dept가 1인 category 여러개")
    void findAllCategoryOneDept() throws Exception {
        // given

        // when

        // then
    }

    @Test
    @DisplayName("dept 2인 경우")
    void findAllCategoryTowDept() throws Exception {
        // given

        // when

        // then
    }

    @Test
    @DisplayName("dept가 3인 경우")
    void findAllCategoryThreeDept() throws Exception {
        // given

        // when

        // then
    }
}