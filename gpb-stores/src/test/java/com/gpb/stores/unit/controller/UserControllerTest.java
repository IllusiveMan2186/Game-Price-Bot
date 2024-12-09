package com.gpb.stores.unit.controller;

import com.gpb.stores.bean.user.BasicUser;
import com.gpb.stores.controller.UserController;
import com.gpb.stores.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createAccountLinker_ShouldReturnCreatedUserId() {
        long userId = 1L;
        String token = "token";
        when(userService.getAccountLinkerToken(userId)).thenReturn(token);


        String result = userController.createAccountLinkerToken(userId);


        assertEquals(token, result);
        verify(userService, times(1)).getAccountLinkerToken(userId);
    }

    @Test
    void createUser_ShouldReturnCreatedUserId() {
        BasicUser mockUser = new BasicUser();
        mockUser.setId(1L);
        when(userService.createUser()).thenReturn(mockUser);


        Long userId = userController.createUser();


        assertEquals(1L, userId);
        verify(userService, times(1)).createUser();
    }
}
