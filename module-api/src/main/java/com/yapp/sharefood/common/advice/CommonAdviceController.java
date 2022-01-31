package com.yapp.sharefood.common.advice;

import com.yapp.sharefood.common.error.ErrorResponse;
import com.yapp.sharefood.common.exception.BadRequestException;
import com.yapp.sharefood.common.exception.InvalidOperationException;
import com.yapp.sharefood.common.exception.file.FileTypeValidationException;
import com.yapp.sharefood.common.exception.file.FileUploadException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
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
    protected ResponseEntity<ErrorResponse> handleFileUploadException(final RuntimeException exception) {
        log.info("FileUploadException: {}", exception.getMessage(), exception);

        return ErrorResponse.toResponseEntity(HttpStatus.BAD_GATEWAY, exception.getMessage());
    }

    /**
     * 400 Bad Request
     * file format 실패
     */
    @ExceptionHandler({
            FileTypeValidationException.class,
            BadRequestException.class,
            BindException.class
    })
    protected ResponseEntity<ErrorResponse> handleBadRequestException(final RuntimeException exception) {
        log.info("File type or BadRequest: {}", exception.getMessage(), exception);

        return ErrorResponse.toResponseEntity(HttpStatus.BAD_REQUEST, exception.getMessage());
    }

    /**
     * 500 Interval Server Error
     * 적절하지 못한 Exception 발생
     */
    @ExceptionHandler(InvalidOperationException.class)
    protected ResponseEntity<ErrorResponse> handleInvalidOperationException(final RuntimeException exception) {
        log.info("InvalidOperationException: {}", exception.getMessage(), exception);

        return ErrorResponse.toResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR, exception.getMessage());
    }

    /**
     * 400 Bad Request Error
     * 적절하지 못한 Method Type
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> mappingException(final MethodArgumentNotValidException exception) {
        log.error("MethodArgumentNotValidException: {}", exception.getMessage(), exception);

        return ErrorResponse.toResponseEntity(HttpStatus.BAD_REQUEST, exception.getMessage());
    }

    /**
     * 500 Interval Server Error
     * 예상하지 못한 에러
     */
    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ErrorResponse> handlerForBaseException(final Exception exception) {
        log.error("Error: {}", exception.getMessage(), exception);

        return ErrorResponse.toResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR, "Interval Server Error");
    }
}
