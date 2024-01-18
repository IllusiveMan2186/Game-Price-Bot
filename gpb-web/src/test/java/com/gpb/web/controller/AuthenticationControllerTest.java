package com.gpb.web.controller;

import com.gpb.web.bean.user.UserActivation;
import com.gpb.web.bean.user.UserRegistration;
import com.gpb.web.bean.user.WebUser;
import com.gpb.web.configuration.MapperConfig;
import com.gpb.web.configuration.UserAuthenticationProvider;
import com.gpb.web.service.EmailService;
import com.gpb.web.service.UserActivationService;
import com.gpb.web.service.UserService;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;

import static com.gpb.web.util.Constants.USER_ROLE;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AuthenticationControllerTest {

    UserService service = mock(UserService.class);

    UserAuthenticationProvider provider = mock(UserAuthenticationProvider.class);

    UserActivationService userActivationService = mock(UserActivationService.class);

    EmailService emailService = mock(EmailService.class);

    private final AuthenticationController controller = new AuthenticationController(service, provider, userActivationService, emailService);

    private final WebUser user = new WebUser("email", "password", false, false, 0, null, USER_ROLE);

    private final ModelMapper modelMapper = new MapperConfig().modelMapper();

    @Test
    void createUserSuccessfullyShouldReturnUser() {
        UserRegistration userRegistration = new UserRegistration("email", "password".toCharArray(), "ua");
        when(service.createUser(userRegistration)).thenReturn(user);
        UserActivation userActivation = new UserActivation();
        when(userActivationService.createUserActivation(user)).thenReturn(userActivation);

        controller.userRegistration(userRegistration);

        verify(emailService).sendEmailVerification(userActivation);
    }
}