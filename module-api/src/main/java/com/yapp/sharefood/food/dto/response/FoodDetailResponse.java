package com.yapp.sharefood.food.dto.response;

import com.yapp.sharefood.food.dto.FoodFlavorDto;
import com.yapp.sharefood.food.dto.FoodImageDto;
import com.yapp.sharefood.food.dto.FoodTagDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class FoodDetailResponse {
    private String title;
    private String reviewDetail;
    private int price;
    private long numberOfLike;

    private boolean isMeLike;
    private boolean isBookmark;
    private boolean isMyFlavorite;

    private List<FoodFlavorDto> foodFlavors;
    private List<FoodTagDto> foodTags;
    private List<FoodImageDto> foodImages;
}
