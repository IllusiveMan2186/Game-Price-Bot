package com.gpb.game.exception;

public class NotExistingMessengerActivationTokenException extends RuntimeException{

    public NotExistingMessengerActivationTokenException() {
        super("app.user.error.messenger.token.not.exist");
    }
}
