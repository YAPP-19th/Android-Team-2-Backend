package com.yapp.sharefood.food.repository.query;

import com.yapp.sharefood.category.domain.Category;
import com.yapp.sharefood.common.order.SortType;
import com.yapp.sharefood.food.domain.Food;
import com.yapp.sharefood.food.dto.FoodMinePageSearch;
import com.yapp.sharefood.food.dto.FoodPageSearch;
import com.yapp.sharefood.food.dto.FoodRecommendSearch;
import com.yapp.sharefood.food.dto.OrderType;
import com.yapp.sharefood.user.domain.User;

import java.util.List;

public interface FoodQueryRepository {
    List<Food> findFoodWithCategoryByIds(List<Long> ids);

    List<Food> findFoodWithCategoryByIds(List<Long> ids, SortType sortType, OrderType orderType);

    List<Food> findFavoriteFoods(User findUser, List<Category> categories);

    List<Food> findRecommendFoods(FoodRecommendSearch foodRecommendSearch);

    List<Food> findFoodNormalSearch(FoodPageSearch foodPageSearch);

    List<Food> findFoodFilterWithTag(FoodPageSearch foodPageSearch);

    List<Food> findFoodFilterWithFlavor(FoodPageSearch foodPageSearch);

    List<Food> findMineFoodSearch(User ownerUser, FoodMinePageSearch foodMinePageSearch);

    List<Food> findMineBookMarkFoodSearch(User ownerUser, FoodMinePageSearch foodMinePageSearch);

    void updateFoodNumberOfLikesForAtomic(Food updateFood, int numberOfChange);
}
