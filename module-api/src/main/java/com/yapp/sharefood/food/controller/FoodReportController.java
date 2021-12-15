package com.yapp.sharefood.food.controller;

import com.yapp.sharefood.auth.resolver.AuthUser;
import com.yapp.sharefood.food.dto.request.FoodReportRequest;
import com.yapp.sharefood.food.service.FoodReportService;
import com.yapp.sharefood.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class FoodReportController {
    private final FoodReportService foodReportService;

    @PostMapping("/api/v1/foods/{foodId}/report")
    public ResponseEntity<Void> createReport(@AuthUser User user, @PathVariable("foodId") Long foodId, @RequestBody FoodReportRequest request) {
        foodReportService.createReport(foodId, request);

        return ResponseEntity.ok().build();
    }
}
