package com.yapp.sharefood.flavor.dto;

import com.yapp.sharefood.flavor.domain.FlavorType;
import com.yapp.sharefood.food.domain.FoodFlavor;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FlavorDto {
    private Long id;
    private String flavorName;

    private FlavorDto(Long id, String flavorName) {
        this.id = id;
        this.flavorName = flavorName;
    }

    public static FlavorDto of(Long id, FlavorType flavorType) {
        return new FlavorDto(id, flavorType.getFlavorName());
    }

    public static List<FlavorDto> toFoodFlavor(List<FoodFlavor> foodFlavors) {
        return foodFlavors.stream()
                .map(foodFlavor -> of(foodFlavor.getFlavor().getId(), foodFlavor.getFlavor().getFlavorType()))
                .collect(Collectors.toList());
    }
}
