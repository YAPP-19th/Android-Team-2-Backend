package com.yapp.sharefood.food.service;

import com.yapp.sharefood.food.domain.Food;
import com.yapp.sharefood.food.dto.FoodPageReadType;
import com.yapp.sharefood.food.dto.FoodPageSearch;
import com.yapp.sharefood.food.repository.FoodRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class FoodPagePureReadStrategy implements FoodPageReadStrategy {
    private final FoodRepository foodRepository;

    @Override
    public void setBeanName(String name) {
        if (!name.equals(FoodPageReadType.PURE_READ.getKey())) {
            log.info("expected bean name={}, real bean name={}", FoodPageReadType.PURE_READ.getKey(), name);
            throw new IllegalStateException("bean name not match");
        }
    }

    @Override
    public List<Food> findFoodPageBySearch(FoodPageSearch foodPageSearch) {
        return foodRepository.findFoodNormalSearch(foodPageSearch);
    }
}
