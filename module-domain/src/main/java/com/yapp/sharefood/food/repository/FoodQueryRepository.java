package com.yapp.sharefood.food.repository;

import com.yapp.sharefood.food.domain.Food;
import com.yapp.sharefood.food.dto.FoodPageSearch;
import com.yapp.sharefood.food.dto.FoodRecommendSearch;

import java.util.List;

public interface FoodQueryRepository {
    List<Food> findFoodWithCategoryByIds(List<Long> ids);

    List<Food> findRecommendFoods(FoodRecommendSearch foodRecommendSearch);

    List<Food> findPageFoods(FoodPageSearch foodPageSearch);
}
