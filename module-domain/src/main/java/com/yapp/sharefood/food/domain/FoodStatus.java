package com.yapp.sharefood.food.domain;

public enum FoodStatus {
    SHARED, MINE;

    public boolean isShared() {
        return this.name().equals(SHARED.name());
    }
}
