package com.gpb.web.exception;

public class LoginFailedException extends RuntimeException{

    public LoginFailedException() {
        super("app.user.error.login.error");
    }
}
