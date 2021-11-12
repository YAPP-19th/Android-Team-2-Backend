package com.yapp.sharefood.food.dto.response;

import com.yapp.sharefood.food.dto.FoodPageDto;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TopRankFoodResponse {
    private List<FoodPageDto> topRankingFoods;

    private TopRankFoodResponse(List<FoodPageDto> topRankingFoods) {
        this.topRankingFoods = topRankingFoods;
    }

    public static TopRankFoodResponse of(List<FoodPageDto> foodPageDtos) {
        return new TopRankFoodResponse(foodPageDtos);
    }
}
