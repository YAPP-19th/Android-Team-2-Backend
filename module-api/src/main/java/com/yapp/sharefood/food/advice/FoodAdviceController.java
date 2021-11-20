package com.yapp.sharefood.food.advice;

import com.yapp.sharefood.food.exception.FoodNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class FoodAdviceController {
    @ExceptionHandler(FoodNotFoundException.class)
    protected ResponseEntity<String> handlerFoodNotFoundException(final FoodNotFoundException exception) {
        log.error(exception.getMessage(), exception);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exception.getMessage());
    }
}
