package com.yapp.sharefood.category.exception;

import com.yapp.sharefood.common.exception.NotFoundException;

public class CategoryNotFoundException extends NotFoundException {
    private static final String CATEGORY_NOT_FOUND_EXCEPTION_MSG = "해당 category를 찾을 수 없습니다";

    public CategoryNotFoundException() {
        super(CATEGORY_NOT_FOUND_EXCEPTION_MSG);
    }
}
