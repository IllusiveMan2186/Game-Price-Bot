package com.gpb.backend.exception;

public class EmailAlreadyExistException extends RuntimeException{

    public EmailAlreadyExistException() {
        super("app.user.error.email.already.exists");
    }
}
