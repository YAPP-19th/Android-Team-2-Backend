package com.yapp.sharefood.common.exception;

public class ForbiddenException extends RuntimeException {
    private static final String FORIBIDDEN_EXCECPTION_MSG = "허용되지 않은 사용자 입니다.";

    public ForbiddenException() {
        super(FORIBIDDEN_EXCECPTION_MSG);
    }
}
