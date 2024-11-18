package com.gpb.web.unit.service.impl;

import com.gpb.web.bean.user.UserActivation;
import com.gpb.web.bean.user.WebUser;
import com.gpb.web.exception.NotExistingUserActivationTokenException;
import com.gpb.web.repository.UserActivationRepository;
import com.gpb.web.service.EmailService;
import com.gpb.web.service.UserActivationService;
import com.gpb.web.service.UserService;
import com.gpb.web.service.impl.UserActivationServiceImpl;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserActivationServiceImplTest {

    UserActivationRepository userActivationRepository = mock(UserActivationRepository.class);

    UserService userService = mock(UserService.class);

    EmailService emailService = mock(EmailService.class);

    UserActivationService userActivationService = new UserActivationServiceImpl(userActivationRepository, userService, emailService);

    @Test
    void createUserActivationSuccessfullyShouldReturnUserActivationAccount() {
        WebUser user = WebUser.builder().id(1).build();
        UserActivation userActivation = UserActivation.builder()
                .user(user)
                .build();
        when(userActivationRepository.save(userActivation)).thenReturn(userActivation);

        UserActivation result = userActivationService.createUserActivation(user);

        assertEquals(userActivation, result);
    }

    @Test
    void activateUserAccountSuccessfullyShouldActivateUser() {
        WebUser user = new WebUser();
        String token = "token";
        UserActivation userActivation = UserActivation.builder()
                .user(user)
                .build();
        when(userActivationRepository.findByToken(token)).thenReturn(userActivation);

        userActivationService.activateUserAccount(token);

        verify(userService).activateUser(user.getId());
    }

    @Test
    void activateUserAccountThatNotExistSuccessfullyShouldThrowException() {
        String token = "token";
        when(userActivationRepository.findByToken(token)).thenReturn(null);

        assertThrows(NotExistingUserActivationTokenException.class, () -> userActivationService.activateUserAccount(token),
                "app.user.error.token.not.exist");
    }

    @Test
    void resendActivationEmailSuccessfullyShouldSendEmail() {
        String email = "email";
        WebUser user = new WebUser();
        UserActivation userActivation = UserActivation.builder()
                .user(user)
                .build();
        when(userService.getWebUserByEmail(email)).thenReturn(user);
        when(userActivationRepository.findByUser(user)).thenReturn(userActivation);

        userActivationService.resendActivationEmail(email);

        verify(emailService).sendEmailVerification(userActivation);
    }
}