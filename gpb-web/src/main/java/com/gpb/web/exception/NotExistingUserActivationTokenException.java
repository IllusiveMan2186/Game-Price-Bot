package com.gpb.web.exception;

public class NotExistingUserActivationTokenException extends RuntimeException{

    public NotExistingUserActivationTokenException() {
        super("app.user.error.token.not.exist");
    }
}
