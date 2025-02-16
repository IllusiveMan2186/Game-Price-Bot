package com.gpb.backend.exception;

public class WrongPasswordException extends RuntimeException {

    public WrongPasswordException() {
        super("app.user.error.wrong.password");
    }
}