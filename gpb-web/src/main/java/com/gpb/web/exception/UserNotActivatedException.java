package com.gpb.web.exception;

public class UserNotActivatedException  extends RuntimeException {

    public UserNotActivatedException() {
        super("app.user.error.account.not.activated");
    }
}
