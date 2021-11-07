package com.yapp.sharefood.food.dto.response;

import com.yapp.sharefood.food.dto.FoodPageDto;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Slice;

@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FoodPageResponse {
    private Slice<FoodPageDto> foods;
}
