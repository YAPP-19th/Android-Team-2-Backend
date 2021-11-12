package com.yapp.sharefood.food.repository;

import com.yapp.sharefood.food.domain.Food;

import java.util.List;

public interface FoodQueryRepository {
    List<Food> findFoodWithCategoryByIds(List<Long> ids);
}
