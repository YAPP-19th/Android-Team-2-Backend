package com.yapp.sharefood.flavor.domain;

import com.yapp.sharefood.flavor.exception.FlavorNotFoundException;

import java.util.Arrays;

public enum FlavorType {
    SPICY("매운맛"), SWEET("단 맛");

    private final String flavorName;

    FlavorType(String value) {
        this.flavorName = value;
    }

    public String getFlavorName() {
        return flavorName;
    }

    public static FlavorType of(String name) {
        return Arrays.stream(FlavorType.values())
                .filter(flavorType -> flavorType.getFlavorName().equals(name))
                .findAny()
                .orElseThrow(FlavorNotFoundException::new);
    }
}
