package com.gpb.web.exception;

public class UrlAlreadyExistException extends RuntimeException{

    public UrlAlreadyExistException() {
        super("Game with this url already exist");
    }
}
