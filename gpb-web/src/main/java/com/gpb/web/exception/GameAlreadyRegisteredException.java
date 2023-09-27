package com.gpb.web.exception;

public class GameAlreadyRegisteredException extends RuntimeException {

    public GameAlreadyRegisteredException() {
        super("Game with this name already exist");
    }
}
