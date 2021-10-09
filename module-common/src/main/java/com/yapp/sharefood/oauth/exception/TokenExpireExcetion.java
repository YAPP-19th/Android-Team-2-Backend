package com.yapp.sharefood.oauth.exception;

public class TokenExpireExcetion extends RuntimeException {
    private static final String TOKEN_EXPIRE_ERR_MSG = "token이 만료가 되었습니다.";

    public TokenExpireExcetion() {
        super(TOKEN_EXPIRE_ERR_MSG);
    }
}
