package com.yapp.sharefood.food.service;

import com.yapp.sharefood.food.domain.Food;
import com.yapp.sharefood.food.dto.FoodPageSearch;
import org.springframework.beans.factory.BeanNameAware;

import java.util.List;

public interface FoodPageReadStrategy extends BeanNameAware {
    List<Food> findFoodPageBySearch(FoodPageSearch foodPageSearch);
}
