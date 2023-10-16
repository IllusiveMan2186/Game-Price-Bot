package com.gpb.web.exception;

public class LoginFailedException extends RuntimeException{

    public LoginFailedException() {
        super("Invalid email or password");
    }
}
