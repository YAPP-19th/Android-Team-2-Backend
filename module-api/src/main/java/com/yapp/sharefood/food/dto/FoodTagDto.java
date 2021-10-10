package com.yapp.sharefood.food.dto;

import com.yapp.sharefood.food.domain.IngredientUseType;
import lombok.Data;

@Data
public class FoodTagDto {
    private Long id;
    private String name;
    private IngredientUseType tagUseType;
}
