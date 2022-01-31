package com.yapp.sharefood.food.exception;

import com.yapp.sharefood.common.exception.NotFoundException;

public class FoodNotFoundException extends NotFoundException {
    private static final String FOOD_NOT_FOUND_EXCEPTION_MSG = "음식을 찾을 수 없습니다";

    public FoodNotFoundException() {
        super(FOOD_NOT_FOUND_EXCEPTION_MSG);
    }
}
