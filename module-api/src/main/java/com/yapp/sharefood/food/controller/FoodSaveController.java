package com.yapp.sharefood.food.controller;

import com.yapp.sharefood.auth.resolver.AuthUser;
import com.yapp.sharefood.food.dto.request.FoodCreationRequest;
import com.yapp.sharefood.food.dto.request.FoodImageCreateRequest;
import com.yapp.sharefood.food.dto.request.FoodUpdateRequest;
import com.yapp.sharefood.food.dto.response.FoodDetailResponse;
import com.yapp.sharefood.food.dto.response.FoodImageCreateResponse;
import com.yapp.sharefood.food.facade.FoodSaveFacade;
import com.yapp.sharefood.food.service.FoodImageService;
import com.yapp.sharefood.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;

@RestController
@RequiredArgsConstructor
public class FoodSaveController {

    private final FoodSaveFacade foodSaveFacade;
    private final FoodImageService foodImageService;

    @PostMapping("/api/v1/foods")
    public ResponseEntity<URI> saveFood(@AuthUser User user,
                                        @Valid @RequestBody FoodCreationRequest foodCreationRequest) {
        URI userCreateUri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(foodSaveFacade.saveFoodFacade(user, foodCreationRequest))
                .toUri();

        return ResponseEntity.created(userCreateUri).build();
    }

    @PutMapping("/api/v1/foods/{foodId}")
    public ResponseEntity<FoodDetailResponse> updateFood(@AuthUser User user,
                                                         @PathVariable("foodId") Long foodId,
                                                         @Valid @RequestBody FoodUpdateRequest foodUpdateRequest) {
        return ResponseEntity.ok(foodSaveFacade.updateFoodFacade(user, foodId, foodUpdateRequest));
    }

    @PostMapping("/api/v1/foods/{foodId}/images")
    public ResponseEntity<FoodImageCreateResponse> saveImages(@PathVariable("foodId") Long foodId,
                                                              @Valid FoodImageCreateRequest images) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(foodImageService.saveImages(foodId, images.getImages()));
    }
}
