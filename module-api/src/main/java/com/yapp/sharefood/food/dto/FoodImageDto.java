package com.yapp.sharefood.food.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FoodImageDto {
    private Long id;
    private String imageUrl;
}
