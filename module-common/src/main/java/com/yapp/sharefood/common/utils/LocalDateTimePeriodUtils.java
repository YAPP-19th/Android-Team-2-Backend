package com.yapp.sharefood.common.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LocalDateTimePeriodUtils {
    public static LocalDateTime getBeforePeriod(int period) {
        return LocalDateTime.now().minusDays(period);
    }

    public static LocalDateTime now() {
        return LocalDateTime.now();
    }
}
