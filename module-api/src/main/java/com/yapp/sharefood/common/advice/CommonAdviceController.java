package com.yapp.sharefood.common.advice;

import com.yapp.sharefood.common.exception.BadRequestException;
import com.yapp.sharefood.common.exception.InvalidOperationException;
import com.yapp.sharefood.common.exception.file.FileTypeValidationException;
import com.yapp.sharefood.common.exception.file.FileUploadException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
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
        log.info("FileUploadException: {}", exception.getMessage(), exception);

        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(exception.getMessage());
    }

    /**
     * 400 Bad Request
     * file format 실패
     */
    @ExceptionHandler({FileTypeValidationException.class, BadRequestException.class})
    protected ResponseEntity<Object> handleBadRequestException(final RuntimeException exception) {
        log.info("File type or BadRequest: {}", exception.getMessage(), exception);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exception.getMessage());
    }

    /**
     * 500 Interval Server Error
     * 적절하지 못한 Exception 발생
     */
    @ExceptionHandler({InvalidOperationException.class})
    protected ResponseEntity<Object> handleInvalidOperationException(final RuntimeException exception) {
        log.info("InvalidOperationException: {}", exception.getMessage(), exception);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exception.getMessage());
    }

    /**
     * 400 Bad Request Error
     * 적절하지 못한 Method Type
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> mappingException(MethodArgumentNotValidException e) {
        log.error("MethodArgumentNotValidException: {}", e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }

    /**
     * 500 Interval server Error Default
     * 적절하지 못한 모든 Exception 발생
     */
    @ExceptionHandler({Exception.class})
    protected ResponseEntity<Object> handleDefaultException(final Exception exception) {
        log.error("Exception: {}", exception.getMessage(), exception);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exception.getMessage());
    }
}
