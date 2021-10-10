package com.yapp.sharefood.food.dto.request;

import com.yapp.sharefood.food.dto.FoodCategoryDto;
import com.yapp.sharefood.food.dto.FoodFavorDto;
import com.yapp.sharefood.food.dto.FoodTagDto;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class FoodCreatRequest {
    private FoodCategoryDto foodCategory; // first page

    private String title;
    private List<FoodTagDto> foodTags;
    private int price;
    private List<FoodFavorDto> foodFavors;

    private String reviewMsg;
    private List<MultipartFile> foodImages;

    private String foodStatus;
}
