package com.yapp.sharefood.category.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class CategoryDto {
    private Long id;
    private String name;
    private List<CategoryDto> childCategories;
}
