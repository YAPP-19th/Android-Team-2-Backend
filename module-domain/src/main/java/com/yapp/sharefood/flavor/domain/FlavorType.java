package com.yapp.sharefood.flavor.domain;

public enum FlavorType {
    SPICY("매운맛"), SWEET("단 맛");

    private final String flavorName;

    FlavorType(String value) {
        this.flavorName = value;
    }

    public String getFlavorName() {
        return flavorName;
    }
}
