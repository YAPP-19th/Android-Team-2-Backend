package com.yapp.sharefood.category.advice;

import com.yapp.sharefood.category.exception.CategoryNotFoundException;
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
public class CategoryAdviceController {
    @ExceptionHandler(CategoryNotFoundException.class)
    protected ResponseEntity<ErrorResponse> handlerCategoryNotFoundException(final CategoryNotFoundException exception) {
        log.info("CategoryNotFoundException: {}", exception.getMessage(), exception);
        return ErrorResponse.toResponseEntity(HttpStatus.NOT_FOUND, exception.getMessage());
    }
}
