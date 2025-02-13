package com.gpb.backend.unit.filter;

import com.auth0.jwt.exceptions.TokenExpiredException;
import com.gpb.backend.configuration.security.UserAuthenticationProvider;
import com.gpb.backend.entity.dto.UserDto;
import com.gpb.backend.filter.AuthFilter;
import com.gpb.backend.util.Constants;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
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
    private SecurityContext securityContext ;


    @InjectMocks
    private AuthFilter authFilter;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.setContext(securityContext);
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
    void testDoFilter_whenExpiredAccessToken_shouldRefreshToken() throws ServletException, IOException {
        String expiredToken = "Bearer expired-token";
        String refreshToken = "valid-refresh-token";
        String newAccessToken = "new-access-token";
        UserDto userDto = mock(UserDto.class);
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDto, null, userDto.getAuthorities());
        Cookie cookie = new Cookie(Constants.REFRESH_TOKEN_COOKIES_ATTRIBUTE, refreshToken);

        when(request.getCookies()).thenReturn(new Cookie[]{cookie});
        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn(expiredToken);
        TokenExpiredException expiredException = mock(TokenExpiredException.class);
        when(userAuthenticationProvider.validateAuthToken(expiredToken)).thenThrow(expiredException);
        when(userAuthenticationProvider.validateRefreshToken(refreshToken)).thenReturn(userDto);
        when(userAuthenticationProvider.generateAccessToken(userDto.getId())).thenReturn(newAccessToken);

        authFilter.doFilter(request, response, filterChain);

        verify(response).setHeader(HttpHeaders.AUTHORIZATION, Constants.AUTHORIZATION_HEADER_BEARER + newAccessToken);
        verify(filterChain).doFilter(request, response);
        verify(securityContext).setAuthentication(authentication);
    }

    @Test
    void testDoFilter_whenNoAuthorizationHeader_shouldProceedWithoutAuthentication() throws ServletException, IOException {
        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn(null);

        authFilter.doFilter(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void testDoFilter_whenInvalidAuthorizationHeader_shouldProceedWithoutAuthentication() throws ServletException, IOException {
        String invalidToken = "Invalid token";
        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn(invalidToken);

        authFilter.doFilter(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void testDoFilter_whenInvalidToken_shouldClearSecurityContext() throws ServletException, IOException {
        String invalidToken = "Bearer invalid-token";

        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn(invalidToken);
        when(userAuthenticationProvider.validateAuthToken(invalidToken)).thenThrow(new RuntimeException("Invalid token"));

        authFilter.doFilter(request, response, filterChain);

        verify(userAuthenticationProvider).validateAuthToken(invalidToken);
        verify(filterChain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }
}
