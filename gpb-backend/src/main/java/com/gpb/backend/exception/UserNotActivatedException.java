package com.gpb.backend.exception;

public class UserNotActivatedException  extends RuntimeException {

    public UserNotActivatedException() {
        super("app.user.error.account.not.activated");
    }
}
