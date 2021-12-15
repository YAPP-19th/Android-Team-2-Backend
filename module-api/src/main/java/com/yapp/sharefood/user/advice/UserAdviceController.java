package com.yapp.sharefood.user.advice;


import com.yapp.sharefood.user.exception.UserBanndedException;
import com.yapp.sharefood.user.exception.UserNicknameExistException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class UserAdviceController {
    @ExceptionHandler(UserNicknameExistException.class)
    protected ResponseEntity<Object> handleUserNicknameExistException(final UserNicknameExistException exception) {
        log.error(exception.getMessage(), exception);

        return ResponseEntity.status(HttpStatus.CONFLICT).body(exception.getMessage());
    }

    @ExceptionHandler(UserBanndedException.class)
    protected ResponseEntity<Object> handleUserBanndedException(final UserBanndedException exception) {
        log.error(exception.getMessage(), exception);

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(exception.getMessage());
    }
}
