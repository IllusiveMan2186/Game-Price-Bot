package com.gpb.web.exception;

public class UrlAlreadyExistException extends RuntimeException{

    public UrlAlreadyExistException() {
        super("app.game.error.url.already.exists");
    }
}
