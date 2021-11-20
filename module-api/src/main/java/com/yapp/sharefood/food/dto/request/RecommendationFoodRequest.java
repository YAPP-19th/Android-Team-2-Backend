package com.yapp.sharefood.food.dto.request;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RecommendationFoodRequest {
    @Min(5)
    @Max(10)
    @NotNull
    private Integer top;

    @Max(10)
    @Min(3)
    @NotNull
    private Integer rankDatePeriod;

    @NotNull
    @NotBlank
    private String categoryName;

    private RecommendationFoodRequest(Integer top, Integer rankDatePeriod, String categoryName) {
        this.top = top;
        this.rankDatePeriod = rankDatePeriod;
        this.categoryName = categoryName;
    }

    public static RecommendationFoodRequest of(Integer top, Integer rankDatePeriod, String categoryName) {
        return new RecommendationFoodRequest(top, rankDatePeriod, categoryName);
    }
}
