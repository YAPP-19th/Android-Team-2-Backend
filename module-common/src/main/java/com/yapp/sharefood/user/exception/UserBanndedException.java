package com.yapp.sharefood.user.exception;

public class UserBanndedException extends RuntimeException{
    public static String USER_BANNDED_EXCEPTION_MSG = "이 사용자는 정지된 사용자 입니다.";

    public UserBanndedException() {
        super(USER_BANNDED_EXCEPTION_MSG);
    }

    public UserBanndedException(String message) {
        super(message);
    }
}
