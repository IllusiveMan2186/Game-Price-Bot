package com.gpb.web.exception;

public class InvalidPasswordException extends RuntimeException{

    public InvalidPasswordException() {
        super("Invalid password");
    }
}
