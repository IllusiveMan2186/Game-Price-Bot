package com.gpb.backend.exception;

public class PriceRangeException extends RuntimeException{

    public PriceRangeException() {
        super("app.game.error.price");
    }
}
