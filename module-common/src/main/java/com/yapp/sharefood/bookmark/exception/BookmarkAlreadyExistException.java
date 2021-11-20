package com.yapp.sharefood.bookmark.exception;

import com.yapp.sharefood.common.exception.ConflictException;

public class BookmarkAlreadyExistException extends ConflictException {
    private static final String BOOKMARK_ALREADY_EXIST_MSG = "북마크가 이미 존재합니다.";

    public BookmarkAlreadyExistException() {
        super(BOOKMARK_ALREADY_EXIST_MSG);
    }
}
