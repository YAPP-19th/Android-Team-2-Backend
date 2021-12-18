package com.yapp.sharefood.bookmark.advice;

import com.yapp.sharefood.bookmark.exception.BookmarkAlreadyExistException;
import com.yapp.sharefood.bookmark.exception.BookmarkNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class BookmarkAdviceController {

    @ExceptionHandler(BookmarkNotFoundException.class)
    protected ResponseEntity<Object> handleBookmarkNotFoundException(final BookmarkNotFoundException exception) {
        log.info("BookmarkNotFoundException: {}", exception.getMessage(), exception);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exception.getMessage());
    }

    @ExceptionHandler(BookmarkAlreadyExistException.class)
    protected ResponseEntity<Object> handleBookmarkAlreadyExistException(final BookmarkAlreadyExistException exception) {
        log.info("BookmarkAlreadyExistException: {}", exception.getMessage(), exception);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(exception.getMessage());
    }
}
