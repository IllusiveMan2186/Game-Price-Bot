package com.gpb.web.service.impl;

import com.gpb.web.bean.user.Credentials;
import com.gpb.web.bean.user.UserDto;
import com.gpb.web.bean.user.UserRegistration;
import com.gpb.web.bean.user.WebUser;
import com.gpb.web.exception.EmailAlreadyExistException;
import com.gpb.web.exception.LoginFailedException;
import com.gpb.web.exception.NotFoundException;
import com.gpb.web.exception.UserDataNotChangedException;
import com.gpb.web.exception.UserLockedException;
import com.gpb.web.repository.WebUserRepository;
import com.gpb.web.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.nio.CharBuffer;
import java.util.Calendar;
import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    private static final String ENCODED_PASSWORD = "$2a$04$6B90esin.A8CPQ7PY2EheOu7nFzKBrHGlWlNyKlmtRCPPiikObH/W";

    WebUserRepository repository = mock(WebUserRepository.class);

    PasswordEncoder encoder = mock(PasswordEncoder.class);

    UserService userService = new UserServiceImpl(repository, encoder);

    private final WebUser user = new WebUser("email", ENCODED_PASSWORD, false, 0, null);

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
        WebUser newUser = new WebUser("email2", "password2", false, 0, null);
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
        WebUser newUser = new WebUser("email", "pass", false, 0, null);
        when(repository.findById(1)).thenReturn(Optional.of(user));
        UserRegistration userRegistration = new UserRegistration(newUser);
        when(encoder.matches(CharBuffer.wrap(userRegistration.getPassword()), user.getPassword())).thenReturn(true);
        when(encoder.encode(CharBuffer.wrap(userRegistration.getPassword()))).thenReturn(newUser.getPassword());

        assertThrows(UserDataNotChangedException.class, () -> userService.updateUser(userRegistration, 1),
                "User didn't changed during update operation");
    }

    @Test
    void updateUserWithRegisteredEmailShouldThrowException() {
        WebUser newUser = new WebUser("email2", "password2", false, 0, null);
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

    @Test
    void loginUserSuccessfullyShouldReturnUser() {
        Credentials credentials = new Credentials("email", "pass".toCharArray());
        WebUser user = new WebUser("email", "pass", false, 0, null);
        when(repository.findByEmail(credentials.getEmail())).thenReturn(Optional.of(user));
        when(encoder.matches(CharBuffer.wrap(credentials.getPassword()),user.getPassword()))
                .thenReturn(true);

        UserDto result = userService.login(credentials);

        assertEquals(new UserDto(user), result);
    }

    @Test
    void loginUserWithWithWrongEmailShouldThrowException() {
        Credentials credentials = new Credentials("email", "pass".toCharArray());
        when(repository.findByEmail(credentials.getEmail())).thenReturn(Optional.empty());

        assertThrows(LoginFailedException.class, () -> userService.login(new Credentials("email", "pass".toCharArray())),
                "Invalid email or password");
    }

    @Test
    void loginUserWithWithWrongPasswordShouldThrowExceptionAndIncreaseFailedAttempt() {
        Credentials credentials = new Credentials("email", "pass".toCharArray());
        WebUser newUser = new WebUser("email", "pass", false, 0, null);
        when(repository.findByEmail(credentials.getEmail())).thenReturn(Optional.of(newUser));


        assertThrows(LoginFailedException.class, () -> userService.login(new Credentials("email", "pass".toCharArray())),
                "Invalid email or password");
        verify(repository).save(new WebUser("email", "pass", false, 1, null));
    }

    @Test
    void loginUserWithWrongPasswordAndTooManyFailedAttemptsShouldThrowExceptionAndLockUser() {
        Credentials credentials = new Credentials("email", "pass".toCharArray());
        user.setFailedAttempt(4);
        when(repository.findByEmail(credentials.getEmail())).thenReturn(Optional.of(user));


        assertThrows(LoginFailedException.class, () -> userService.login(new Credentials("email", "pass".toCharArray())),
                "Invalid email or password");
        verify(repository).save(new WebUser("email", "pass", true, 5, any()));
    }

    @Test
    void loginUserSuccessfullyThatWasLockedShouldUnlockAndReturnUser() {
        Credentials credentials = new Credentials("email", "pass".toCharArray());
        Calendar lockTime = Calendar.getInstance();
        lockTime.setTime(new Date());
        lockTime.add(Calendar.DATE, -2);
        WebUser user = new WebUser("email", "pass", true, 5, lockTime.getTime());
        when(repository.findByEmail(credentials.getEmail())).thenReturn(Optional.of(user));
        when(encoder.matches(CharBuffer.wrap(credentials.getPassword()), user.getPassword()))
                .thenReturn(true);

        UserDto result = userService.login(credentials);

        assertEquals(new UserDto(user), result);
        verify(repository).save(new WebUser("email", "pass", false, 0, null));
    }

    @Test
    void loginLockedUserShouldThrowException() {
        Credentials credentials = new Credentials("email", "pass".toCharArray());
        Calendar lockTime = Calendar.getInstance();
        lockTime.setTime(new Date());
        WebUser user = new WebUser("email", "pass", true, 5, lockTime.getTime());
        when(repository.findByEmail(credentials.getEmail())).thenReturn(Optional.of(user));
        when(encoder.matches(CharBuffer.wrap(credentials.getPassword()), user.getPassword()))
                .thenReturn(true);

        assertThrows(UserLockedException.class, () -> userService.login(new Credentials("email", "pass".toCharArray())),
                "Your account has been locked for 24 hours due to many failed login attempts.");
    }

    @Test
    void getUserByEmailShouldReturnUser() {
        when(repository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        UserDto result = userService.getUserByEmail(user.getEmail());

        assertEquals(new UserDto(user), result);
    }

    @Test
    void getUserByEmailNotExistingEmailShouldThrowException() {
        when(repository.findByEmail("email")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.getUserByEmail(user.getEmail()),
                "User with email 'email' not found");
    }

    private WebUser getWebUser(UserRegistration userRegistration) {
        return WebUser.builder()
                .email(userRegistration.getEmail())
                .password(ENCODED_PASSWORD).build();
    }
}