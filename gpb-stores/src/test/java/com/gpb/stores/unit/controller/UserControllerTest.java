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
import static org.mockito.Mockito.*;

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
    void createUser_ShouldReturnCreatedUserId() {
        // Arrange
        BasicUser mockUser = new BasicUser();
        mockUser.setId(1L);
        when(userService.createUser()).thenReturn(mockUser);

        // Act
        Long userId = userController.createUser();

        // Assert
        assertEquals(1L, userId);
        verify(userService, times(1)).createUser();
    }
}
