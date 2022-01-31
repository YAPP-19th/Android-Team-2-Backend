package com.yapp.sharefood.food.exception;

public class FoodBanndedException extends RuntimeException {
    private static final String FOOD_BANNDED_EXCEPTION_MSG = "이 음식은 정지된 음식입니다.";

    public FoodBanndedException() {
        super(FOOD_BANNDED_EXCEPTION_MSG);
    }
}
