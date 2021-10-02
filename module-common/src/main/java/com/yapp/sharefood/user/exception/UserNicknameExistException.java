package com.yapp.sharefood.user.exception;

public class UserNicknameExistException extends RuntimeException {
    private static final String NICKNAME_EXIST_EXCEPTION_MSG = "설정한 nickname은 현재 다른 사용자가 사용하고 있습니다.";

    public UserNicknameExistException() {
        super(NICKNAME_EXIST_EXCEPTION_MSG);
    }

    public UserNicknameExistException(String msg) {
        super(msg);
    }
}
