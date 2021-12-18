package com.yapp.sharefood.flavor.advice;

import com.yapp.sharefood.flavor.exception.FlavorNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class FlavorAdviceController {

    @ExceptionHandler(FlavorNotFoundException.class)
    protected ResponseEntity<Object> handleFlavorNotFoundException(final FlavorNotFoundException exception) {
        log.info("FlavorNotFoundException: {}", exception.getMessage(), exception);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exception.getMessage());
    }
}
