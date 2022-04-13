package com.yapp.sharefood.food.dto.response;

import com.yapp.sharefood.flavor.dto.FlavorDto;
import com.yapp.sharefood.food.domain.Food;
import com.yapp.sharefood.food.dto.FoodImageDto;
import com.yapp.sharefood.food.dto.FoodTagDto;
import com.yapp.sharefood.user.domain.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

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

    private boolean isMyFood;
    private boolean isMeLike;
    private boolean isMeBookmark;

    private List<FoodTagDto> foodTags;
    private List<FlavorDto> foodFlavors;
    private List<FoodImageDto> foodImages;

    @Builder
    public FoodDetailResponse(Long id, String foodTitle, String reviewDetail, int price, long numberOfLike, boolean isMyFood, boolean isMeLike, boolean isMeBookmark, String categoryName,
                              String writerName, List<FoodTagDto> foodTags, List<FoodImageDto> foodImages, List<FlavorDto> foodFlavors) {
        this.id = id;
        this.foodTitle = foodTitle;
        this.reviewDetail = reviewDetail;
        this.price = price;
        this.numberOfLike = numberOfLike;
        this.categoryName = categoryName;
        this.isMyFood = isMyFood;
        this.isMeLike = isMeLike;
        this.isMeBookmark = isMeBookmark;
        this.writerName = writerName;
        this.foodTags = foodTags;
        this.foodFlavors = foodFlavors;
        this.foodImages = foodImages;
    }

    public static FoodDetailResponse toFoodDetailDto(User user, Food food) {
        return FoodDetailResponse.builder()
                .id(food.getId())
                .foodTitle(food.getFoodTitle())
                .reviewDetail(food.getReviewMsg())
                .price(food.getPrice())
                .numberOfLike(food.getLikeNumber())
                .categoryName(food.getCategory().getName())
                .isMyFood(food.isMyFood(user))
                .isMeLike(food.isMeLike(food.getWriter()))
                .isMeBookmark(food.isMeBookMark(food.getWriter()))
                .writerName(food.getWriterNickname())
                .foodTags(FoodTagDto.toList(food.getFoodTags().getFoodTags()))
                .foodFlavors(FlavorDto.toFoodFlavor(food.getFoodFlavors().getFoodFlavors()))
                .foodImages(FoodImageDto.toList(food.getImages().getImages()))
                .build();
    }
}
