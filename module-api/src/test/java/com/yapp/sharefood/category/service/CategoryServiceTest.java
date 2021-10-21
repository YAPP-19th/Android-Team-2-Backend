package com.yapp.sharefood.category.service;

import com.yapp.sharefood.category.domain.Category;
import com.yapp.sharefood.category.dto.response.CategoriesTreeResponse;
import com.yapp.sharefood.category.repository.CategoryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Comparator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest
class CategoryServiceTest {
    @Autowired
    CategoryService categoryService;
    @Autowired
    CategoryRepository categoryRepository;

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
        Category category = Category.of("category1");
        categoryRepository.save(category);

        // when
        CategoriesTreeResponse allTreeCategories = categoryService.findAllTreeCategories();

        // then
        assertEquals(1, allTreeCategories.getCategories().size());
        assertNull(allTreeCategories.getCategories().get(0).getChildCategories());
        assertEquals("category1", allTreeCategories.getCategories().get(0).getCategoryName());
    }

    @Test
    @DisplayName("dept가 1인 category 여러개")
    void findAllCategoryOneDept() throws Exception {
        // given
        Category category1 = Category.of("category1");
        Category category2 = Category.of("category2");
        Category category3 = Category.of("category3");
        categoryRepository.save(category1);
        categoryRepository.save(category2);
        categoryRepository.save(category3);

        // when
        CategoriesTreeResponse allTreeCategories = categoryService.findAllTreeCategories();

        // then
        assertEquals(3, allTreeCategories.getCategories().size());
        assertNull(allTreeCategories.getCategories().get(0).getChildCategories());
        assertThat(allTreeCategories.getCategories())
                .extracting("categoryName")
                .contains("category1", "category2", "category3");
    }

    @Test
    @DisplayName("dept 2인 경우")
    void findAllCategoryTowDept() throws Exception {
        // given
        Category category1 = Category.of("category1"); // group1
        Category category3 = Category.of("category3");
        category1.addChildCategories(category3);
        categoryRepository.save(category1);
        categoryRepository.save(category3);

        Category category2 = Category.of("category2"); // group 2
        Category category4 = Category.of("category4");
        Category category5 = Category.of("category5");
        category2.addChildCategories(category4, category5);
        categoryRepository.save(category2);
        categoryRepository.save(category4);
        categoryRepository.save(category5);

        // when
        CategoriesTreeResponse allTreeCategories = categoryService.findAllTreeCategories();
        allTreeCategories.getCategories().sort(Comparator.comparingInt(category -> category.getChildCategories().size()));

        // then
        assertEquals(2, allTreeCategories.getCategories().size());
        assertEquals(1, allTreeCategories.getCategories().get(0).getChildCategories().size());
        assertEquals(2, allTreeCategories.getCategories().get(1).getChildCategories().size());
    }

    @Test
    @DisplayName("dept가 3인 경우")
    void findAllCategoryThreeDept() throws Exception {
        Category category1 = Category.of("category1"); // group1
        categoryRepository.save(category1);
        Category category3 = Category.of("category3");
        category1.addChildCategories(category3);
        categoryRepository.save(category3);
        Category category6 = Category.of("category6");
        Category category7 = Category.of("category7");
        category3.addChildCategories(category6, category7);
        categoryRepository.save(category6);
        categoryRepository.save(category7);

        Category category2 = Category.of("category2"); // group 2
        categoryRepository.save(category2);
        Category category4 = Category.of("category4");
        Category category5 = Category.of("category5");
        category2.addChildCategories(category4, category5);
        categoryRepository.save(category4);
        categoryRepository.save(category5);

        // when
        CategoriesTreeResponse allTreeCategories = categoryService.findAllTreeCategories();
        allTreeCategories.getCategories().sort(Comparator.comparingInt(category -> category.getChildCategories().size()));

        // then
        assertEquals(2, allTreeCategories.getCategories().size());
        assertEquals(1, allTreeCategories.getCategories().get(0).getChildCategories().size());
        assertEquals(2, allTreeCategories.getCategories().get(0).getChildCategories().get(0).getChildCategories().size());
        assertEquals(2, allTreeCategories.getCategories().get(1).getChildCategories().size());
    }

    @Test
    @DisplayName("cycle 케이스 제외 test")
    void exeptCycleTest() throws Exception {
        // given
        Category category1 = Category.of("category1"); // group1
        Category category2 = Category.of("category2");
        category1.addChildCategories(category2);
        Category category3 = Category.of("category3");
        category2.addChildCategories(category3);
        category3.addChildCategories(category1);
        categoryRepository.save(category1);
        categoryRepository.save(category2);
        categoryRepository.save(category3);

        // when
        CategoriesTreeResponse allTreeCategories = categoryService.findAllTreeCategories();

        // then
        assertEquals(0, allTreeCategories.getCategories().size());
    }
}