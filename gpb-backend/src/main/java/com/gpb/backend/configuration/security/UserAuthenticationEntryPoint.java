package com.gpb.backend.configuration.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Entry point for handling authentication errors. This component is invoked when an unauthenticated user
 * attempts to access a secured resource. It returns a JSON-formatted error response with a 401 status code.
 */
@Slf4j
@Component
public class UserAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    /**
     * Commences an authentication scheme.
     * <p>
     * This method is triggered when an unauthenticated user requests a secured HTTP resource and an
     * {@link AuthenticationException} is thrown. It sets the response status to 401 (Unauthorized) and writes
     * a JSON error response to the output stream.
     * </p>
     *
     * @param request       the {@link HttpServletRequest} that resulted in an {@link AuthenticationException}
     * @param response      the {@link HttpServletResponse} to send the error response to
     * @param authException the exception that caused the invocation
     * @throws IOException in the event of an input/output error
     */
    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        log.error("Unauthorized access attempt: {}", authException.getMessage());

        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("status", HttpStatus.UNAUTHORIZED.value());
        errorResponse.put("error", "Unauthorized");
        errorResponse.put("message", "Access to the requested resource is denied");
        errorResponse.put("path", request.getRequestURI());

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

        OBJECT_MAPPER.writeValue(response.getOutputStream(), errorResponse);
    }
}