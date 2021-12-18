package com.yapp.sharefood.category.advice;

import com.yapp.sharefood.category.exception.CategoryNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class CategoryAdviceController {
    @ExceptionHandler(CategoryNotFoundException.class)
    protected ResponseEntity<String> handlerCategoryNotFoundException(final CategoryNotFoundException exception) {
        log.info("CategoryNotFoundException: {}", exception.getMessage(), exception);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exception.getMessage());
    }
}
