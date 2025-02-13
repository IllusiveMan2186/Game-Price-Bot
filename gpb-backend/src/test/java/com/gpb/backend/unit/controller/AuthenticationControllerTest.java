package com.gpb.backend.unit.controller;

import com.gpb.backend.configuration.security.UserAuthenticationProvider;
import com.gpb.backend.controller.AuthenticationController;
import com.gpb.backend.entity.Credentials;
import com.gpb.backend.entity.RefreshToken;
import com.gpb.backend.entity.UserActivation;
import com.gpb.backend.entity.UserRegistration;
import com.gpb.backend.entity.WebUser;
import com.gpb.backend.entity.dto.UserDto;
import com.gpb.backend.exception.RefreshTokenException;
import com.gpb.backend.service.EmailService;
import com.gpb.backend.service.RefreshTokenService;
import com.gpb.backend.service.UserActivationService;
import com.gpb.backend.service.UserAuthenticationService;
import com.gpb.backend.util.Constants;
import com.gpb.common.entity.user.TokenRequestDto;
import com.gpb.common.service.UserLinkerService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.Locale;
import java.util.Optional;

import static com.gpb.backend.util.Constants.USER_ROLE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthenticationControllerTest {

    @Mock
    UserAuthenticationService service;
    @Mock
    UserAuthenticationProvider provider;
    @Mock
    UserActivationService userActivationService;
    @Mock
    UserLinkerService userLinkerService;
    @Mock
    EmailService emailService;
    @Mock
    RefreshTokenService refreshTokenService;
    @Mock
    ModelMapper modelMapper;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;

    @InjectMocks
    AuthenticationController controller;

    private final WebUser user = new WebUser(1L, 1L, "email", "password", false, false,
            0, null, USER_ROLE, new Locale("ua"));

    @Test
    void testLogin_whenLinkTokenNotPresented_shouldReturnUser() {
        long basicUserId = 123L;
        String token = "token";
        Credentials credentials = new Credentials("email", null, false);
        UserDto userDto = new UserDto(credentials.getEmail(), "", "", "ADMIN", "ua");
        userDto.setBasicUserId(basicUserId);
        HttpServletResponse response = mock(HttpServletResponse.class);
        when(service.login(credentials)).thenReturn(user);
        when(provider.generateAccessToken(user.getId())).thenReturn(token);
        when(modelMapper.map(user, UserDto.class)).thenReturn(userDto);


        UserDto result = controller.login(credentials, Optional.empty(), response);


        assertEquals(userDto, result);
        assertEquals(token, result.getToken());
        verify(userLinkerService, times(0)).linkAccounts(any(String.class), any(Long.class));
    }

    @Test
    void testLogin_whenLinkTokenPresented_shouldReturnUser() {
        String token = "token";
        String linkToken = "linkToken";
        Credentials credentials = new Credentials("email", null, false);
        UserDto userDto = new UserDto(credentials.getEmail(), "", "", "ADMIN", "ua");
        HttpServletResponse response = mock(HttpServletResponse.class);
        when(service.login(credentials)).thenReturn(user);
        when(provider.generateAccessToken(user.getId())).thenReturn(token);
        when(modelMapper.map(user, UserDto.class)).thenReturn(userDto);


        UserDto result = controller.login(credentials, Optional.of(linkToken), response);


        assertEquals(userDto, result);
        assertEquals(token, result.getToken());
        verify(userLinkerService).linkAccounts(linkToken, user.getBasicUserId());
    }


    @Test
    void testUserRegistration_whenLinkTokenNotPresented_shouldReturnUser() {
        UserRegistration userRegistration = new UserRegistration("email", "password".toCharArray(), "ua");
        when(service.createUser(userRegistration)).thenReturn(user);
        UserActivation userActivation = new UserActivation();
        when(userActivationService.createUserActivation(user)).thenReturn(userActivation);


        controller.userRegistration(userRegistration, null);


        verify(emailService).sendEmailVerification(userActivation);
        verify(userLinkerService, times(0)).linkAccounts(any(String.class), any(Long.class));
    }

    @Test
    void testUserRegistration_whenLinkTokenPresented_shouldReturnUser() {
        String linkToken = "linkToken";
        UserRegistration userRegistration = new UserRegistration("email", "password".toCharArray(), "ua");
        when(service.createUser(userRegistration)).thenReturn(user);
        UserActivation userActivation = new UserActivation();
        when(userActivationService.createUserActivation(user)).thenReturn(userActivation);


        controller.userRegistration(userRegistration, linkToken);


        verify(emailService).sendEmailVerification(userActivation);
        verify(userLinkerService).linkAccounts(linkToken, user.getBasicUserId());
    }

    @Test
    void testUserActivation_whenValidToken_shouldCallActivationUserService() {
        String token = "valid-token";


        controller.userActivation(new TokenRequestDto(token));


        verify(userActivationService, times(1)).activateUserAccount(token);
    }

    @Test
    void testRefreshToken_whenSuccess_shouldReturnToken() {
        long basicUserId = 123L;
        String token = "token";
        Credentials credentials = new Credentials("email", null, false);
        UserDto userDto = new UserDto(credentials.getEmail(), "", "", "ADMIN", "ua");
        userDto.setBasicUserId(basicUserId);
        HttpServletResponse response = mock(HttpServletResponse.class);
        when(service.login(credentials)).thenReturn(user);
        when(provider.generateAccessToken(user.getId())).thenReturn(token);
        when(modelMapper.map(user, UserDto.class)).thenReturn(userDto);


        UserDto result = controller.login(credentials, Optional.empty(), response);


        assertEquals(userDto, result);
        assertEquals(token, result.getToken());
        verify(userLinkerService, times(0)).linkAccounts(any(String.class), any(Long.class));
    }

    @Test
    void refreshToken_Success() {
        RefreshToken mockRefreshToken;
        mockRefreshToken = new RefreshToken();
        mockRefreshToken.setUser(user);
        mockRefreshToken.setToken("valid-refresh-token");
        Cookie cookie = new Cookie(Constants.REFRESH_TOKEN_COOKIES_ATTRIBUTE, "valid-refresh-token");
        when(request.getCookies()).thenReturn(new Cookie[]{cookie});
        when(refreshTokenService.getByToken("valid-refresh-token")).thenReturn(Optional.of(mockRefreshToken));
        when(provider.generateRefreshToken(user)).thenReturn("new-refresh-token");
        when(provider.generateAccessToken(user.getId())).thenReturn("new-access-token");

        String result = controller.refreshToken(request, response);

        assertEquals("new-access-token", result);
        verify(response, times(1)).addCookie(any(Cookie.class));
    }

    @Test
    void testRefreshToken_whenNoCookies_shouldThrowsException() {
        when(request.getCookies()).thenReturn(null);

        assertThrows(RefreshTokenException.class, () -> controller.refreshToken(request, response));
    }

    @Test
    void testRefreshToken_whenNoTokenInCookie_shouldThrowsException() {
        when(request.getCookies()).thenReturn(new Cookie[]{});

        assertThrows(RefreshTokenException.class, () -> controller.refreshToken(request, response));
    }

    @Test
    void refreshToken_InvalidToken_ThrowsException() {
        Cookie cookie = new Cookie(Constants.REFRESH_TOKEN_COOKIES_ATTRIBUTE, "invalid-token");
        when(request.getCookies()).thenReturn(new Cookie[]{cookie});
        when(refreshTokenService.getByToken("invalid-token")).thenReturn(Optional.empty());

        assertThrows(RefreshTokenException.class, () -> controller.refreshToken(request, response));
    }

    @Test
    void logout_shouldSetExpiredTokenCookie() {
        HttpServletResponse response = mock(HttpServletResponse.class);
        Cookie cookie = new Cookie(Constants.REFRESH_TOKEN_COOKIES_ATTRIBUTE, null);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);


        controller.logout(response);


        verify(response, times(1)).addCookie(cookie);
    }
}