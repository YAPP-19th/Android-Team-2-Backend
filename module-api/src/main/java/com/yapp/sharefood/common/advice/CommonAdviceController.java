package com.yapp.sharefood.common.advice;

import com.yapp.sharefood.common.exception.BadRequestException;
import com.yapp.sharefood.common.exception.InvalidOperationException;
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
    @ExceptionHandler({FileTypeValidationException.class, BadRequestException.class})
    protected ResponseEntity<Object> handleBadRequestException(final RuntimeException exception) {
        log.error(exception.getMessage(), exception);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exception.getMessage());
    }

    /**
     * 500 Interval Server Error
     * 적절하지 못한 Exception 발생
     */
    @ExceptionHandler({InvalidOperationException.class})
    protected ResponseEntity<Object> handleInvalidOperationException(final RuntimeException exception) {
        log.error(exception.getMessage(), exception);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exception.getMessage());
    }
}
