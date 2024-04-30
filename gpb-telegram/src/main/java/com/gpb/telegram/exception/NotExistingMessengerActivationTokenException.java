package com.gpb.telegram.exception;

public class NotExistingMessengerActivationTokenException extends RuntimeException{

    public NotExistingMessengerActivationTokenException() {
        super("accounts.synchronization.token.not.exist.message");
    }
}
