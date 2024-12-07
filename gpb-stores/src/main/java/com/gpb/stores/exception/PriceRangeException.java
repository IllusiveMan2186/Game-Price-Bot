package com.gpb.stores.exception;

public class PriceRangeException extends RuntimeException{

    public PriceRangeException() {
        super("app.game.error.price");
    }
}
