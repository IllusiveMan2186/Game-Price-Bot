package com.gpb.backend.exception;

public class LoginFailedException extends RuntimeException{

    public LoginFailedException() {
        super("app.user.error.login.error");
    }
}
