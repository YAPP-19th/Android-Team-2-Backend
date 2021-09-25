package com.yapp.sharefood.auth.advice;

import com.yapp.sharefood.external.exception.BadGatewayException;
import com.yapp.sharefood.oauth.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class AuthAdviceController {

    /**
     * 502 Bad Gateway
     * 외부 API 연동 중 에러가 발생할 경우 발생하는 Exception
     */
    @ExceptionHandler(BadGatewayException.class)
    protected ResponseEntity<Object> handleBadGatewayException(final BadGatewayException exception) {
        log.error(exception.getMessage(), exception);
        // add event publisher for let me know in slack or other application
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(exception.getMessage());
    }

    /**
     * 404 Not Found
     * 회원가입이 안되어 있는 경우
     */
    @ExceptionHandler(UserNotFoundException.class)
    protected ResponseEntity<Object> handleUserNotFoundException(final UserNotFoundException exception) {
        log.error(exception.getMessage(), exception);

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exception.getMessage());
    }
}
