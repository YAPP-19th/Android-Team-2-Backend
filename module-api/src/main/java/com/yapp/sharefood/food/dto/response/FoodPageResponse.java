package com.yapp.sharefood.food.dto.response;

import com.yapp.sharefood.food.dto.FoodPageDto;
import lombok.Data;
import org.springframework.data.domain.Slice;

@Data
public class FoodPageResponse {
    private Slice<FoodPageDto> foods;
}
