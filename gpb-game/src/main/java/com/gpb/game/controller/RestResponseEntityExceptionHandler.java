package com.gpb.game.controller;

import com.gpb.common.exception.NotFoundException;
import com.gpb.common.exception.PriceRangeException;
import com.gpb.game.exception.AccountAlreadyLinkedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * Global exception handler for REST API responses.
 */
@Slf4j
@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    /**
     * Handles exceptions connected to wrong data in request by returning a 400 Bad Request response.
     *
     * @param ex      the exception that was thrown.
     * @param request the current web request.
     * @return a {@link ResponseEntity} containing the error message and a 400 status code.
     */
    @ExceptionHandler(PriceRangeException.class)
    protected ResponseEntity<Object> handleBadRequest(Exception ex, WebRequest request) {
        log.error("Bad request error: {}", ex.getMessage(), ex);
        return handleExceptionInternal(
                ex,
                ex.getMessage(),
                new HttpHeaders(),
                HttpStatus.BAD_REQUEST,
                request
        );
    }

    /**
     * Handles exceptions connected to not founded data by returning a 404 Not Found response.
     *
     * @param ex      the exception that was thrown.
     * @param request the current web request.
     * @return a {@link ResponseEntity} containing the error message and a 404 status code.
     */
    @ExceptionHandler(NotFoundException.class)
    protected ResponseEntity<Object> handleNotFound(Exception ex, WebRequest request) {
        log.error("Resource not found: {}", ex.getMessage(), ex);
        return handleExceptionInternal(
                ex,
                ex.getMessage(),
                new HttpHeaders(),
                HttpStatus.NOT_FOUND,
                request
        );
    }

    /**
     * Handles exceptions connected to conflict data by returning a 409 Conflict response.
     *
     * @param ex      the exception that was thrown.
     * @param request the current web request.
     * @return a {@link ResponseEntity} containing the error message and a 409 status code.
     */
    @ExceptionHandler(AccountAlreadyLinkedException.class)
    protected ResponseEntity<Object> handleConflict(Exception ex, WebRequest request) {
        log.error("Conflict error: {}", ex.getMessage(), ex);
        return handleExceptionInternal(
                ex,
                ex.getMessage(),
                new HttpHeaders(),
                HttpStatus.CONFLICT,
                request
        );
    }
}
