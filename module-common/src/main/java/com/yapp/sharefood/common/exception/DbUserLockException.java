package com.yapp.sharefood.common.exception;

public class DbUserLockException extends RuntimeException {
    public DbUserLockException(String msg) {
        super(msg);
    }

    public DbUserLockException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
