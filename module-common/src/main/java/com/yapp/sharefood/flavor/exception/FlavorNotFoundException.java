package com.yapp.sharefood.flavor.exception;

import com.yapp.sharefood.common.exception.NotFoundException;

public class FlavorNotFoundException extends NotFoundException {
    public static final String FLAVOR_NOT_FOUND_EXCEPTION_MSG = "일치하는 맛을 찾을 수 없습니다.";

    public FlavorNotFoundException() {
        super(FLAVOR_NOT_FOUND_EXCEPTION_MSG);
    }
}
