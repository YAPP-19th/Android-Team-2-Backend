package com.yapp.sharefood.common.domain;

import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@ToString
public class ApiErrorEvent {
    private final int code;
    private final String message;
    private final LocalDateTime errorTime;

    public ApiErrorEvent(int code, String message) {
        this.code = code;
        this.message = message;
        this.errorTime = LocalDateTime.now();
    }
}
