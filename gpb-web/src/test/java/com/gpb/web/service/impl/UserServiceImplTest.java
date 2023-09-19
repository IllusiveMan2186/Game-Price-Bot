package com.gpb.web.service.impl;

import com.gpb.web.bean.WebUser;
import com.gpb.web.exception.EmailAlreadyExistException;
import com.gpb.web.exception.NotFoundException;
import com.gpb.web.repository.WebUserRepository;
import com.gpb.web.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    WebUserRepository repository = mock(WebUserRepository.class);

    UserService userService = new UserServiceImpl(repository);

    private final WebUser user = new WebUser();

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void getUserByIdSuccessfullyShouldReturnUser() {
        int id = 1;
        when(repository.findById(id)).thenReturn(user);

        WebUser result = userService.getUserById(id);

        assertEquals(user, result);
    }

    @Test
    void getUserByIdThatNotFoundShouldThrowException() {
        int id = 1;
        when(repository.findById(id)).thenReturn(null);

        assertThrows(NotFoundException.class, () -> {
            userService.getUserById(id);
        }, "User with id '1' not found");
    }

    @Test
    void getUserByUsernameSuccessfullyShouldReturnUser() {
        String username = "username";
        when(repository.findByUsername(username)).thenReturn(user);

        WebUser result = userService.getUserByUsername(username);

        assertEquals(user, result);
    }

    @Test
    void getUserByUsernameThatNotFoundShouldThrowException() {
        String username = "username";
        when(repository.findByUsername(username)).thenReturn(null);

        assertThrows(NotFoundException.class, () -> {
            userService.getUserByUsername(username);
        }, "User with username 'username' not found");
    }

    @Test
    void getUserByEmailSuccessfullyShouldReturnUser() {
        String email = "email";
        when(repository.findByEmail(email)).thenReturn(user);

        WebUser result = userService.getUserByEmail(email);

        assertEquals(user, result);
    }

    @Test
    void getUserByEmailThatNotFoundShouldThrowException() {
        String email = "email";
        when(repository.findByEmail(email)).thenReturn(null);

        assertThrows(NotFoundException.class, () -> {
            userService.getUserByEmail(email);
        }, "User with email 'email' not found");
    }

    @Test
    void createUserSuccessfullyShouldSaveAndReturnUser() {
        String email = "email";
        user.setEmail(email);
        when(repository.findByEmail(email)).thenReturn(null);
        when(repository.save(user)).thenReturn(user);

        WebUser result = userService.createUser(user);

        assertEquals(user, result);
    }

    @Test
    void createUserWithRegisteredEmailShouldThrowException() {
        String email = "email";
        user.setEmail(email);
        when(repository.findByEmail(email)).thenReturn(user);

        assertThrows(EmailAlreadyExistException.class, () -> {
            userService.createUser(user);
        }, "User with this email already exist");
    }


    @Test
    void deleteUserSuccessfullyShouldRemoveAndReturnTrue() {
        int id = 1;
        when(repository.findById(id)).thenReturn(user);

        boolean result = userService.deleteUser(id);

        assertTrue(result);
    }

    @Test
    void deleteNotExistingUserShouldReturnFalse() {
        int id = 1;
        when(repository.findById(id)).thenReturn(null);

        boolean result = userService.deleteUser(id);

        assertFalse(result);
    }
}