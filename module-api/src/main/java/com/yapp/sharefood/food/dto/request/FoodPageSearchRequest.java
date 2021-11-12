package com.yapp.sharefood.food.dto.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class FoodPageSearchRequest {
    @NotNull
    @NotBlank
    private String categoryName;
}
