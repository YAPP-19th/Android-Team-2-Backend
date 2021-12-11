package com.yapp.sharefood.food.dto;

import com.yapp.sharefood.food.domain.Food;
import com.yapp.sharefood.user.domain.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FoodPageDto {
    private static final int FIRST_IMAGE_INDEX = 0;

    private Long id;
    private String foodTitle;
    private String categoryName;
    private int price;
    private long numberOfLikes;
    private boolean isMeBookmark;
    private boolean isMeLike;
    private List<FoodImageDto> foodimages;

    @Builder
    public FoodPageDto(Long id, String foodTitle, String categoryName, int price, boolean isMeBookmark, boolean isMeLike, long numberOfLikes, List<FoodImageDto> foodImages) {
        this.id = id;
        this.foodTitle = foodTitle;
        this.categoryName = categoryName;
        this.price = price;
        this.isMeBookmark = isMeBookmark;
        this.isMeLike = isMeLike;
        this.numberOfLikes = numberOfLikes;
        this.foodimages = foodImages;
    }

    public static FoodPageDto toFoodPageDto(Food food, User user) {
        return FoodPageDto.builder()
                .id(food.getId())
                .foodTitle(food.getFoodTitle())
                .categoryName(food.getCategory().getName())
                .price(food.getPrice())
                .numberOfLikes(food.getNumberOfLikes())
                .numberOfLikes(food.getNumberOfLikes())
                .isMeBookmark(food.isMeBookMark(user))
                .foodImages(FoodImageDto.toList(food.getImages().getImages()))
                .build();
    }

    public static List<FoodPageDto> toList(List<Food> foods, User user) {
        return foods.stream()
                .map(food -> FoodPageDto.toFoodPageDto(food, user))
                .collect(Collectors.toList());
    }
}
