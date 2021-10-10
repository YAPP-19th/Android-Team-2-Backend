package com.yapp.sharefood.food.dto;

import lombok.Data;

@Data
public class FoodCategoryDto {
    private Long id;
    private String categoryName;
    private FoodCategoryDto childFoodCategory;
}
