package com.yapp.sharefood.food.dto.request;

import com.yapp.sharefood.food.domain.FoodStatus;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
public class FoodCreationRequest {
    @NotNull
    @NotBlank
    private String title;

    @NotNull
    private Integer price;

    @NotNull
    @NotBlank
    private String reviewMsg;

    @NotNull
    private FoodStatus foodStatus;

    @Builder
    public FoodCreationRequest(String title, Integer price, String reviewMsg, FoodStatus foodStatus) {
        this.title = title;
        this.price = price;
        this.reviewMsg = reviewMsg;
        this.foodStatus = foodStatus;
    }
}
