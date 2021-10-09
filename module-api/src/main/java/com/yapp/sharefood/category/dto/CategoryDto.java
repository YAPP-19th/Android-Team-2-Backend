package com.yapp.sharefood.category.dto;

import lombok.Data;

import java.util.List;

@Data
public class CategoryDto {
    private Long id;
    private String name;
    private List<CategoryDto> childCategories;
}
