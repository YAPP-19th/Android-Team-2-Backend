package com.yapp.sharefood.common.exception;

public class ConflictException extends RuntimeException {
    private static final String DATA_CONFLICT_EXCEPTION_MSG = "데이터의 충돌이 발생했습니다.";

    public ConflictException(String msg) {
        super(msg);
    }

    public ConflictException() {
        super(DATA_CONFLICT_EXCEPTION_MSG);
    }
}
