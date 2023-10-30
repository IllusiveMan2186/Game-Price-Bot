package com.gpb.web.exception;

public class PriceRangeException extends RuntimeException{

    public PriceRangeException() {
        super("app.game.error.price");
    }
}
