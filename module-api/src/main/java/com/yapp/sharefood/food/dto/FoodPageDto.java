package com.yapp.sharefood.food.dto;

import com.yapp.sharefood.food.domain.Food;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;
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
    private boolean isBookmark;
    private List<FoodImageDto> foodimages;

    @Builder
    public FoodPageDto(Long id, String foodTitle, String categoryName, int price, boolean isBookmark, long numberOfLikes, List<FoodImageDto> foodImages) {
        this.id = id;
        this.foodTitle = foodTitle;
        this.categoryName = categoryName;
        this.price = price;
        this.isBookmark = isBookmark;
        this.numberOfLikes = numberOfLikes;
        this.foodimages = foodImages;
    }

    public static FoodPageDto toFoodPageDto(Food food, long numberOfLikes) {
        return FoodPageDto.builder()
                .id(food.getId())
                .foodTitle(food.getFoodTitle())
                .categoryName(food.getCategory().getName())
                .price(food.getPrice())
                .numberOfLikes(numberOfLikes)
                .foodImages(FoodImageDto.toList(food.getImages().getImages()))
                .build();
    }

    public static FoodPageDto toFoodPageDto(Food food) {
        return FoodPageDto.builder()
                .id(food.getId())
                .foodTitle(food.getFoodTitle())
                .categoryName(food.getCategory().getName())
                .price(food.getPrice())
                .numberOfLikes(food.getNumberOfLikes())
                .foodImages(FoodImageDto.toList(food.getImages().getImages()))
                .build();
    }

    public static List<FoodPageDto> toList(List<Food> foods, Map<Long, Long> foodIdKeylikeCountMap) {
        return foods.stream()
                .map(food -> FoodPageDto.toFoodPageDto(food, foodIdKeylikeCountMap.get(food.getId())))
                .collect(Collectors.toList());
    }

    public static List<FoodPageDto> toList(List<Food> foods) {
        return foods.stream()
                .map(FoodPageDto::toFoodPageDto)
                .collect(Collectors.toList());
    }
}
