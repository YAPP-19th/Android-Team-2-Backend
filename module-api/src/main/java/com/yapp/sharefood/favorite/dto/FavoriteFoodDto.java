package com.yapp.sharefood.favorite.dto;

import com.yapp.sharefood.food.dto.FoodImageDto;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FavoriteFoodDto {
    private Long id;
    private String foodTitle;
    private String categoryName;
    private int price;
    private boolean isMeFavorite;
    private List<FoodImageDto> foodImages;

    private FavoriteFoodDto(Long id, String title, String categoryName, int price, boolean isMeFavorite, List<FoodImageDto> images) {
        this.id = id;
        this.foodTitle = title;
        this.categoryName = categoryName;
        this.price = price;
        this.isMeFavorite = isMeFavorite;
        this.foodImages = images;
    }

    public static FavoriteFoodDto of(Long id, String title, String categoryName, int price, boolean isMeFavorite, List<FoodImageDto> images) {
        return new FavoriteFoodDto(id, title, categoryName, price, isMeFavorite, images);
    }
}