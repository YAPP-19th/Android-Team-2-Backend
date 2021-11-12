package com.yapp.sharefood.food.controller;

import com.yapp.sharefood.common.utils.LocalDateTimePeriodUtils;
import com.yapp.sharefood.food.dto.request.FoodTopRankRequest;
import com.yapp.sharefood.food.dto.response.TopRankFoodResponse;
import com.yapp.sharefood.food.service.FoodService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
public class FoodController {
    private final FoodService foodService;

    @GetMapping("/api/v1/foods/rank")
    public ResponseEntity<TopRankFoodResponse> findFavoriteFoods(@Valid FoodTopRankRequest foodTopRankRequest) {
        LocalDateTime beforePeriod = LocalDateTimePeriodUtils.getBeforePeriod(foodTopRankRequest.getRankDatePeriod());
        LocalDateTime now = LocalDateTimePeriodUtils.now();

        return ResponseEntity.ok(foodService.findTopRankFoods(foodTopRankRequest, beforePeriod, now));
    }
}
