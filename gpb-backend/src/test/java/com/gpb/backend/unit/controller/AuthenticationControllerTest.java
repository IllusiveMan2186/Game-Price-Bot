package com.gpb.backend.unit.controller;

import com.gpb.backend.configuration.security.UserAuthenticationProvider;
import com.gpb.backend.controller.AuthenticationController;
import com.gpb.backend.entity.Credentials;
import com.gpb.backend.entity.UserActivation;
import com.gpb.backend.entity.UserRegistration;
import com.gpb.backend.entity.WebUser;
import com.gpb.backend.entity.dto.UserDto;
import com.gpb.backend.service.EmailService;
import com.gpb.backend.service.UserActivationService;
import com.gpb.backend.service.UserAuthenticationService;
import com.gpb.backend.util.Constants;
import com.gpb.common.entity.user.TokenRequestDto;
import com.gpb.common.service.UserLinkerService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Locale;

import static com.gpb.backend.util.Constants.USER_ROLE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
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

    @InjectMocks
    AuthenticationController controller;

    private final WebUser user = new WebUser(0, 1L, "email", "password", false, false,
            0, null, USER_ROLE, new Locale("ua"));

    @Test
    void testLogin_whenCookiesEnabled_shouldReturnUserAndSetCookies() {
        long basicUserId = 123L;
        String token = "token";
        Credentials credentials = new Credentials("email", null, true);
        UserDto userDto = new UserDto(credentials.getEmail(), "", "", "ADMIN", "ua");
        userDto.setBasicUserId(basicUserId);
        HttpServletResponse response = mock(HttpServletResponse.class);
        when(service.login(credentials)).thenReturn(userDto);
        when(provider.createToken(user.getEmail())).thenReturn(token);

        UserDto result = controller.login(credentials, null, response);


        assertEquals(userDto, result);
        assertNull(result.getToken());

        Cookie cookie = new Cookie(Constants.TOKEN_COOKIES_ATTRIBUTE, token);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(Constants.TOKEN_EXPIRATION);

        verify(response, times(1)).addCookie(cookie);
    }

    @Test
    void testLogin_whenLinkTokenNotPresented_shouldReturnUser() {
        long basicUserId = 123L;
        String token = "token";
        Credentials credentials = new Credentials("email", null, false);
        UserDto userDto = new UserDto(credentials.getEmail(), "", "", "ADMIN", "ua");
        userDto.setBasicUserId(basicUserId);
        HttpServletResponse response = mock(HttpServletResponse.class);
        when(service.login(credentials)).thenReturn(userDto);
        when(provider.createToken(user.getEmail())).thenReturn(token);


        UserDto result = controller.login(credentials, null, response);


        assertEquals(userDto, result);
        assertEquals(token, result.getToken());
        verify(userLinkerService, times(0)).linkAccounts(any(String.class), any(Long.class));
    }

    @Test
    void testLogin_whenLinkTokenPresented_shouldReturnUser() {
        long basicUserId = 123L;
        String token = "token";
        String linkToken = "linkToken";
        Credentials credentials = new Credentials("email", null, false);
        UserDto userDto = new UserDto(credentials.getEmail(), "", "", "ADMIN", "ua");
        userDto.setBasicUserId(basicUserId);
        HttpServletResponse response = mock(HttpServletResponse.class);
        when(service.login(credentials)).thenReturn(userDto);
        when(provider.createToken(user.getEmail())).thenReturn(token);


        UserDto result = controller.login(credentials, linkToken, response);


        assertEquals(userDto, result);
        assertEquals(token, result.getToken());
        verify(userLinkerService).linkAccounts(linkToken, basicUserId);
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
    void checkAuth_whenUserAuthenticated_shouldReturnOk() {
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);


        ResponseEntity<?> responseEntity = controller.checkAuth();


        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    void checkAuth_whenUserNotAuthenticated_shouldReturnUnauthorized() {
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(null);
        SecurityContextHolder.setContext(securityContext);


        ResponseEntity<?> responseEntity = controller.checkAuth();


        assertEquals(HttpStatus.UNAUTHORIZED, responseEntity.getStatusCode());
    }

    @Test
    void logout_shouldSetExpiredTokenCookie() {
        HttpServletResponse response = mock(HttpServletResponse.class);
        Cookie cookie = new Cookie(Constants.TOKEN_COOKIES_ATTRIBUTE, null);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);


        controller.logout(response);


        verify(response, times(1)).addCookie(cookie);
    }
}