package com.gpb.web.service.impl;

import com.gpb.web.bean.WebUser;
import com.gpb.web.exception.EmailAlreadyExistException;
import com.gpb.web.exception.NotFoundException;
import com.gpb.web.exception.UserDataNotChangedException;
import com.gpb.web.repository.WebUserRepository;
import com.gpb.web.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    WebUserRepository repository = mock(WebUserRepository.class);

    UserService userService = new UserServiceImpl(repository);

    private final WebUser user = new WebUser("email", "password");

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

        UserDetails result = userService.getUserById(id);

        assertEquals(user, result);
    }

    @Test
    void getUserByIdThatNotFoundShouldThrowException() {
        int id = 1;
        when(repository.findById(id)).thenReturn(null);

        assertThrows(NotFoundException.class, () -> userService.getUserById(id),
                "User with id '1' not found");
    }

    @Test
    void getUserByEmailSuccessfullyShouldReturnUser() {
        String email = "email";
        when(repository.findByEmail(email)).thenReturn(user);

        UserDetails result = userService.getUserByEmail(email);

        assertEquals(user, result);
    }

    @Test
    void getUserByEmailThatNotFoundShouldThrowException() {
        String email = "email";
        when(repository.findByEmail(email)).thenReturn(null);

        assertThrows(NotFoundException.class, () -> userService.getUserByEmail(email),
                "User with email 'email' not found");
    }

    @Test
    void createUserSuccessfullyShouldSaveAndReturnUser() {
        when(repository.findByEmail(user.getEmail())).thenReturn(null);
        when(repository.save(user)).thenReturn(user);

        UserDetails result = userService.createUser(user);

        assertEquals(user, result);
    }

    @Test
    void createUserWithRegisteredEmailShouldThrowException() {
        when(repository.findByEmail(user.getEmail())).thenReturn(user);

        assertThrows(EmailAlreadyExistException.class, () -> userService.createUser(user),
                "User with this email already exist");
    }

    @Test
    void updateUserSuccessfullyShouldSaveAndReturnUser() {
        WebUser newUser = new WebUser("email2", "password2");
        when(repository.findByEmail(user.getEmail())).thenReturn(null);
        when(repository.save(newUser)).thenReturn(newUser);

        UserDetails result = userService.updateUser(newUser, user);

        assertEquals(newUser, result);
    }

    @Test
    void updateUserThatDidNotChangedInfoShouldThrowException() {
        WebUser newUser = new WebUser("email", "password");

        assertThrows(UserDataNotChangedException.class, () -> userService.updateUser(newUser, user),
                "User didn't changed during update operation");
    }

    @Test
    void updateUserWithRegisteredEmailShouldThrowException() {
        WebUser newUser = new WebUser("email2", "password2");
        when(repository.findByEmail(newUser.getEmail())).thenReturn(new WebUser());

        assertThrows(EmailAlreadyExistException.class, () -> userService.updateUser(newUser, user),
                "User with this email already exist");
    }
}