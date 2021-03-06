package com.yapp.sharefood.food.dto.request;

import com.yapp.sharefood.food.domain.FoodStatus;
import com.yapp.sharefood.food.dto.FoodTagDto;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FoodCreationRequest {
    // step 1
    @NotBlank
    @NotNull
    private String categoryName;

    // step 2
    @NotNull
    @NotBlank
    private String title;

    @NotNull
    private List<FoodTagDto> tags = new ArrayList<>();

    @NotNull
    private Integer price;

    @NotNull
    private List<String> flavors = new ArrayList<>();

    @NotNull
    @NotBlank
    private String reviewMsg;

    @NotNull
    private FoodStatus foodStatus;

    @Builder
    public FoodCreationRequest(String categoryName, String title, List<FoodTagDto> tags, Integer price, List<String> flavors, String reviewMsg, FoodStatus foodStatus) {
        this.categoryName = categoryName;
        this.title = title;
        this.tags = tags;
        this.price = price;
        this.flavors = flavors;
        this.reviewMsg = reviewMsg;
        this.foodStatus = foodStatus;
    }
}
