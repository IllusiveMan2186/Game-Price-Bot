package com.gpb.backend.filter;

import com.gpb.backend.configuration.security.UserAuthenticationProvider;
import com.gpb.backend.util.Constants;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
public class AuthFilter extends OncePerRequestFilter {

    private final UserAuthenticationProvider userAuthenticationProvider;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {
        log.info("Processing request: {}", request.getRequestURL());

        Cookie[] cookies = request.getCookies();

        if (cookies != null && cookies.length > 0) {
            authenticateWithCookies(cookies);
        } else {
            authenticateWithHeader(request);
        }

        filterChain.doFilter(request, response);
    }

    private void authenticateWithHeader(HttpServletRequest request) {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (header != null) {
            String[] authElements = header.split(" ");

            if (authElements.length == 2 && "Bearer".equalsIgnoreCase(authElements[0])) {
                try {
                    log.info("Authenticating user via Authorization header...");
                    SecurityContextHolder.getContext().setAuthentication(
                            userAuthenticationProvider.validateToken(authElements[1])
                    );
                } catch (RuntimeException e) {
                    log.warn("Invalid token in Authorization header: {}", e.getMessage());
                    SecurityContextHolder.clearContext();
                }
            } else {
                log.warn("Invalid Authorization header format");
            }
        }
    }

    private void authenticateWithCookies(Cookie[] cookies) {
        Optional<String> token = Arrays.stream(cookies)
                .filter(cookie -> Constants.TOKEN_COOKIES_ATTRIBUTE.equals(cookie.getName()))
                .map(Cookie::getValue)
                .filter(value -> !value.isEmpty())
                .findFirst();

        token.ifPresentOrElse(
                this::validateTokenFromCookie,
                () -> log.warn("No valid authentication cookie found")
        );
    }

    private void validateTokenFromCookie(String token) {
        try {
            log.info("Check user authenticating via cookie.");
            Authentication authentication = userAuthenticationProvider.validateToken(token);

            SecurityContextHolder.getContext().setAuthentication(authentication);
            log.info("Authentication successful via cookie: {}", authentication.getPrincipal());
        } catch (RuntimeException e) {
            log.warn("Invalid token in cookie: {}", e.getMessage());
            SecurityContextHolder.clearContext();
        }
    }
}
