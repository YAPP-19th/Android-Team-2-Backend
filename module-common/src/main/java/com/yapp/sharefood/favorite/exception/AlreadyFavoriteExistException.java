package com.yapp.sharefood.favorite.exception;

public class AlreadyFavoriteExistException extends RuntimeException {
    public static String ALREADY_FAVORITE_EXIST_EXCEPTION_MSG = "최애가 이미 존재합니다.";

    public AlreadyFavoriteExistException() {
        super(ALREADY_FAVORITE_EXIST_EXCEPTION_MSG);
    }

    public AlreadyFavoriteExistException(String message) {
        super(message);
    }
}
