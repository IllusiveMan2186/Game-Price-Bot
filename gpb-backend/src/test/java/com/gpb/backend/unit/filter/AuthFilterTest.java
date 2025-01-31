package com.gpb.backend.unit.filter;

import com.gpb.backend.configuration.security.UserAuthenticationProvider;
import com.gpb.backend.filter.AuthFilter;
import com.gpb.backend.util.Constants;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


class AuthFilterTest {

    private UserAuthenticationProvider userAuthenticationProvider;

    private HttpServletRequest request;

    private HttpServletResponse response;

    private FilterChain filterChain;

    private Authentication authentication;

    private AuthFilter authFilter;

    @BeforeEach
    void setUp() {
        userAuthenticationProvider = mock(UserAuthenticationProvider.class);
        authFilter = new AuthFilter(userAuthenticationProvider);

        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        filterChain = mock(FilterChain.class);

        SecurityContextHolder.clearContext();
    }

    @Test
    void testDoFilter_whenValidTokenInHeader_shouldAuthenticate() throws ServletException, IOException {
        String token = "valid-token";
        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer " + token);
        when(userAuthenticationProvider.validateToken(token)).thenReturn(authentication);

        
        authFilter.doFilter(request, response, filterChain);


        assertEquals(authentication, SecurityContextHolder.getContext().getAuthentication());
        verify(userAuthenticationProvider, times(1)).validateToken(token);
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void testDoFilter_whenInvalidTokenInHeader_shouldClearSecurityContext() throws ServletException, IOException {
        String token = "invalid-token";
        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer " + token);
        when(userAuthenticationProvider.validateToken(token)).thenThrow(new RuntimeException("Invalid token"));

        
        authFilter.doFilter(request, response, filterChain);


        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(userAuthenticationProvider, times(1)).validateToken(token);
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void testDoFilter_whenTokenPresentedButInvalidFormat_shouldNotAuthenticate() throws ServletException, IOException {
        String token = "invalid-token";
        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Invalid Bearer " + token);
        when(userAuthenticationProvider.validateToken(token)).thenThrow(new RuntimeException("Invalid token"));


        authFilter.doFilter(request, response, filterChain);


        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(userAuthenticationProvider, times(0)).validateToken(token);
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void testDoFilter_whenNoAuthHeader_shouldNotAuthenticate() throws ServletException, IOException {
        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn(null);

        
        authFilter.doFilter(request, response, filterChain);


        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(userAuthenticationProvider, never()).validateToken(anyString());
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void testDoFilter_whenValidTokenInCookie_shouldAuthenticate() throws ServletException, IOException {
        String token = "valid-cookie-token";
        Cookie authCookie = new Cookie(Constants.TOKEN_COOKIES_ATTRIBUTE, token);
        when(request.getCookies()).thenReturn(new Cookie[]{authCookie});
        when(userAuthenticationProvider.validateToken(token)).thenReturn(authentication);

        
        authFilter.doFilter(request, response, filterChain);


        assertEquals(authentication, SecurityContextHolder.getContext().getAuthentication());
        verify(userAuthenticationProvider, times(1)).validateToken(token);
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void testDoFilter_whenInvalidTokenInCookie_shouldClearSecurityContext() throws ServletException, IOException {
        String token = "invalid-cookie-token";
        Cookie authCookie = new Cookie(Constants.TOKEN_COOKIES_ATTRIBUTE, token);
        when(request.getCookies()).thenReturn(new Cookie[]{authCookie});
        when(userAuthenticationProvider.validateToken(token)).thenThrow(new RuntimeException("Invalid token"));

        
        authFilter.doFilter(request, response, filterChain);


        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(userAuthenticationProvider, times(1)).validateToken(token);
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void testDoFilter_whenNoCookies_shouldNotAuthenticate() throws ServletException, IOException {
        when(request.getCookies()).thenReturn(null);

        
        authFilter.doFilter(request, response, filterChain);


        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(userAuthenticationProvider, never()).validateToken(anyString());
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void testDoFilter_whenCookieIsPresentButEmpty_shouldNotAuthenticate() throws ServletException, IOException {
        Cookie emptyCookie = new Cookie(Constants.TOKEN_COOKIES_ATTRIBUTE, "");
        when(request.getCookies()).thenReturn(new Cookie[]{emptyCookie});

        
        authFilter.doFilter(request, response, filterChain);


        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(userAuthenticationProvider, never()).validateToken(anyString());
        verify(filterChain, times(1)).doFilter(request, response);
    }
}