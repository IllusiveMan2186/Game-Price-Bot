package com.gpb.backend.exception;

public class NotExistingUserActivationTokenException extends RuntimeException{

    public NotExistingUserActivationTokenException() {
        super("app.user.error.token.not.exist");
    }
}
