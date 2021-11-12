package com.yapp.sharefood.food.dto.request;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FoodTopRankRequest {
    @Min(5)
    @Max(10)
    @NotNull
    private Integer top;

    @Max(10)
    @Min(3)
    @NotNull
    private Integer rankDatePeriod;

    private FoodTopRankRequest(Integer top, Integer rankDatePeriod) {
        this.top = top;
        this.rankDatePeriod = rankDatePeriod;
    }

    public static FoodTopRankRequest of(Integer top, Integer rankDatePeriod) {
        return new FoodTopRankRequest(top, rankDatePeriod);
    }
}
