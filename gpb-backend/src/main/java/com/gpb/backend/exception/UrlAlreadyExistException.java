package com.gpb.backend.exception;

public class UrlAlreadyExistException extends RuntimeException{

    public UrlAlreadyExistException() {
        super("app.game.error.url.already.exists");
    }
}
