package com.gpb.stores.exception;

public class GameAlreadyRegisteredException extends RuntimeException {

    public GameAlreadyRegisteredException() {
        super("app.game.error.name.already.exists");
    }
}
