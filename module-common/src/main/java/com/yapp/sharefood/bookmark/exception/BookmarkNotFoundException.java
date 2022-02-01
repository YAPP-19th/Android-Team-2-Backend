package com.yapp.sharefood.bookmark.exception;

import com.yapp.sharefood.common.exception.NotFoundException;

public class BookmarkNotFoundException extends NotFoundException {
    private static final String BOOKMARK_NOT_FOUND_EXCEPTION_MSG = "북마크를 찾지 못하였습니다.";

    public BookmarkNotFoundException() {
        super(BOOKMARK_NOT_FOUND_EXCEPTION_MSG);
    }
}
