package com.yapp.sharefood.food.dto;

import com.yapp.sharefood.image.domain.Image;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FoodImageDto {
    private Long id;
    private String imageUrl;
    private String realImageName;

    public static FoodImageDto toFoodImageDto(Image image) {
        return new FoodImageDto(image.getId(), image.getStoreFilename(), image.getRealFilename());
    }

    public static List<FoodImageDto> toList(List<Image> images) {
        return images.stream().map(FoodImageDto::toFoodImageDto)
                .collect(Collectors.toList());
    }
}
