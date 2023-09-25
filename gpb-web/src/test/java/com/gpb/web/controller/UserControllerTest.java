package com.gpb.web.controller;

import com.gpb.web.bean.WebUser;
import com.gpb.web.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UserControllerTest {

    UserService service = mock(UserService.class);

    private final UserController controller = new UserController(service);

    private final WebUser user = new WebUser("email", "password");

    @Test
    void getUserByIdSuccessfullyShouldReturnUser() {
        int id = 1;
        when(service.getUserById(id)).thenReturn(user);

        UserDetails result = controller.getUserById(id);

        assertEquals(user, result);
    }


    @Test
    void createUserSuccessfullyShouldReturnUser() {
        String email = "email";
        user.setEmail(email);
        when(service.createUser(user)).thenReturn(user);

        UserDetails result = controller.createUser(user);

        assertEquals(user, result);
    }

    @Test
    void removeUserSuccessfullyShouldReturnTrue() {
        int id = 1;
        when(service.deleteUser(id)).thenReturn(true);

        boolean result = controller.removeUser(id);

        assertTrue(result);
    }
}