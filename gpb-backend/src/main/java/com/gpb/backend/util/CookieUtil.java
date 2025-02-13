package com.gpb.backend.util;

import com.gpb.backend.exception.RefreshTokenException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Arrays;
import java.util.Optional;

public class CookieUtil {

    private CookieUtil() {
    }

    public static Optional<String> getRefreshToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) throw new RefreshTokenException();
        return Arrays.stream(cookies)
                .filter(cookie -> Constants.REFRESH_TOKEN_COOKIES_ATTRIBUTE.equals(cookie.getName()))
                .map(Cookie::getValue)
                .filter(value -> !value.isEmpty())
                .findFirst();
    }
}
