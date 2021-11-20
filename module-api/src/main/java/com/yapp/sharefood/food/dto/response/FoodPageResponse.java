package com.yapp.sharefood.food.dto.response;

import com.yapp.sharefood.food.domain.Food;
import com.yapp.sharefood.food.dto.FoodPageDto;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FoodPageResponse {
    private List<FoodPageDto> foods;
    private int pageSize;
    private long offset;

    private FoodPageResponse(List<FoodPageDto> foods, int pageSize, long offset) {
        this.foods = foods;
        this.pageSize = pageSize;
        this.offset = offset;
    }

    public static FoodPageResponse ofLastPage(List<Food> foods, int pageSize) {
        return of(foods, pageSize, -1L);
    }

    public static FoodPageResponse of(List<Food> foods, int pageSize, long offset) {
        List<FoodPageDto> content = foods.stream()
                .map(FoodPageDto::toFoodPageDto)
                .collect(Collectors.toList());
        return new FoodPageResponse(content, pageSize, offset);
    }
}
