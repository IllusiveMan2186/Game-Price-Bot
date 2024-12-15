package com.gpb.backend.unit.controller;

import com.gpb.backend.controller.EmailController;
import com.gpb.backend.service.UserActivationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class EmailControllerTest {

    @Mock
    private UserActivationService userActivationService;

    @InjectMocks
    private EmailController emailController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void userActivation_ValidToken_NoExceptions() {
        // Arrange
        String token = "valid-token";
        doNothing().when(userActivationService).activateUserAccount(token);

        // Act & Assert
        assertDoesNotThrow(() -> emailController.userActivation(token));
        verify(userActivationService, times(1)).activateUserAccount(token);
    }

    @Test
    void userActivation_InvalidToken_ThrowsException() {
        // Arrange
        String token = "invalid-token";
        doThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid token"))
                .when(userActivationService).activateUserAccount(token);

        // Act & Assert
        try {
            emailController.userActivation(token);
        } catch (ResponseStatusException e) {
            // Assert
            verify(userActivationService, times(1)).activateUserAccount(token);
            assert e.getStatusCode() == HttpStatus.BAD_REQUEST;
            assert e.getReason().equals("Invalid token");
        }
    }
}
