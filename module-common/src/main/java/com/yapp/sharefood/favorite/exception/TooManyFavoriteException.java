package com.yapp.sharefood.favorite.exception;

public class TooManyFavoriteException extends RuntimeException {
    private static final String TOO_MANY_FAVORITE_EXCEPTION_MSG = "최애는 5개를 넘을 수 없습니다.";

    public TooManyFavoriteException() {
        super(TOO_MANY_FAVORITE_EXCEPTION_MSG);
    }
}
