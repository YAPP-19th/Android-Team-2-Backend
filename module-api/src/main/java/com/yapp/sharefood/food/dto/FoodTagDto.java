package com.yapp.sharefood.food.dto;

import com.yapp.sharefood.food.domain.FoodIngredientType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class FoodTagDto {
    private Long id;
    private String name;
    private FoodIngredientType tagUseType;

    public static FoodTagDto of(Long id, String name, FoodIngredientType tagUseType) {
        return new FoodTagDto(id, name, tagUseType);
    }
}
