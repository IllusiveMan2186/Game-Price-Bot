package com.gpb.backend.controller;

import com.auth0.jwt.exceptions.TokenExpiredException;
import com.gpb.backend.exception.EmailAlreadyExistException;
import com.gpb.backend.exception.GameImageNotFoundException;
import com.gpb.backend.exception.LoginFailedException;
import com.gpb.backend.exception.RefreshTokenException;
import com.gpb.backend.exception.UserDataNotChangedException;
import com.gpb.backend.exception.UserLockedException;
import com.gpb.backend.exception.UserNotActivatedException;
import com.gpb.backend.exception.WrongPasswordException;
import com.gpb.common.exception.NotFoundException;
import com.gpb.common.exception.PriceRangeException;
import com.gpb.common.exception.RestTemplateRequestException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.net.ConnectException;

/**
 * Global exception handler that intercepts and handles exceptions thrown from REST controllers.
 * <p>
 * Each handler returns a ResponseEntity with an appropriate HTTP status code and error message.
 * </p>
 */
@ControllerAdvice
@Slf4j
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    /**
     * Handles bad request exceptions.
     *
     * @param ex      the exception that occurred
     * @param request the current web request
     * @return a ResponseEntity with status BAD_REQUEST and the exception message
     */
    @ExceptionHandler(value = {EmailAlreadyExistException.class, UserDataNotChangedException.class,
            LoginFailedException.class, PriceRangeException.class, UserNotActivatedException.class,
            WrongPasswordException.class})
    protected ResponseEntity<Object> handleBadRequest(RuntimeException ex, WebRequest request) {
        log.warn("Bad request exception: {}", ex.getMessage(), ex);
        return handleExceptionInternal(ex, ex.getMessage(), new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    /**
     * Handles not found exceptions.
     *
     * @param ex      the exception that occurred
     * @param request the current web request
     * @return a ResponseEntity with status NOT_FOUND and the exception message
     */
    @ExceptionHandler(value = {NotFoundException.class, UsernameNotFoundException.class, GameImageNotFoundException.class})
    protected ResponseEntity<Object> handleNotFound(RuntimeException ex, WebRequest request) {
        log.warn("Not found exception: {}", ex.getMessage(), ex);
        return handleExceptionInternal(ex, ex.getMessage(), new HttpHeaders(), HttpStatus.NOT_FOUND, request);
    }

    /**
     * Handles requests where the user is locked.
     *
     * @param ex      the exception that occurred
     * @param request the current web request
     * @return a ResponseEntity with status LOCKED and the exception message
     */
    @ExceptionHandler(value = {UserLockedException.class})
    protected ResponseEntity<Object> handleLockedRequest(RuntimeException ex, WebRequest request) {
        log.warn("Locked exception: {}", ex.getMessage(), ex);
        return handleExceptionInternal(ex, ex.getMessage(), new HttpHeaders(), HttpStatus.LOCKED, request);
    }

    /**
     * Handles authorization exceptions
     *
     * @param ex      the exception that occurred
     * @param request the current web request
     * @return a ResponseEntity with status INTERNAL_SERVER_ERROR and the exception message
     */
    @ExceptionHandler(value = {RefreshTokenException.class, SecurityException.class, TokenExpiredException.class})
    protected ResponseEntity<Object> handleAuthorizationError(RuntimeException ex, WebRequest request) {
        log.warn("Authorization exception: {}", ex.getMessage(), ex);
        return handleExceptionInternal(ex, ex.getMessage(), new HttpHeaders(), HttpStatus.UNAUTHORIZED, request);
    }

    /**
     * Handles server errors, such as exceptions from external REST calls.
     *
     * @param ex      the exception that occurred
     * @param request the current web request
     * @return a ResponseEntity with status INTERNAL_SERVER_ERROR and the exception message
     */
    @ExceptionHandler(value = {RestTemplateRequestException.class, ConnectException.class})
    protected ResponseEntity<Object> handleServerError(RuntimeException ex, WebRequest request) {
        log.error("Server error: {}", ex.getMessage(), ex);
        return handleExceptionInternal(ex, "", new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    /**
     * Handles all other exceptions by returning a 500 Internal Server Error response.
     *
     * @param ex      the exception that was thrown.
     * @param request the current web request.
     * @return a {@link ResponseEntity} containing the error message and a 500 status code.
     */
    @ExceptionHandler(Exception.class)
    protected ResponseEntity<Object> handleGlobalException(Exception ex, WebRequest request) {
        log.error("Unexpected error: {}", ex.getMessage(), ex);
        return handleExceptionInternal(
                ex,
                "An unexpected error occurred.",
                new HttpHeaders(),
                HttpStatus.INTERNAL_SERVER_ERROR,
                request
        );
    }
}