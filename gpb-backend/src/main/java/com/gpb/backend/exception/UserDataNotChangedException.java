package com.gpb.backend.exception;

public class UserDataNotChangedException extends RuntimeException {

    public UserDataNotChangedException() {
        super("app.user.error.did.not.changed");
    }
}
