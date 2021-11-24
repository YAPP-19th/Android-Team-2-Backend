package com.yapp.sharefood.food.dto.response;

import com.yapp.sharefood.food.dto.FoodPageDto;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class RecommendationFoodResponse {
    private List<FoodPageDto> recommendationFoods;
}
