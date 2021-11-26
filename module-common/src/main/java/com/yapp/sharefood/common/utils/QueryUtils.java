package com.yapp.sharefood.common.utils;

import com.yapp.sharefood.common.exception.InvalidOperationException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Objects;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class QueryUtils {
    public static <T> boolean isEmpty(List<T> inputData) {
        return Objects.isNull(inputData) || inputData.isEmpty();
    }

    public static <T> void validateNotEmptyList(List<T> inputData) {
        if (isEmpty(inputData)) {
            throw new InvalidOperationException("List가 비었습니다.");
        }
    }
}
