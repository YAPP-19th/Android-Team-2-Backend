package com.yapp.sharefood.common.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Objects;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class QueryUtils {
    public static <T> boolean isEmpty(List<T> inputData) {
        return Objects.isNull(inputData) || inputData.isEmpty();
    }
}
