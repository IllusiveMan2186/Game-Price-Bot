package com.gpb.backend.unit.controller;

import com.gpb.backend.bean.user.UserActivation;
import com.gpb.backend.bean.user.UserRegistration;
import com.gpb.backend.bean.user.WebUser;
import com.gpb.backend.configuration.MapperConfig;
import com.gpb.backend.configuration.UserAuthenticationProvider;
import com.gpb.backend.controller.AuthenticationController;
import com.gpb.backend.service.EmailService;
import com.gpb.backend.service.UserActivationService;
import com.gpb.backend.service.UserAuthenticationService;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;

import java.util.Locale;

import static com.gpb.backend.util.Constants.USER_ROLE;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AuthenticationControllerTest {

    UserAuthenticationService service = mock(UserAuthenticationService.class);

    UserAuthenticationProvider provider = mock(UserAuthenticationProvider.class);

    UserActivationService userActivationService = mock(UserActivationService.class);

    EmailService emailService = mock(EmailService.class);

    private final AuthenticationController controller = new AuthenticationController(service, provider, userActivationService, emailService);

    private final WebUser user = new WebUser(0, 1L,"email", "password", false, false,
            0, null, USER_ROLE, new Locale("ua"));

    private final ModelMapper modelMapper = new MapperConfig().modelMapper();

    @Test
    void testCreateUser_whenSuccessful_shouldReturnUser() {
        UserRegistration userRegistration = new UserRegistration("email", "password".toCharArray(), "ua");
        when(service.createUser(userRegistration)).thenReturn(user);
        UserActivation userActivation = new UserActivation();
        when(userActivationService.createUserActivation(user)).thenReturn(userActivation);

        controller.userRegistration(userRegistration);

        verify(emailService).sendEmailVerification(userActivation);
    }
}