package com.yapp.sharefood.tag.exception;

import com.yapp.sharefood.common.exception.ConflictException;

public class TagConflictException extends ConflictException {
    private static final String TAG_CONFLICT_EXCEPTION_MSG = "tag 가 이미 존재합니다.";

    public TagConflictException() {
        super(TAG_CONFLICT_EXCEPTION_MSG);
    }
}
