package com.yapp.sharefood.favorite.exception;

public class FavoriteNotFoundException extends RuntimeException {
    public static final String FAVORITE_NOT_FOUND_EXCEPTION_MSG = "최애를 찾을 수 없습니다.";

    public FavoriteNotFoundException() {
        super(FAVORITE_NOT_FOUND_EXCEPTION_MSG);
    }
}
