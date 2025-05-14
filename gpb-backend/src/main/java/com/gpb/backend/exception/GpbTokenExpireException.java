package com.gpb.backend.exception;

public class GpbTokenExpireException extends RuntimeException {

    public GpbTokenExpireException() {
        super("app.email.change.token.expire");
    }
}
