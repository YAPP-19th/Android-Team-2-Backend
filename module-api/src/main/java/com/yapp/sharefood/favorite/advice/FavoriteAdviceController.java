package com.yapp.sharefood.favorite.advice;

import com.yapp.sharefood.favorite.exception.FavoriteNotFoundException;
import com.yapp.sharefood.favorite.exception.TooManyFavoriteException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class FavoriteAdviceController {
    @ExceptionHandler(FavoriteNotFoundException.class)
    protected ResponseEntity<Object> handleFavoriteNotFoundException(final FavoriteNotFoundException exception) {
        log.info("FavoriteNotFoundException: {}", exception.getMessage(), exception);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exception.getMessage());
    }

    @ExceptionHandler(TooManyFavoriteException.class)
    protected ResponseEntity<Object> handleTooManyFavoriteException(final TooManyFavoriteException exception) {
        log.info("handleTooManyFavoriteException : {}", exception.getMessage(), exception);
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(exception.getMessage());
    }
}
