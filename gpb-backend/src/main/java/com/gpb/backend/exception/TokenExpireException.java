package com.gpb.backend.exception;

public class TokenExpireException extends RuntimeException {

    public TokenExpireException() {
        super("app.email.change.token.expire");
    }
}
