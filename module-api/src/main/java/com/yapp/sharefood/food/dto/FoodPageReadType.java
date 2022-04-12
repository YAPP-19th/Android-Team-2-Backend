package com.yapp.sharefood.food.dto;

import com.yapp.sharefood.common.exception.InvalidOperationException;
import lombok.Getter;

@Getter
public enum FoodPageReadType {
    FLAVOR_READ("foodPageFlavorFilterReadStrategy"), TAG_READ("foodPageTagFilterReadStrategy"), PURE_READ("foodPagePureReadStrategy");

    private final String key;

    FoodPageReadType(String key) {
        this.key = key;
    }

    public static FoodPageReadType of(FoodPageSearch foodPageSearch) {
        if (!foodPageSearch.getTags().isEmpty() && !foodPageSearch.getFlavors().isEmpty()) {
            throw new InvalidOperationException("flavor와 tag는 중복으로 filter 할 수 없습니다.");
        }

        if (!foodPageSearch.getTags().isEmpty()) {
            return TAG_READ;
        } else if (!foodPageSearch.getFlavors().isEmpty()) {
            return FLAVOR_READ;
        }

        return PURE_READ;
    }
}
