package com.gpb.backend.util;

public class Constants {

    private Constants() {
    }

    public static final String LINK_TOKEN_HEADER = "LinkToken";
    public static final String ADMIN_ROLE = "ROLE_ADMIN";
    public static final String USER_ROLE = "ROLE_USER";
    public static final String PNG_IMG_FILE_EXTENSION = ".png";
    public static final String REFRESH_TOKEN_COOKIES_ATTRIBUTE = "REFRESH_TOKEN";
    public static final String AUTHORIZATION_HEADER_BEARER = "Bearer ";
    public static final long TOKEN_EXPIRATION = 15 * 60 * 1000; // 15 minutes
    public static final int REFRESH_TOKEN_EXPIRATION = 10 * 24 * 60 * 60 * 1000; // 10 days
    public static final int MAX_FAILED_ATTEMPTS = 3;
    public static final long LOCK_TIME_DURATION = 86_400_000; // 24 hours
}
