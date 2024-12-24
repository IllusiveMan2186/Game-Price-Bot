package com.gpb.common.exception;

public class NotExistingLinkerTokenException extends RuntimeException{

    public NotExistingLinkerTokenException() {
        super("app.user.error.messenger.token.not.exist");
    }
}
