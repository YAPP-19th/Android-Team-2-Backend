package com.yapp.sharefood.food.dto.response;

import com.yapp.sharefood.food.domain.Food;
import com.yapp.sharefood.food.dto.FoodPageDto;
import com.yapp.sharefood.user.domain.User;
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

    public static FoodPageResponse ofLastPage(List<Food> foods, int pageSize, User user) {
        return of(foods, pageSize, -1L, user);
    }

    public static FoodPageResponse of(List<Food> foods, int pageSize, long offset, User user) {
        List<FoodPageDto> content = foods.stream()
                .map(food -> FoodPageDto.toFoodPageDto(food, user))
                .collect(Collectors.toList());
        return new FoodPageResponse(content, pageSize, offset);
    }

    public static FoodPageResponse ofPureDto(List<FoodPageDto> content, int pageSize, long offset) {
        return new FoodPageResponse(content, pageSize, offset);
    }
}
