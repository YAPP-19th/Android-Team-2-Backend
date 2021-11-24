package com.yapp.sharefood.common.exception;

public class ParameterException extends RuntimeException {
    private static final String PARAMETER_EXCEPTION_MSG = "parameter 가 적절하지 못합니다.";

    public ParameterException() {
        super(PARAMETER_EXCEPTION_MSG);
    }
}
