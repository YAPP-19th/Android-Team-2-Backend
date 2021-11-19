package com.yapp.sharefood.flavor.domain;

import com.yapp.sharefood.flavor.exception.FlavorNotFoundException;

import java.util.Arrays;

public enum FlavorType {
    SWEET("단맛"), SPICY("매운맛"), SALTY("짠맛"), BITTER("쓴맛"), SOUR("씬맛"),
    SWEET_DETAIL("달콤함"), PLAIN_DETAIL("단백한"), SOUR_DETAIL("새콤한"), COOL_DETAIL("시원한"), SALTY_DETAIL("짭짤한"), GREASY_DETAIL("느끼한"),
    ODD_DETAIL("삼삼함"), TOO_SPICY_DETAIL("매콤한"), SPICY_DETAIL("얼큰한"), CLEAR_DETAIL("깔끔한"), SAVORY_DETAIL("고소한"), REFRESH_DETAIL("개운한");

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
