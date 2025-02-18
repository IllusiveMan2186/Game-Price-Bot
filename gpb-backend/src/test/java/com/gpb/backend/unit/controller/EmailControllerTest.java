package com.gpb.backend.unit.controller;

import com.gpb.backend.controller.EmailController;
import com.gpb.backend.entity.EmailChanging;
import com.gpb.backend.entity.WebUser;
import com.gpb.backend.entity.dto.EmailRequestDto;
import com.gpb.backend.entity.dto.UserDto;
import com.gpb.backend.exception.EmailAlreadyExistException;
import com.gpb.backend.exception.UserDataNotChangedException;
import com.gpb.backend.service.EmailChangingService;
import com.gpb.backend.service.EmailService;
import com.gpb.backend.service.UserActivationService;
import com.gpb.backend.service.UserManagementService;
import com.gpb.common.entity.user.TokenRequestDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EmailControllerTest {

    @Mock
    private UserManagementService userManagementService;
    @Mock
    private UserActivationService userActivationService;
    @Mock
    private EmailChangingService emailChangingService;
    @Mock
    private EmailService emailService;

    @InjectMocks
    EmailController emailController;


    @Test
    void testUpdateUserEmail_whenSuccess_shouldSendEmail() {
        String newEmail = "newemail@example.com";
        UserDto user = new UserDto("username", "password", "token", "role", "ua");
        user.setId(123L);

        WebUser webUser = new WebUser();
        EmailChanging emailChanging = new EmailChanging();
        when(userManagementService.getWebUserByEmail(newEmail)).thenReturn(Optional.empty());
        when(userManagementService.getWebUserById(user.getId())).thenReturn(webUser);
        when(emailChangingService.createEmailChanging(newEmail, webUser)).thenReturn(emailChanging);


        emailController.updateUserEmail(new EmailRequestDto(newEmail), user);


        verify(emailChangingService, times(1)).createEmailChanging(eq(newEmail), eq(webUser));
        verify(emailService, times(1)).sendEmailChange(eq(emailChanging));
    }

    @Test
    void testUpdateUserEmail_whenEmailNotChanged_shouldThrowException() {
        String newEmail = "newemail@example.com";
        UserDto user = new UserDto("newemail@example.com", "password", "token", "role", "ua");


        assertThrows(UserDataNotChangedException.class,
                () -> emailController.updateUserEmail(new EmailRequestDto(newEmail), user));


        verify(emailChangingService, never()).createEmailChanging(any(String.class), any(WebUser.class));
    }

    @Test
    void testUpdateUserEmail_whenEmailAlreadyRegisteredInSystem_shouldThrowException() {
        String newEmail = "newemail@example.com";
        UserDto user = new UserDto("username", "password", "token", "role", "ua");

        when(userManagementService.getWebUserByEmail(newEmail)).thenReturn(Optional.of(new WebUser()));


        assertThrows(EmailAlreadyExistException.class,
                () -> emailController.updateUserEmail(new EmailRequestDto(newEmail), user));


        verify(emailChangingService, never()).createEmailChanging(any(String.class), any(WebUser.class));
    }

    @Test
    void testResendUserActivationEmail_whenSuccess_shouldInvokeServiceMethod() {
        String email = "user@example.com";


        emailController.resendUserActivationEmail(new EmailRequestDto(email));


        verify(userActivationService).resendActivationEmail(email);
    }

    @Test
    void testEmailConformation_whenSuccess_shouldConfirmNewEmail() {
        String token = "token";
        String response = "response";
        when(emailChangingService.confirmEmailChangingToken(token)).thenReturn(response);


        String result = emailController.emailConformation(new TokenRequestDto(token));


        assertEquals(response, result);
        verify(emailChangingService).confirmEmailChangingToken(token);
    }
}
