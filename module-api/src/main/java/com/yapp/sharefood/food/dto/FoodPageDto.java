package com.yapp.sharefood.food.dto;

import com.yapp.sharefood.favor.domain.FavorType;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class FoodPageDto {
    private String foodName;
    private MultipartFile thumnail;
    private String categoryName;
    private int price;
    private FavorType mainFavorType;
}
