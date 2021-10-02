package com.yapp.sharefood.oauth.exception;

import com.yapp.sharefood.common.exception.ConflictException;

public class OAUthExistException extends ConflictException {
    public OAUthExistException(String msg) {
        super(msg);
    }
}
