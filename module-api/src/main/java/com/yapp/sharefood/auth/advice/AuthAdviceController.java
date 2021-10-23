package com.yapp.sharefood.auth.advice;

import com.yapp.sharefood.external.exception.BadGatewayException;
import com.yapp.sharefood.oauth.exception.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.security.InvalidParameterException;

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

    /**
     * 400 Bad Request
     * OAuth 요청 정보가 적절하지 못한 경우
     */
    @ExceptionHandler(InvalidParameterException.class)
    protected ResponseEntity<String> handleParamterIsNotValidException(final InvalidParameterException exception) {
        log.error(exception.getMessage(), exception);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exception.getMessage());
    }

    /**
     * 409 Conflict
     * 이미 동일한 OAUTh로 등록한 회원이 존재하는 경우
     */
    @ExceptionHandler(OAUthExistException.class)
    protected ResponseEntity<String> handleOAuthUserExistException(final OAUthExistException exception) {
        log.error(exception.getMessage(), exception);

        return ResponseEntity.status(HttpStatus.CONFLICT).body(exception.getMessage());
    }

    /**
     * 401 Unauthorized
     * token이 적절하지 않을 경우
     */
    @ExceptionHandler({AuthHeaderOmittedException.class, TokenValidationException.class, TokenExpireExcetion.class})
    protected ResponseEntity<String> handleUnAuthorizedException(final RuntimeException exception) {
        log.error(exception.getMessage(), exception);

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(exception.getMessage());
    }
}
