package com.yapp.sharefood.food.advice;

import com.yapp.sharefood.food.exception.FoodBanndedException;
import com.yapp.sharefood.food.exception.FoodNotFoundException;
import com.yapp.sharefood.report.exception.ReportNotDefineException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class FoodAdviceController {
    @ExceptionHandler(FoodNotFoundException.class)
    protected ResponseEntity<Object> handlerFoodNotFoundException(final FoodNotFoundException exception) {
        log.error(exception.getMessage(), exception);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exception.getMessage());
    }

    @ExceptionHandler(FoodBanndedException.class)
    protected ResponseEntity<Object> handleFoodBanndedException(final FoodBanndedException exception) {
        log.error(exception.getMessage(), exception);
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(exception.getMessage());
    }

    @ExceptionHandler(ReportNotDefineException.class)
    protected ResponseEntity<Object> handleReportNotDefineException(final ReportNotDefineException exception) {
        log.error(exception.getMessage(), exception);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exception.getMessage());
    }
}
