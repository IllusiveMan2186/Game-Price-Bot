package com.gpb.backend.unit.controller;

import com.gpb.backend.controller.UserLinkerController;
import com.gpb.backend.entity.dto.UserDto;
import com.gpb.backend.service.UserManagementService;
import com.gpb.common.entity.user.TokenRequestDto;
import com.gpb.common.service.UserLinkerService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserLinkerControllerTest {

    @Mock
    private UserLinkerService userLinkerService;
    @Mock
    private UserManagementService userManagementService;
    @InjectMocks
    private UserLinkerController userController;

    @Test
    void testLinkUser_whenSuccess_shouldInvokeServiceMethod() {
        long userId = 123L;
        long currentBasicUserId = 1L;
        String token = "telegram-token";
        UserDto user = new UserDto("username", "password", "token", "role", "ua");
        user.setId(userId);
        user.setBasicUserId(currentBasicUserId);


        userController.linkUser(new TokenRequestDto(token), user);


        verify(userLinkerService).linkAccounts(token, userId);
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
