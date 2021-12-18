package com.yapp.sharefood.food.dto.response;

import com.yapp.sharefood.flavor.dto.FlavorDto;
import com.yapp.sharefood.food.domain.Food;
import com.yapp.sharefood.food.dto.FoodImageDto;
import com.yapp.sharefood.food.dto.FoodTagDto;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FoodDetailResponse {
    private Long id;
    private String foodTitle;
    private String reviewDetail;
    private int price;
    private long numberOfLike;
    private String categoryName;

    private String writerName;

    private boolean isMeLike;
    private boolean isMeBookmark;

    private List<FoodTagDto> foodTags;
    private List<FlavorDto> foodFlavors;
    private List<FoodImageDto> foodImages;

    @Builder
    public FoodDetailResponse(Long id, String foodTitle, String reviewDetail, int price, long numberOfLike, boolean isMeLike, boolean isMeBookmark, String categoryName,
                              String writerName, List<FoodTagDto> foodTags, List<FoodImageDto> foodImages, List<FlavorDto> foodFlavors) {
        this.id = id;
        this.foodTitle = foodTitle;
        this.reviewDetail = reviewDetail;
        this.price = price;
        this.numberOfLike = numberOfLike;
        this.categoryName = categoryName;
        this.isMeLike = isMeLike;
        this.isMeBookmark = isMeBookmark;
        this.writerName = writerName;
        this.foodTags = foodTags;
        this.foodFlavors = foodFlavors;
        this.foodImages = foodImages;
    }

    public static FoodDetailResponse toFoodDetailDto(Food food) {
        List<FoodTagDto> foodTagDtos = food.getFoodTags().getFoodTags().stream()
                .map(foodTag -> FoodTagDto.of(foodTag.getId(), foodTag.getTag().getName(), foodTag.getIngredientType()))
                .collect(Collectors.toList());

        return FoodDetailResponse.builder()
                .id(food.getId())
                .foodTitle(food.getFoodTitle())
                .reviewDetail(food.getReviewMsg())
                .price(food.getPrice())
                .numberOfLike(food.getLikeNumber())
                .categoryName(food.getCategory().getName())
                .isMeLike(food.isMeLike(food.getWriter()))
                .isMeBookmark(food.isMeBookMark(food.getWriter()))
                .writerName(food.getWriterNickname())
                .foodTags(foodTagDtos)
                .foodFlavors(FlavorDto.toFoodFlavor(food.getFoodFlavors().getFoodFlavors()))
                .foodImages(FoodImageDto.toList(food.getImages().getImages()))
                .build();
    }
}
