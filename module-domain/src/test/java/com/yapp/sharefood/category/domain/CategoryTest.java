package com.yapp.sharefood.category.domain;

import com.yapp.sharefood.category.exception.CategoryNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("category 도메인 테스트")
class CategoryTest {

    @Test
    @DisplayName("부모 Category 설정 테스트")
    void assignParentTest() {
        // given
        Category now = Category.of("now");
        Category parent = Category.of("parent");

        // when
        now.assignParent(parent);

        // then
        assertEquals("parent", now.getParent().getName());
        assertEquals("now", now.getChildCategories().getChildCategories().get(0).getName());
    }

    @Test
    @DisplayName("부모 category가 null일 경우")
    void assignParentErrorIfParentIsNull() throws Exception {
        // given
        Category now = Category.of("now");
        Category parent = null;

        // when

        // then
        assertThrows(CategoryNotFoundException.class, () -> now.assignParent(parent));
    }

    @Test
    @DisplayName("자식 category추가")
    void addChildCategoryTest() {
        // given
        Category now = Category.of("now");
        Category child = Category.of("child");

        // when
        now.addChildCategory(child);

        // then
        assertEquals("child", now.getChildCategories().getChildCategories().get(0).getName());
        assertEquals("now", child.getChildCategories().getChildCategories().get(0).getParent().getName());
    }

    @Test
    @DisplayName("자식 category가 null일 경우")
    void addChildErrorIfParentIsNull() throws Exception {
        // given
        Category now = Category.of("now");
        Category child = null;

        // when

        // then
        assertThrows(CategoryNotFoundException.class, () -> now.addChildCategory(child));
    }
}