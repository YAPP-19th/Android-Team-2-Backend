package com.yapp.sharefood.bookmark.advice;

import com.yapp.sharefood.bookmark.exception.BookmarkAlreadyExistException;
import com.yapp.sharefood.bookmark.exception.BookmarkNotFoundException;
import com.yapp.sharefood.common.error.ErrorResponse;
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
public class BookmarkAdviceController {

    @ExceptionHandler(BookmarkNotFoundException.class)
    protected ResponseEntity<ErrorResponse> handleBookmarkNotFoundException(final BookmarkNotFoundException exception) {
        log.info("BookmarkNotFoundException: {}", exception.getMessage(), exception);
        return ErrorResponse.toResponseEntity(HttpStatus.NOT_FOUND, exception.getMessage());
    }

    @ExceptionHandler(BookmarkAlreadyExistException.class)
    protected ResponseEntity<ErrorResponse> handleBookmarkAlreadyExistException(final BookmarkAlreadyExistException exception) {
        log.info("BookmarkAlreadyExistException: {}", exception.getMessage(), exception);
        return ErrorResponse.toResponseEntity(HttpStatus.CONFLICT, exception.getMessage());
    }
}
