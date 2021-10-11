package com.yapp.sharefood.food.dto.response;

import com.yapp.sharefood.food.dto.FoodPageDto;
import lombok.Data;

import java.util.List;

@Data
public class TopRankFoodResponse {
    private List<FoodPageDto> topRankingFoods;
}
