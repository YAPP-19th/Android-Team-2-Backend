package com.yapp.sharefood.tag.exception;

import com.yapp.sharefood.common.exception.NotFoundException;

public class TagNotFoundException extends NotFoundException {
    private static final String TAG_NOT_FOUND_EXCEPTION_MSG = "tag 정보를 찾을 수 없습니다.";

    public TagNotFoundException() {
        super(TAG_NOT_FOUND_EXCEPTION_MSG);
    }
}
