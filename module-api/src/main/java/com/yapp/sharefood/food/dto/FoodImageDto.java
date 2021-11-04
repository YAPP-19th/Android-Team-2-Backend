package com.yapp.sharefood.food.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FoodImageDto {
    private Long id;
    private String imageUrl;
    private String realImageName;
}
