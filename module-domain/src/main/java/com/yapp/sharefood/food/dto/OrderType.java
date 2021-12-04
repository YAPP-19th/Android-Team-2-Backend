package com.yapp.sharefood.food.dto;

import com.yapp.sharefood.common.exception.ParameterException;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Objects;

@Getter
@AllArgsConstructor
public enum OrderType {
    DESC("desc"),
    ASC("asc");

    private String order;

    public static OrderType of(String value) {
        if (Objects.isNull(value)) {
            return DESC;
        }

        return Arrays.stream(values())
                .filter(orderType -> orderType.order.equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(ParameterException::new);
    }
}
