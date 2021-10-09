package com.yapp.sharefood.oauth.exception;

import com.yapp.sharefood.common.exception.NotFoundException;

public class UserNotFoundException extends NotFoundException {
    private static final String USER_NOT_FOUND_EXCEPTION_MSG = "등록된 회원이 아닙니다.";

    public UserNotFoundException() {
        super(USER_NOT_FOUND_EXCEPTION_MSG);
    }
}
