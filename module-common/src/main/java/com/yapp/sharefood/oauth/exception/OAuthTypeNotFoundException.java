package com.yapp.sharefood.oauth.exception;

public class OAuthTypeNotFoundException extends NotFoundException {
    private static final String ERR_MSG = "OAuth 요청 케이스가 잘 못 되었습니다.";

    public OAuthTypeNotFoundException() {
        super(ERR_MSG);
    }
}
