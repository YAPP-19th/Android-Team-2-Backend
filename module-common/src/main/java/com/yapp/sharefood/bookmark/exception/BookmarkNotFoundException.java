package com.yapp.sharefood.bookmark.exception;

import com.yapp.sharefood.common.exception.NotFoundException;

public class BookmarkNotFoundException extends NotFoundException {
    private static final String LIKE_NOT_FOUND_EXCEPTION_MSG = "bookmark를 찾지 못 하였습니다.";

    public BookmarkNotFoundException() {
        super(LIKE_NOT_FOUND_EXCEPTION_MSG);
    }
}
