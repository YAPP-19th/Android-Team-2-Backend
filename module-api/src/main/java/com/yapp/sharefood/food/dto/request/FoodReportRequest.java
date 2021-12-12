package com.yapp.sharefood.food.dto.request;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Data
public class FoodReportRequest {
    private String foodReportMessage;

    @Builder
    public FoodReportRequest(String foodReportMessage) {
        this.foodReportMessage = foodReportMessage;
    }
}
