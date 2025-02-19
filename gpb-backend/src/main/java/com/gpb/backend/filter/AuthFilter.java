package com.gpb.backend.filter;

import com.auth0.jwt.exceptions.TokenExpiredException;
import com.gpb.backend.configuration.security.UserAuthenticationProvider;
import com.gpb.backend.entity.dto.UserDto;
import com.gpb.backend.util.Constants;
import com.gpb.backend.util.CookieUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
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
        log.debug("Processing request: {}", request.getRequestURL());

        String header = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (header != null && header.startsWith(Constants.AUTHORIZATION_HEADER_BEARER)) {
            processAuthorizationHeader(header);
        } else if (header != null) {
            log.warn("Invalid Authorization header format");
        }

        filterChain.doFilter(request, response);
    }

    private void processAuthorizationHeader(String header) {
        try {
            log.debug("Authenticating user via Authorization header...");
            Authentication authentication = userAuthenticationProvider.validateAuthToken(header);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (Exception e) {
            log.warn("Invalid token in Authorization header: {}", e.getMessage());
            SecurityContextHolder.clearContext();
        }
    }
}
