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
    private long nextCursor;

    private FoodPageResponse(List<FoodPageDto> foods, long nextCursor) {
        this.foods = foods;
        this.nextCursor = nextCursor;
    }

    public static FoodPageResponse ofLastPage(List<Food> foods) {
        return of(foods, -1L);
    }

    public static FoodPageResponse of(List<Food> foods, long nextCursor) {
        List<FoodPageDto> content = foods.stream()
                .map(FoodPageDto::toFoodPageDto)
                .collect(Collectors.toList());
        return new FoodPageResponse(content, nextCursor);
    }
}
