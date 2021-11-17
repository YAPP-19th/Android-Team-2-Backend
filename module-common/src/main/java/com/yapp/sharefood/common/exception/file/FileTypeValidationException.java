package com.yapp.sharefood.common.exception.file;

import com.yapp.sharefood.common.exception.BadRequestException;

public class FileTypeValidationException extends BadRequestException {
    private static final String FILE_TYPE_VALIDATION_EXCEPTION = "입력된 파일 확장자가 적절하지 않습니다.";

    public FileTypeValidationException() {
        super(FILE_TYPE_VALIDATION_EXCEPTION);
    }
}
