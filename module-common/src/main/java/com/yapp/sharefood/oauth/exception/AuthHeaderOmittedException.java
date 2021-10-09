package com.yapp.sharefood.oauth.exception;

public class AuthHeaderOmittedException extends RuntimeException {
    private static final String AUTH_HEADER_OMITTED_EXCEPTION_MSG = "사용자 권한 요청정보가 없습니다.";

    public AuthHeaderOmittedException() {
        super(AUTH_HEADER_OMITTED_EXCEPTION_MSG);
    }
}
