package com.yapp.sharefood.common.exception;

public class BadRequestException extends RuntimeException {
    private static final String BADREQUEST_EXCEPTION_MSG = "적절하지 못한 입력 값 입니다.";

    public BadRequestException() {
        super(BADREQUEST_EXCEPTION_MSG);
    }

    public BadRequestException(String msg) {
        super(msg);
    }
}
