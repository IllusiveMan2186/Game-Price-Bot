package com.gpb.game.unit.controller;

import com.gpb.common.entity.user.NotificationRequestDto;
import com.gpb.common.entity.user.TokenRequestDto;
import com.gpb.common.entity.user.UserNotificationType;
import com.gpb.game.controller.UserController;
import com.gpb.game.entity.user.BasicUser;
import com.gpb.game.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @Test
    void testCreateAccountLinker_whenSuccess_shouldReturnCreatedUserId() {
        long userId = 1L;
        String token = "token";
        when(userService.createAccountLinkerToken(userId)).thenReturn(token);


        String result = userController.createAccountLinkerToken(userId);


        assertEquals(token, result);
        verify(userService, times(1)).createAccountLinkerToken(userId);
    }

    @Test
    void testUserAccountLink_whenSuccess_shouldReturnCreatedUserId() {
        long userId = 1L;
        String token = "token";
        BasicUser user = new BasicUser();
        user.setId(userId);
        when(userService.linkUsers(token, userId)).thenReturn(user);


        Long result = userController.userAccountLink(new TokenRequestDto(token), userId);


        assertEquals(userId, result);
        verify(userService, times(1)).linkUsers(token, userId);
    }

    @Test
    void testCreateUser_whenSuccess_shouldReturnCreatedUserId() {
        BasicUser mockUser = new BasicUser();
        mockUser.setId(1L);
        when(userService.createUser(UserNotificationType.EMAIL)).thenReturn(mockUser);


        Long userId = userController.createUser(new NotificationRequestDto(UserNotificationType.EMAIL));


        assertEquals(1L, userId);
        verify(userService, times(1)).createUser(UserNotificationType.EMAIL);
    }
}
