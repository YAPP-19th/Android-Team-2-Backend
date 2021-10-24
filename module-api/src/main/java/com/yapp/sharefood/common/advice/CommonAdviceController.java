package com.yapp.sharefood.common.advice;

import com.yapp.sharefood.common.exception.file.FileTypeValidationException;
import com.yapp.sharefood.common.exception.file.FileUploadException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class CommonAdviceController {

    /**
     * 502 Bad Gateway
     * file upload 실패
     */
    @ExceptionHandler(FileUploadException.class)
    protected ResponseEntity<Object> handleFileUploadException(final RuntimeException exception) {
        log.error(exception.getMessage(), exception);

        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(exception.getMessage());
    }

    /**
     * 400 Bad Request
     * file format 실패
     */
    @ExceptionHandler(FileTypeValidationException.class)
    protected ResponseEntity<Object> handleFileTypeValidationException(final RuntimeException exception) {
        log.error(exception.getMessage(), exception);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exception.getMessage());
    }
}
