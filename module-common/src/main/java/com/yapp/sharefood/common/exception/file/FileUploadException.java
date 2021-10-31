package com.yapp.sharefood.common.exception.file;

public class FileUploadException extends RuntimeException {
    private static final String FILE_UPLOAD_EXCEPTION = "file upload에 실패했습니다.";

    public FileUploadException() {
        super(FILE_UPLOAD_EXCEPTION);
    }
}
