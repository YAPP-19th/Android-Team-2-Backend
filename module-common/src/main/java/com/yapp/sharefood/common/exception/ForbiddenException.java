package com.yapp.sharefood.common.exception;

public class ForbiddenException extends RuntimeException {
    public static final String FORBIDDEN_EXCEPTION_MSG = "허용되지 않은 사용자 입니다.";

    public ForbiddenException() {
        super(FORBIDDEN_EXCEPTION_MSG);
    }
}
