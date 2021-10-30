package com.yapp.sharefood.food.dto.request;

import com.yapp.sharefood.food.domain.FoodStatus;
import com.yapp.sharefood.food.dto.FoodCategoryDto;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
public class FoodCreationRequest {
    @NotNull
    @NotBlank
    private String title;

    @NotNull
    private Integer price;

    @NotNull
    @NotBlank
    private String reviewMsg;

    @NotNull
    private FoodStatus foodStatus;

    @NotNull
    private FoodCategoryDto foodCategory;
}
