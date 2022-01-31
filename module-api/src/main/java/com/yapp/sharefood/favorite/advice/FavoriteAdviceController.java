package com.yapp.sharefood.favorite.advice;

import com.yapp.sharefood.common.error.ErrorResponse;
import com.yapp.sharefood.favorite.exception.FavoriteNotFoundException;
import com.yapp.sharefood.favorite.exception.TooManyFavoriteException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class FavoriteAdviceController {
    @ExceptionHandler(FavoriteNotFoundException.class)
    protected ResponseEntity<ErrorResponse> handleFavoriteNotFoundException(final FavoriteNotFoundException exception) {
        log.info("FavoriteNotFoundException: {}", exception.getMessage(), exception);
        return ErrorResponse.toResponseEntity(HttpStatus.NOT_FOUND, exception.getMessage());
    }

    @ExceptionHandler(TooManyFavoriteException.class)
    protected ResponseEntity<ErrorResponse> handleTooManyFavoriteException(final TooManyFavoriteException exception) {
        log.info("handleTooManyFavoriteException : {}", exception.getMessage(), exception);
        return ErrorResponse.toResponseEntity(HttpStatus.TOO_MANY_REQUESTS, exception.getMessage());
    }
}
