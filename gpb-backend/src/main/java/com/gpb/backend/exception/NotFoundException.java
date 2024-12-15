package com.gpb.backend.exception;

public class NotFoundException extends RuntimeException {

    public NotFoundException(final String message) {
        super(message);
    }

}
