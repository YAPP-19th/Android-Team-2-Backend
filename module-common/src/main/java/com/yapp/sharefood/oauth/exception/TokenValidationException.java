package com.yapp.sharefood.oauth.exception;

public class TokenValidationException extends RuntimeException {
    private static final String TOKNE_VALIDATION_ERR_MSG = "token이 validate하지 않습니다.";

    public TokenValidationException() {
        super(TOKNE_VALIDATION_ERR_MSG);
    }
}
