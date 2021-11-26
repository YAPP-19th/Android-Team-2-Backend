package com.yapp.sharefood.food.repository.query;

import com.yapp.sharefood.food.domain.FoodTag;

import java.util.List;

public interface FoodTagQueryRepository {
    List<FoodTag> findFoodtagsWithTag(List<Long> foodTagIds);
}
