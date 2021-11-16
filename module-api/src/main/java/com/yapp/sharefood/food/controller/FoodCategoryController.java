package com.yapp.sharefood.food.controller;

import com.yapp.sharefood.common.utils.LocalDateTimePeriodUtils;
import com.yapp.sharefood.food.dto.request.FoodTopRankRequest;
import com.yapp.sharefood.food.dto.response.FoodDetailResponse;
import com.yapp.sharefood.food.dto.response.TopRankFoodResponse;
import com.yapp.sharefood.food.service.FoodService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
public class FoodCategoryController {

    private final FoodService foodService;

    @GetMapping("/api/v1/categories/{categoryName}/foods/rank")
    public ResponseEntity<TopRankFoodResponse> findFavoriteFoods(@PathVariable("categoryName") String categoryName,
                                                                 @Valid FoodTopRankRequest foodTopRankRequest) {
        LocalDateTime beforePeriod = LocalDateTimePeriodUtils.getBeforePeriod(foodTopRankRequest.getRankDatePeriod());
        LocalDateTime now = LocalDateTimePeriodUtils.now();

        return ResponseEntity.ok(foodService.findTopRankFoods(foodTopRankRequest, categoryName, beforePeriod, now));
    }


    @GetMapping("/api/v1/categories/{categoryName}/foods/{foodId}")
    public ResponseEntity<FoodDetailResponse> findFood(@PathVariable("foodId") Long foodId) {
        return ResponseEntity.ok(foodService.findFoodById(foodId));
    }
}
