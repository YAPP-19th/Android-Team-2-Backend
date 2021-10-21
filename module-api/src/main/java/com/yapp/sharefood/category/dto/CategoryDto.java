package com.yapp.sharefood.category.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CategoryDto {
    private Long id;
    private String categoryName;
    private List<CategoryDto> childCategories;

    private CategoryDto(Long id, String categoryName, List<CategoryDto> childCategories) {
        this.id = id;
        this.categoryName = categoryName;
        this.childCategories = childCategories;
    }

    public static CategoryDto of(Long id, String name, List<CategoryDto> childCategories) {
        return new CategoryDto(id, name, childCategories);
    }
}
