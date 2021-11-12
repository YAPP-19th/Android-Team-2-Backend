package com.yapp.sharefood.like.exception;

import com.yapp.sharefood.common.exception.NotFoundException;

public class LikeNotFoundException extends NotFoundException {
    private static final String LIKE_NOT_FOUND_EXCEPTION_MSG = "like를 찾지 못 하였습니다.";

    public LikeNotFoundException() {
        super(LIKE_NOT_FOUND_EXCEPTION_MSG);
    }
}
