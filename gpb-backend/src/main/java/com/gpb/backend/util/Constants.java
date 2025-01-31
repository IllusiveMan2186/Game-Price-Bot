package com.gpb.backend.util;

public class Constants {

    private Constants() {
    }

    public static final String LINK_TOKEN_HEADER = "LinkToken";
    public static final String ADMIN_ROLE = "ROLE_ADMIN";
    public static final String USER_ROLE = "ROLE_USER";
    public static final String PNG_IMG_FILE_EXTENSION = ".png";
    public static final String TOKEN_COOKIES_ATTRIBUTE = "AUTH_TOKEN";
    public static final int TOKEN_EXPIRATION = 3600;
    public static final int MAX_FAILED_ATTEMPTS = 3;
    public static final long LOCK_TIME_DURATION = 86_400_000; // 24 hours
}
