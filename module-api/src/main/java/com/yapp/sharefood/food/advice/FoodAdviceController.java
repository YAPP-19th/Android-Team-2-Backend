package com.yapp.sharefood.food.advice;

import com.yapp.sharefood.common.error.ErrorResponse;
import com.yapp.sharefood.food.exception.FoodBanndedException;
import com.yapp.sharefood.food.exception.FoodNotFoundException;
import com.yapp.sharefood.report.exception.ReportNotDefineException;
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
public class FoodAdviceController {

    @ExceptionHandler(FoodNotFoundException.class)
    protected ResponseEntity<ErrorResponse> handlerFoodNotFoundException(final FoodNotFoundException exception) {
        log.info("FoodNotFoundException: {}", exception.getMessage(), exception);

        return ErrorResponse.toResponseEntity(HttpStatus.NOT_FOUND, exception.getMessage());
    }

    @ExceptionHandler(FoodBanndedException.class)
    protected ResponseEntity<ErrorResponse> handleFoodBanndedException(final FoodBanndedException exception) {
        log.info("FoodBanndedException: {}", exception.getMessage(), exception);
        return ErrorResponse.toResponseEntity(HttpStatus.FORBIDDEN, exception.getMessage());
    }

    @ExceptionHandler(ReportNotDefineException.class)
    protected ResponseEntity<ErrorResponse> handleReportNotDefineException(final ReportNotDefineException exception) {
        log.info("ReportNotDefineException: {}", exception.getMessage(), exception);
        return ErrorResponse.toResponseEntity(HttpStatus.BAD_REQUEST, exception.getMessage());
    }
}
