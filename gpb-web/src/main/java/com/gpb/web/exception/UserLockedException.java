package com.gpb.web.exception;

public class UserLockedException extends RuntimeException {

    public UserLockedException() {
        super("app.user.error.account.locked");
    }
}
