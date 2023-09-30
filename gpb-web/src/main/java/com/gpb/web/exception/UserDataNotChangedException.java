package com.gpb.web.exception;

public class UserDataNotChangedException extends RuntimeException {

    public UserDataNotChangedException() {
        super("User didn't changed during update operation");
    }
}
