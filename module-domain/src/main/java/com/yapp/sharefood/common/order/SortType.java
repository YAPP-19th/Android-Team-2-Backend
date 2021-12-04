package com.yapp.sharefood.common.order;

import com.yapp.sharefood.common.exception.ParameterException;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Objects;

@Getter
@AllArgsConstructor
public enum SortType {
    ID("id"), PRICE("price"), LIKE("like");

    private final String value;

    public static SortType of(String value) {
        if (Objects.isNull(value)) {
            return ID;
        }

        return Arrays.stream(values())
                .filter(sortType -> sortType.value.equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(ParameterException::new);
    }
}
