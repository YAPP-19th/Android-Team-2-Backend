package com.yapp.sharefood.food.controller;

import com.yapp.sharefood.auth.resolver.AuthUser;
import com.yapp.sharefood.food.dto.request.FoodTopRankRequest;
import com.yapp.sharefood.food.dto.request.RecommendationFoodRequest;
import com.yapp.sharefood.food.dto.response.FoodDetailResponse;
import com.yapp.sharefood.food.dto.response.RecommendationFoodResponse;
import com.yapp.sharefood.food.dto.response.TopRankFoodResponse;
import com.yapp.sharefood.food.service.FoodService;
import com.yapp.sharefood.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
public class FoodCategoryController {

    private final FoodService foodService;

    @GetMapping("/api/v1/foods/rank")
    public ResponseEntity<TopRankFoodResponse> findFavoriteFoods(@Valid FoodTopRankRequest foodTopRankRequest) {
        return ResponseEntity.ok(foodService.findTopRankFoods(foodTopRankRequest));
    }

    @GetMapping("/api/v1/foods/recommendation")
    public ResponseEntity<RecommendationFoodResponse> findUserRecommendation(@AuthUser User user,
                                                                             @Valid RecommendationFoodRequest recommendationFoodRequest) {
        return ResponseEntity.ok(foodService.findFoodRecommendation(recommendationFoodRequest, user));
    }

    @GetMapping("/api/v1/foods/{foodId}")
    public ResponseEntity<FoodDetailResponse> findFood(@PathVariable("foodId") Long foodId) {
        return ResponseEntity.ok(foodService.findFoodDetailById(foodId));
    }

    @DeleteMapping("/api/v1/foods/{foodId}")
    public ResponseEntity<Void> deleteFood(@AuthUser User user, @PathVariable("foodId") Long foodId) {
        foodService.deleteFood(foodId, user);
        return ResponseEntity.ok().build();
    }
}
