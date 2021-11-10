package com.yapp.sharefood.food.controller;

import com.yapp.sharefood.auth.resolver.AuthUser;
import com.yapp.sharefood.food.dto.request.FoodCreationRequest;
import com.yapp.sharefood.food.dto.response.FoodDetailResponse;
import com.yapp.sharefood.food.service.FoodService;
import com.yapp.sharefood.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;

@RestController
@RequiredArgsConstructor
public class FoodCategoryController {
    
    private final FoodService foodService;

    @PostMapping("/api/v1/categories/{categoryName}/foods")
    public ResponseEntity<URI> saveFood(@AuthUser User user,
                                        @Valid @RequestBody FoodCreationRequest foodCreationRequest,
                                        @PathVariable("categoryName") String categoryName) {
        URI userCreateUri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(foodService.saveFood(user, foodCreationRequest, categoryName))
                .toUri();

        return ResponseEntity.created(userCreateUri).build();
    }

    @GetMapping("/api/v1/categories/{categoryName}/foods/{foodId}")
    public ResponseEntity<FoodDetailResponse> findFood(@PathVariable("foodId") Long foodId) {
        return ResponseEntity.ok(foodService.findFoodById(foodId));
    }
}