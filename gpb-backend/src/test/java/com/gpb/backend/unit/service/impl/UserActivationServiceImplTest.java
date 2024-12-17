package com.gpb.backend.unit.service.impl;

import com.gpb.backend.bean.user.UserActivation;
import com.gpb.backend.bean.user.WebUser;
import com.gpb.backend.exception.NotExistingUserActivationTokenException;
import com.gpb.backend.repository.UserActivationRepository;
import com.gpb.backend.service.EmailService;
import com.gpb.backend.service.UserActivationService;
import com.gpb.backend.service.UserManagementService;
import com.gpb.backend.service.impl.UserActivationServiceImpl;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserActivationServiceImplTest {

    UserActivationRepository userActivationRepository = mock(UserActivationRepository.class);

    UserManagementService userService = mock(UserManagementService.class);

    EmailService emailService = mock(EmailService.class);

    UserActivationService userActivationService = new UserActivationServiceImpl(userActivationRepository, userService, emailService);

    @Test
    void testCreateUserActivation_whenSuccess_shouldReturnUserActivationAccount() {
        WebUser user = WebUser.builder().id(1).build();
        UserActivation userActivation = UserActivation.builder()
                .user(user)
                .build();
        when(userActivationRepository.save(userActivation)).thenReturn(userActivation);

        UserActivation result = userActivationService.createUserActivation(user);

        assertEquals(userActivation, result);
    }

    @Test
    void testActivateUserAccount_whenSuccess_shouldActivateUser() {
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
    void testActivateUserAccount_whenUserDoesNotExist_shouldThrowException() {
        String token = "token";
        when(userActivationRepository.findByToken(token)).thenReturn(null);

        assertThrows(NotExistingUserActivationTokenException.class, () -> userActivationService.activateUserAccount(token),
                "app.user.error.token.not.exist");
    }

    @Test
    void testResendActivationEmail_whenSuccessful_shouldSendEmail() {
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