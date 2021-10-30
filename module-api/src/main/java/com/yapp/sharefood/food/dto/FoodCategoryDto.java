package com.yapp.sharefood.food.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class FoodCategoryDto {
    @NotNull
    private Long id;
    @NotNull
    @NotBlank
    private String categoryName;
}
