package com.yapp.sharefood.food.dto;

import com.yapp.sharefood.favor.domain.FavorType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class FoodPageDto {
    private String foodName;
    private String thumnailImgUrl;
    private String categoryName;
    private int price;
    private boolean isBookmark;
    private FavorType mainFavorType;
}
