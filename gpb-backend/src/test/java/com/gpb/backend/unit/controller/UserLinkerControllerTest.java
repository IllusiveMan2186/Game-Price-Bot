package com.gpb.backend.unit.controller;

import com.gpb.backend.bean.user.UserDto;
import com.gpb.backend.controller.UserLinkerController;
import com.gpb.backend.service.UserLinkerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserLinkerControllerTest {

    @Mock
    private UserLinkerService userLinkerService;

    @InjectMocks
    private UserLinkerController userController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testConnectTelegramUser_whenSuccess_shouldInvokeServiceMethod() {
        String token = "telegram-token";
        UserDto user = new UserDto("username", "password", "token", "role", "ua");
        user.setId(123L);


        userController.connectTelegramUser(token, user);


        verify(userLinkerService).linkAccounts(token, 123L);
    }

    @Test
    void testGetTelegramUserConnectorToken_whenSuccess_shouldReturnToken() {
        UserDto user = new UserDto("username", "password", "token", "role", "ua");
        user.setId(123L);
        when(userLinkerService.getAccountsLinkerToken(123L)).thenReturn("connector-token");


        String token = userController.getTelegramUserConnectorToken(user);


        assertEquals("connector-token", token);
        verify(userLinkerService).getAccountsLinkerToken(123L);
    }
}
