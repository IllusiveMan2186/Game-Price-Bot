package com.gpb.backend.controller;

import com.gpb.backend.exception.EmailAlreadyExistException;
import com.gpb.backend.exception.GameImageNotFoundException;
import com.gpb.backend.exception.LoginFailedException;
import com.gpb.backend.exception.NotFoundException;
import com.gpb.backend.exception.PriceRangeException;
import com.gpb.backend.exception.RestTemplateRequestException;
import com.gpb.backend.exception.UserDataNotChangedException;
import com.gpb.backend.exception.UserLockedException;
import com.gpb.backend.exception.UserNotActivatedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
@Slf4j
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = {EmailAlreadyExistException.class, UserDataNotChangedException.class,
            LoginFailedException.class, PriceRangeException.class, UserNotActivatedException.class})
    protected ResponseEntity<Object> handleBadRequest(RuntimeException ex, WebRequest request) {
        log.error("Bad request exception: {}", ex.getMessage(), ex);
        return handleExceptionInternal(ex, ex.getMessage(), new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(value = {NotFoundException.class, UsernameNotFoundException.class, GameImageNotFoundException.class})
    protected ResponseEntity<Object> handleNotFound(RuntimeException ex, WebRequest request) {
        log.error("Not found exception: {}", ex.getMessage(), ex);
        return handleExceptionInternal(ex, ex.getMessage(), new HttpHeaders(), HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler(value = {UserLockedException.class})
    protected ResponseEntity<Object> handleLockedRequest(RuntimeException ex, WebRequest request) {
        log.error("Locked exception: {}", ex.getMessage(), ex);
        return handleExceptionInternal(ex, ex.getMessage(), new HttpHeaders(), HttpStatus.LOCKED, request);
    }

    @ExceptionHandler(value = {RestTemplateRequestException.class})
    protected ResponseEntity<Object> handleServerError(RuntimeException ex, WebRequest request) {
        log.error("Server Error: {}", ex.getMessage(), ex);
        return handleExceptionInternal(ex, ex.getMessage(), new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }
}
