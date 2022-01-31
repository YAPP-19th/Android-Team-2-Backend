package com.yapp.sharefood.auth.advice;

import com.yapp.sharefood.common.error.ErrorResponse;
import com.yapp.sharefood.common.exception.ForbiddenException;
import com.yapp.sharefood.external.exception.BadGatewayException;
import com.yapp.sharefood.oauth.exception.*;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.security.InvalidParameterException;

@Slf4j
@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class AuthAdviceController {

    /**
     * 502 Bad Gateway
     * 외부 API 연동 중 에러가 발생할 경우 발생하는 Exception
     */
    @ExceptionHandler(BadGatewayException.class)
    protected ResponseEntity<ErrorResponse> handleBadGatewayException(final BadGatewayException exception) {
        log.warn("BadGatewayException: {}", exception.getMessage(), exception);
        // add event publisher for let me know in slack or other application

        return ErrorResponse.toResponseEntity(HttpStatus.BAD_GATEWAY, "bad gateway exception");
    }

    /**
     * 404 Not Found
     * 회원가입이 안되어 있는 경우
     */
    @ExceptionHandler(UserNotFoundException.class)
    protected ResponseEntity<ErrorResponse> handleUserNotFoundException(final UserNotFoundException exception) {
        log.info("UserNotFoundException: {}", exception.getMessage(), exception);

        return ErrorResponse.toResponseEntity(HttpStatus.NOT_FOUND, exception.getMessage());
    }

    /**
     * 400 Bad Request
     * OAuth 요청 정보가 적절하지 못한 경우
     */
    @ExceptionHandler(InvalidParameterException.class)
    protected ResponseEntity<ErrorResponse> handleParameterIsNotValidException(final InvalidParameterException exception) {
        log.info("InvalidParameterException: {}", exception.getMessage(), exception);

        return ErrorResponse.toResponseEntity(HttpStatus.BAD_REQUEST, exception.getMessage());
    }

    /**
     * 409 Conflict
     * 이미 동일한 OAUTh로 등록한 회원이 존재하는 경우
     */
    @ExceptionHandler(OAUthExistException.class)
    protected ResponseEntity<ErrorResponse> handleOAuthUserExistException(final OAUthExistException exception) {
        log.info("OAUthExistException: {}", exception.getMessage(), exception);

        return ErrorResponse.toResponseEntity(HttpStatus.CONFLICT, exception.getMessage());
    }

    /**
     * 401 Unauthorized
     * token이 적절하지 않을 경우
     */
    @ExceptionHandler({
            AuthHeaderOmittedException.class,
            TokenValidationException.class,
            TokenExpireExcetion.class,
            ExpiredJwtException.class
    })
    protected ResponseEntity<ErrorResponse> handleUnAuthorizedException(final RuntimeException exception) {
        log.info("UnAuthException: {}", exception.getMessage(), exception);

        return ErrorResponse.toResponseEntity(HttpStatus.UNAUTHORIZED, exception.getMessage());
    }

    /**
     * 403 Forbidden
     * 접근 권한이 없을 경우
     */
    @ExceptionHandler(ForbiddenException.class)
    protected ResponseEntity<ErrorResponse> handleForbiddenException(final RuntimeException execException) {
        log.info("ForbiddenException: {}", execException.getMessage(), execException);

        return ErrorResponse.toResponseEntity(HttpStatus.FORBIDDEN, execException.getMessage());
    }
}
