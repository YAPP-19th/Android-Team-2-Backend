package com.yapp.sharefood.food.dto.response;

import com.yapp.sharefood.food.dto.FoodImageDto;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FoodImageCreateResponse {
    private List<FoodImageDto> images;
}
