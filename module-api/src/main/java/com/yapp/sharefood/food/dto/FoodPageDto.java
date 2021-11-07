package com.yapp.sharefood.food.dto;

import com.yapp.sharefood.flavor.domain.FlavorType;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FoodPageDto {
    private String foodTitle;
    private String thumnailImgUrl;
    private String categoryName;
    private int price;
    private boolean isBookmark;
    private FlavorType mainFlavorType;

    @Builder
    public FoodPageDto(String foodTitle, String thumnailImgUrl, String categoryName, int price, boolean isBookmark, FlavorType mainFlavorType) {
        this.foodTitle = foodTitle;
        this.thumnailImgUrl = thumnailImgUrl;
        this.categoryName = categoryName;
        this.price = price;
        this.isBookmark = isBookmark;
        this.mainFlavorType = mainFlavorType;
    }
}
