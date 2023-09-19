package com.gpb.web.exception;

public class EmailAlreadyExistException extends RuntimeException{

    public EmailAlreadyExistException() {
        super("User with this email already exist");
    }
}
