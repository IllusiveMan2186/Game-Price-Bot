package com.gpb.web.exception;

public class NotExistingTokenException extends RuntimeException{

    public NotExistingTokenException() {
        super("app.user.error.token.not.exist");
    }
}
