package com.yapp.sharefood.common.order;

import com.yapp.sharefood.common.exception.ParameterException;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Objects;

@Getter
@AllArgsConstructor
public enum SortType {
    DESC("desc"),
    ASC("asc");

    private String order;

    public static SortType of(String value) {
        if (Objects.isNull(value)) {
            return DESC;
        }

        return Arrays.stream(values())
                .filter(sortType -> sortType.order.equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(ParameterException::new);
    }
}
