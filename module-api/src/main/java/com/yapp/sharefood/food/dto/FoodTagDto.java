package com.yapp.sharefood.food.dto;

import com.yapp.sharefood.food.domain.FoodIngredientType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class FoodTagDto {
    private Long id;
    @NotNull
    @NotBlank
    private String name;
    @NotNull
    @NotBlank
    private FoodIngredientType tagUseType;

    public static FoodTagDto of(Long id, String name, FoodIngredientType tagUseType) {
        return new FoodTagDto(id, name, tagUseType);
    }
}
