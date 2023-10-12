package com.gpb.web.service.impl;

import com.gpb.web.bean.user.UserDto;
import com.gpb.web.bean.user.UserRegistration;
import com.gpb.web.bean.user.WebUser;
import com.gpb.web.exception.EmailAlreadyExistException;
import com.gpb.web.exception.UserDataNotChangedException;
import com.gpb.web.repository.WebUserRepository;
import com.gpb.web.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.nio.CharBuffer;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    private static final String ENCODED_PASSWORD = "$2a$04$6B90esin.A8CPQ7PY2EheOu7nFzKBrHGlWlNyKlmtRCPPiikObH/W";

    WebUserRepository repository = mock(WebUserRepository.class);

    PasswordEncoder encoder = mock(PasswordEncoder.class);

    UserService userService = new UserServiceImpl(repository, encoder);

    private final WebUser user = new WebUser("email", ENCODED_PASSWORD);

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void createUserSuccessfullyShouldSaveAndReturnUser() {
        when(repository.findByEmail(user.getEmail())).thenReturn(Optional.empty());
        when(repository.save(user)).thenReturn(user);
        UserRegistration userRegistration = new UserRegistration(user);
        when(encoder.encode(CharBuffer.wrap(userRegistration.getPassword()))).thenReturn(user.getPassword());

        UserDto result = userService.createUser(userRegistration);

        assertEquals(new UserDto(user), result);
    }

    @Test
    void createUserWithRegisteredEmailShouldThrowException() {
        when(repository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        assertThrows(EmailAlreadyExistException.class, () -> userService.createUser(new UserRegistration(user)),
                "User with this email already exist");
    }

    @Test
    void updateUserSuccessfullyShouldSaveAndReturnUser() {
        WebUser newUser = new WebUser("email2", "password2");
        newUser.setId(1);
        when(repository.findByEmail(user.getEmail())).thenReturn(Optional.empty());
        when(repository.findById(1)).thenReturn(Optional.of(user));
        when(repository.save(newUser)).thenReturn(newUser);
        UserRegistration userRegistration = new UserRegistration(newUser);
        when(encoder.encode(CharBuffer.wrap(userRegistration.getPassword()))).thenReturn(newUser.getPassword());

        UserDto result = userService.updateUser(userRegistration, 1);

        assertEquals(new UserDto(newUser), result);
    }

    @Test
    void updateUserThatDidNotChangedInfoShouldThrowException() {
        WebUser newUser = new WebUser("email", "pass");
        when(repository.findById(1)).thenReturn(Optional.of(user));
        UserRegistration userRegistration = new UserRegistration(newUser);
        when(encoder.matches(CharBuffer.wrap(userRegistration.getPassword()), user.getPassword())).thenReturn(true);
        when(encoder.encode(CharBuffer.wrap(userRegistration.getPassword()))).thenReturn(newUser.getPassword());

        assertThrows(UserDataNotChangedException.class, () -> userService.updateUser(userRegistration, 1),
                "User didn't changed during update operation");
    }

    @Test
    void updateUserWithRegisteredEmailShouldThrowException() {
        WebUser newUser = new WebUser("email2", "password2");
        when(repository.findByEmail(newUser.getEmail())).thenReturn(Optional.of(new WebUser()));
        when(repository.findById(1)).thenReturn(Optional.of(user));

        assertThrows(EmailAlreadyExistException.class, () -> userService.updateUser(new UserRegistration(newUser), 1),
                "User with this email already exist");
    }

    @Test
    void addGameToUserListOfGamesShouldCallRepository() {

        userService.addGameToUserListOfGames(1, 1);

        verify(repository).addGameToUserListOfGames(1, 1);
    }

    private WebUser getWebUser(UserRegistration userRegistration) {
        return WebUser.builder()
                .email(userRegistration.getEmail())
                .password(ENCODED_PASSWORD).build();
    }
}