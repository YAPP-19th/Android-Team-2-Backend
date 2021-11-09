package com.yapp.sharefood.food.controller;

import com.yapp.sharefood.auth.resolver.AuthUser;
import com.yapp.sharefood.common.utils.LocalDateTimePeriodUtils;
import com.yapp.sharefood.food.dto.request.FoodCreationRequest;
import com.yapp.sharefood.food.dto.request.FoodTopRankRequest;
import com.yapp.sharefood.food.dto.response.FoodDetailResponse;
import com.yapp.sharefood.food.dto.response.TopRankFoodResponse;
import com.yapp.sharefood.food.service.FoodService;
import com.yapp.sharefood.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
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
