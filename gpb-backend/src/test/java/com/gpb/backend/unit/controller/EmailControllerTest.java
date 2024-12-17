package com.gpb.backend.unit.controller;

import com.gpb.backend.controller.EmailController;
import com.gpb.backend.service.UserActivationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class EmailControllerTest {

    @Mock
    private UserActivationService userActivationService;

    @InjectMocks
    private EmailController emailController;

    @Test
    void testUserActivation_whenValidToken_shouldNotThrowExceptions() {
        String token = "valid-token";
        doNothing().when(userActivationService).activateUserAccount(token);


        assertDoesNotThrow(() -> emailController.userActivation(token));
        verify(userActivationService, times(1)).activateUserAccount(token);
    }

    @Test
    void testUserActivation_whenInvalidToken_shouldThrowException() {
        String token = "invalid-token";
        doThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid token"))
                .when(userActivationService).activateUserAccount(token);


        try {
            emailController.userActivation(token);
        } catch (ResponseStatusException e) {
            verify(userActivationService, times(1)).activateUserAccount(token);
            assert e.getStatusCode() == HttpStatus.BAD_REQUEST;
            assert e.getReason().equals("Invalid token");
        }
    }
}
