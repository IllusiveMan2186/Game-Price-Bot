package com.gpb.game.filter;

import com.gpb.common.util.CommonConstants;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filter that validates the API key provided in the HTTP request header.
 * <p>
 * This filter checks if the API key present in the header (as defined by
 * {@link CommonConstants#API_KEY_HEADER}) matches the valid API key configured in the application properties.
 * If the API key is missing or invalid, the filter responds with an HTTP 401 (Unauthorized) status.
 * </p>
 */
@Slf4j
@Component
public class ApiKeyFilter extends OncePerRequestFilter {

    @Value("${API_KEY}")
    private String validApiKey;

    /**
     * Checks the incoming request for a valid API key.
     * <p>
     * If the API key is missing or does not match the expected value, this filter will set the response status
     * to 401 (Unauthorized) and write an error message. Otherwise, the request is forwarded to the next filter
     * in the chain.
     * </p>
     *
     * @param request     the incoming {@link HttpServletRequest}
     * @param response    the outgoing {@link HttpServletResponse}
     * @param filterChain the {@link FilterChain} to pass control to the next filter
     * @throws ServletException if an error occurs during filtering
     * @throws IOException      if an I/O error occurs during filtering
     */
    @Override
    protected void doFilterInternal(final HttpServletRequest request,
                                    final HttpServletResponse response,
                                    final FilterChain filterChain)
            throws ServletException, IOException {
        final String apiKey = request.getHeader(CommonConstants.API_KEY_HEADER);

        if (apiKey == null || !validApiKey.equals(apiKey)) {
            log.warn("Unauthorized access attempt with API key: {}", apiKey);
            response.setContentType("text/plain;charset=UTF-8");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Invalid API Key");
            return;
        }

        filterChain.doFilter(request, response);
    }
}
