package com.gpb.backend.util;

public class Constants {

    private Constants() {
    }

    public static final String ADMIN_ROLE = "ROLE_ADMIN";
    public static final String USER_ROLE = "ROLE_USER";
    public static final String PNG_IMG_FILE_EXTENSION = ".png";
    public static final String TOKEN_SESSION_ATTRIBUTE = "token";
    public static final int MAX_FAILED_ATTEMPTS = 3;
    public static final long LOCK_TIME_DURATION = 86_400_000; // 24 hours
}
