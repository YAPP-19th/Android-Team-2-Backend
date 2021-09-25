package com.yapp.sharefood.oauth.exception;

import com.yapp.sharefood.common.exception.NotFoundException;

public class UserNotFoundException extends NotFoundException {
    private static final String USER_NOT_FOUND_EXCEPTION_MSG = "";

    public UserNotFoundException() {
        super(USER_NOT_FOUND_EXCEPTION_MSG);
    }
}
