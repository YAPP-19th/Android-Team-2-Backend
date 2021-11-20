package com.yapp.sharefood.food.dto;

import com.yapp.sharefood.common.exception.ParameterException;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Objects;

@Getter
@AllArgsConstructor
public enum FoodOrderType {
    ID("id"), PRICE("price"), LIKE("like"), BOOKMARK("bookmark");

    private final String value;

    public static FoodOrderType of(String value) {
        if (Objects.isNull(value)) {
            return ID;
        }

        return Arrays.stream(values())
                .filter(foodOrderType -> foodOrderType.value.equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(ParameterException::new);
    }
}
