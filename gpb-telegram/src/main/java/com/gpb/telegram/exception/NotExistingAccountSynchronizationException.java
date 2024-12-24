package com.gpb.telegram.exception;

public class NotExistingAccountSynchronizationException extends RuntimeException{

    public NotExistingAccountSynchronizationException() {
        super("accounts.synchronization.token.not.exist.message");
    }
}
