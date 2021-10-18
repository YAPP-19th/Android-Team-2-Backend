package com.yapp.sharefood.food.dto;

import com.yapp.sharefood.food.domain.IngredientUseType;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FoodTagDto {
    private Long id;
    private String name;
    private IngredientUseType tagUseType;
}
