package com.yapp.sharefood.user.advice;


import com.yapp.sharefood.common.error.ErrorResponse;
import com.yapp.sharefood.user.exception.UserBanndedException;
import com.yapp.sharefood.user.exception.UserNicknameExistException;
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
public class UserAdviceController {
    @ExceptionHandler(UserNicknameExistException.class)
    protected ResponseEntity<ErrorResponse> handleUserNicknameExistException(final UserNicknameExistException exception) {
        log.error(exception.getMessage(), exception);

        return ErrorResponse.toResponseEntity(HttpStatus.CONFLICT, exception.getMessage());
    }

    @ExceptionHandler(UserBanndedException.class)
    protected ResponseEntity<ErrorResponse> handleUserBanndedException(final UserBanndedException exception) {
        log.error(exception.getMessage(), exception);

        return ErrorResponse.toResponseEntity(HttpStatus.FORBIDDEN, exception.getMessage());
    }
}
