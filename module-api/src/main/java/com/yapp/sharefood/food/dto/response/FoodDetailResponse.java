package com.yapp.sharefood.food.dto.response;

import com.yapp.sharefood.food.dto.FoodImageDto;
import com.yapp.sharefood.food.dto.FoodTagDto;
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

    private String writerName;

    private boolean isMeLike;
    private boolean isBookmark;
    private boolean isMyFlavorite;

    private List<FoodTagDto> foodTags;
    private List<FoodImageDto> foodImages;

    @Builder
    public FoodDetailResponse(Long id, String foodTitle, String reviewDetail, int price, long numberOfLike, boolean isMeLike, boolean isBookmark,
                              boolean isMyFlavorite, String writerName, List<FoodTagDto> foodTags, List<FoodImageDto> foodImages) {
        this.id = id;
        this.foodTitle = foodTitle;
        this.reviewDetail = reviewDetail;
        this.price = price;
        this.numberOfLike = numberOfLike;
        this.isMeLike = isMeLike;
        this.isBookmark = isBookmark;
        this.isMyFlavorite = isMyFlavorite;
        this.writerName = writerName;
        this.foodTags = foodTags;
        this.foodImages = foodImages;
    }
}
