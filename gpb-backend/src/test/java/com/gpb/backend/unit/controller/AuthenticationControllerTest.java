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
import com.gpb.common.entity.user.TokenRequestDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Locale;

import static com.gpb.backend.util.Constants.USER_ROLE;
import static org.junit.jupiter.api.Assertions.assertEquals;
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
    EmailService emailService;

    @InjectMocks
    AuthenticationController controller;

    private final WebUser user = new WebUser(0, 1L, "email", "password", false, false,
            0, null, USER_ROLE, new Locale("ua"));

    @Test
    void testLogin_whenSuccess_shouldReturnUser() {
        String token = "token";
        Credentials credentials = new Credentials("email", null);
        UserDto userDto = new UserDto(credentials.getEmail(), "", "", "ADMIN", "ua");
        when(service.login(credentials)).thenReturn(userDto);
        when(provider.createToken(user.getEmail())).thenReturn(token);


        UserDto result = controller.login(credentials);


        assertEquals(userDto, result);
        assertEquals(token, result.getToken());
    }

    @Test
    void testUserRegistration_whenSuccess_shouldReturnUser() {
        UserRegistration userRegistration = new UserRegistration("email", "password".toCharArray(), "ua");
        when(service.createUser(userRegistration)).thenReturn(user);
        UserActivation userActivation = new UserActivation();
        when(userActivationService.createUserActivation(user)).thenReturn(userActivation);

        controller.userRegistration(userRegistration);

        verify(emailService).sendEmailVerification(userActivation);
    }

    @Test
    void testUserActivation_whenValidToken_shouldCallActivationUserService() {
        String token = "valid-token";


        controller.userActivation(new TokenRequestDto(token));


        verify(userActivationService, times(1)).activateUserAccount(token);
    }
}