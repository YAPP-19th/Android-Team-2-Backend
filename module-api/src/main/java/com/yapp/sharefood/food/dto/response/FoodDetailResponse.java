package com.yapp.sharefood.food.dto.response;

import com.yapp.sharefood.food.dto.FoodFavorDto;
import com.yapp.sharefood.food.dto.FoodTagDto;
import lombok.Data;

import java.util.List;

@Data
public class FoodDetailResponse {
    private String title;
    private String reviewDetail;
    private int price;
    private long numberOfLike;
    private List<FoodFavorDto> foodFavors;
    private List<FoodTagDto> foodTags;
}
