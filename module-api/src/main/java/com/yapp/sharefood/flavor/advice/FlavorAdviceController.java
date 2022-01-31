package com.yapp.sharefood.flavor.advice;

import com.yapp.sharefood.common.error.ErrorResponse;
import com.yapp.sharefood.flavor.exception.FlavorNotFoundException;
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
public class FlavorAdviceController {

    @ExceptionHandler(FlavorNotFoundException.class)
    protected ResponseEntity<ErrorResponse> handleFlavorNotFoundException(final FlavorNotFoundException exception) {
        log.info("FlavorNotFoundException: {}", exception.getMessage(), exception);
        return ErrorResponse.toResponseEntity(HttpStatus.NOT_FOUND, exception.getMessage());
    }
}
