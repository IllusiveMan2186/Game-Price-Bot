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
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    WebUserRepository repository = mock(WebUserRepository.class);

    UserService userService = new UserServiceImpl(repository);

    private final WebUser user = new WebUser("email", "password");
    private UserDetails userDetails = getUser(user);

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

        assertThrows(NotFoundException.class, () -> {
            userService.getUserById(id);
        }, "User with id '1' not found");
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

        UserDetails result = userService.createUser(user);

        assertEquals(user, result);
    }

    @Test
    void createUserWithRegisteredEmailShouldThrowException() {
        String email = "email";
        user.setEmail(email);
        userDetails = getUser(user);
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

    private WebUser getWebUser(final UserDetails user) {
        return new WebUser(user.getUsername(), user.getPassword());
    }

    private User getUser(WebUser user) {
        List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("user"));

        return new User(user.getEmail(), user.getPassword(), authorities);
    }
}