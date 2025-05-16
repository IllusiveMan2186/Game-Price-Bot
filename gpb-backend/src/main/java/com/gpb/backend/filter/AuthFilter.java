package com.gpb.backend.filter;

import com.gpb.backend.configuration.security.UserAuthenticationProvider;
import com.gpb.backend.util.Constants;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
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

@Slf4j
@RequiredArgsConstructor
public class AuthFilter extends OncePerRequestFilter {

    private final UserAuthenticationProvider userAuthenticationProvider;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {
        log.debug("Processing request: {}", request.getRequestURL());

        if (isSkippablePath(request)) {
            log.trace("Skip auth filter for request: {}", request.getRequestURL());
            filterChain.doFilter(request, response);
            return;
        }

        String header = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (header != null && header.startsWith(Constants.AUTHORIZATION_HEADER_BEARER)) {
            log.trace("Processing Authorization header");
            processAuthorizationHeader(header);
        } else if (header != null) {
            log.warn("Invalid Authorization header format");
            throw new SecurityException("Invalid Authorization header format");
        }

        filterChain.doFilter(request, response);
    }

    private void processAuthorizationHeader(String header) {
        log.trace("Authenticating user via Authorization header...");
        Authentication authentication = userAuthenticationProvider.validateAuthToken(header);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        log.trace("Authenticating user via Authorization header successful");
    }

    private boolean isSkippablePath(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.equals("/refresh-token") || path.equals("/logout-user");
    }
}
