package com.gpb.web.controller;

import com.gpb.web.GpbWebApplication;
import com.gpb.web.bean.WebUser;
import com.gpb.web.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UserControllerTest {

    UserService service = mock(UserService.class);

    private UserController controller = new UserController(service);

    private WebUser user = new WebUser();

    @Test
    void getUserByIdSuccessfullyShouldReturnUser() {
        int id = 1;
        when(service.getUserById(id)).thenReturn(user);

        WebUser result = controller.getUserById(id);

        assertEquals(user, result);
    }

    @Test
    void getUserByUsernameSuccessfullyShouldReturnUser() {
        String name = "name";
        when(service.getUserByUsername(name)).thenReturn(user);

        WebUser result = controller.getUserByUsername(name);

        assertEquals(user, result);
    }

    @Test
    void getUserByEmailSuccessfullyShouldReturnUser() {
        String email = "email";
        when(service.getUserByEmail(email)).thenReturn(user);

        WebUser result = controller.getUserByEmail(email);

        assertEquals(user, result);
    }

    @Test
    void createUserSuccessfullyShouldReturnUser() {
        String email = "email";
        user.setEmail(email);
        when(service.createUser(user)).thenReturn(user);

        WebUser result = controller.createUser(user);

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