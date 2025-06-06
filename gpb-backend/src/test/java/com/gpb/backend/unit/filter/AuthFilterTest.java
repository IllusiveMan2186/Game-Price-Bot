package com.gpb.backend.unit.filter;

import com.gpb.backend.configuration.security.UserAuthenticationProvider;
import com.gpb.backend.filter.AuthFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthFilterTest {

    @Mock
    private UserAuthenticationProvider userAuthenticationProvider;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;
    @Mock
    private SecurityContext securityContext;


    @InjectMocks
    private AuthFilter authFilter;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.setContext(securityContext);
        when(request.getRequestURI()).thenReturn("/");
    }

    @Test
    void testDoFilter_whenValidTokenInHeader_shouldAuthenticate() throws ServletException, IOException {
        String validToken = "Bearer valid-token";
        Authentication authentication = mock(Authentication.class);

        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn(validToken);
        when(userAuthenticationProvider.validateAuthToken(validToken)).thenReturn(authentication);


        authFilter.doFilter(request, response, filterChain);


        verify(userAuthenticationProvider).validateAuthToken(validToken);
        verify(filterChain).doFilter(request, response);
        verify(securityContext).setAuthentication(authentication);
    }

    @Test
    void testDoFilter_whenSkippablePath_shouldProceedWithoutAuthentication() throws ServletException, IOException {
        when(request.getRequestURI()).thenReturn("/logout-user");


        authFilter.doFilter(request, response, filterChain);


        verify(filterChain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void testDoFilter_whenNoAuthorizationHeader_shouldProceedWithoutAuthentication() throws ServletException, IOException {
        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn(null);


        authFilter.doFilter(request, response, filterChain);


        verify(filterChain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void testDoFilter_whenInvalidAuthorizationHeader_shouldThrowSecurityException() throws ServletException, IOException {
        String invalidToken = "Invalid token";
        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn(invalidToken);


        assertThrows(SecurityException.class, () -> authFilter.doFilter(request, response, filterChain));


        verify(filterChain, never()).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void testDoFilter_whenInvalidToken_shouldClearSecurityContext() throws ServletException, IOException {
        String invalidToken = "Bearer invalid-token";

        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn(invalidToken);
        when(userAuthenticationProvider.validateAuthToken(invalidToken)).thenThrow(new SecurityException("Invalid token"));


        assertThrows(SecurityException.class, () -> authFilter.doFilter(request, response, filterChain));


        verify(userAuthenticationProvider).validateAuthToken(invalidToken);
        verify(filterChain, never()).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }
}
