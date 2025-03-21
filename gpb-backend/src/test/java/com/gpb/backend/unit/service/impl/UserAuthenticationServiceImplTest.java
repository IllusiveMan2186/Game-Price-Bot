package com.gpb.backend.unit.service.impl;

import com.gpb.backend.entity.Credentials;
import com.gpb.backend.entity.UserRegistration;
import com.gpb.backend.entity.WebUser;
import com.gpb.backend.entity.dto.UserDto;
import com.gpb.backend.exception.EmailAlreadyExistException;
import com.gpb.backend.exception.LoginFailedException;
import com.gpb.backend.exception.UserDataNotChangedException;
import com.gpb.backend.exception.UserLockedException;
import com.gpb.backend.exception.UserNotActivatedException;
import com.gpb.backend.exception.WrongPasswordException;
import com.gpb.backend.repository.WebUserRepository;
import com.gpb.backend.service.impl.UserAuthenticationServiceImpl;
import com.gpb.backend.util.Constants;
import com.gpb.common.entity.user.NotificationRequestDto;
import com.gpb.common.entity.user.UserNotificationType;
import com.gpb.common.exception.NotFoundException;
import com.gpb.common.service.RestTemplateHandlerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpMethod;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.nio.CharBuffer;
import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserAuthenticationServiceImplTest {

    private static final String GAME_SERVICE_URL = "gameServiceUrl";

    @Mock
    private WebUserRepository webUserRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private RestTemplateHandlerService restTemplateHandler;

    @InjectMocks
    private UserAuthenticationServiceImpl userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userService = new UserAuthenticationServiceImpl(webUserRepository, passwordEncoder, modelMapper, restTemplateHandler);
    }

    @Test
    void testGetUserById_whenSuccess_shouldReturnUserDto() {
        long userId = 1L;
        WebUser webUser = new WebUser();
        UserDto userDto = new UserDto("username", "password", "token", "role", "ua");
        when(webUserRepository.findById(userId)).thenReturn(Optional.of(webUser));
        when(modelMapper.map(webUser, UserDto.class)).thenReturn(userDto);


        UserDto result = userService.getUserById(userId);


        assertNotNull(result);
        verify(webUserRepository).findById(userId);
        verify(modelMapper).map(webUser, UserDto.class);
    }

    @Test
    void testGetUserById_whenUserNotFound_shouldThrowNotFoundException() {
        long userId = 1L;
        when(webUserRepository.findById(userId)).thenReturn(Optional.empty());


        assertThrows(NotFoundException.class, () -> userService.getUserById(userId));
    }

    @Test
    void testCreateUser_whenSuccess_shouldCreateAndReturnWebUser() {
        UserRegistration registration = new UserRegistration();
        registration.setEmail("test@example.com");
        registration.setPassword("password".toCharArray());
        registration.setLocale("en");

        WebUser webUser = new WebUser();
        when(webUserRepository.findByEmail(registration.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(CharBuffer.wrap(registration.getPassword()))).thenReturn("encodedPassword");
        when(restTemplateHandler.executeRequestWithBody(
                anyString(),
                eq(HttpMethod.POST),
                isNull(),
                eq(new NotificationRequestDto(UserNotificationType.EMAIL)),
                eq(Long.class)))
                .thenReturn(1L);
        when(webUserRepository.save(any(WebUser.class))).thenReturn(webUser);


        WebUser result = userService.createUser(registration);


        assertNotNull(result);
        verify(webUserRepository).findByEmail(registration.getEmail());
        verify(passwordEncoder).encode(CharBuffer.wrap(registration.getPassword()));
        verify(restTemplateHandler).executeRequestWithBody(
                contains("/user"),
                eq(HttpMethod.POST),
                isNull(),
                eq(new NotificationRequestDto(UserNotificationType.EMAIL)),
                eq(Long.class));
        verify(webUserRepository).save(any(WebUser.class));
    }

    @Test
    void testCreateUser_whenEmailAlreadyExists_shouldThrowException() {
        UserRegistration registration = new UserRegistration();
        registration.setEmail("test@example.com");
        when(webUserRepository.findByEmail(registration.getEmail())).thenReturn(Optional.of(new WebUser()));


        assertThrows(EmailAlreadyExistException.class, () -> userService.createUser(registration));
    }

    @Test
    void testUpdateUserPassword_whenUserNotFound_shouldThrowNotFoundException() {
        long userId = 1L;
        UserDto userDto = new UserDto("email", "pass", "token", "role", "ua");
        userDto.setId(userId);
        char[] newPassword = "newPassword".toCharArray();
        char[] oldPassword = "oldPassword".toCharArray();

        when(webUserRepository.findById(userId)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> userService.updateUserPassword(oldPassword, newPassword, userDto));

        assertEquals("app.user.error.id.not.found", exception.getMessage());
        verify(webUserRepository, times(1)).findById(userId);
    }

    @Test
    void testUpdateUserPassword_whenOldPasswordWrong_shouldThrowWrongPasswordException() {
        long userId = 1L;
        UserDto userDto = new UserDto("email", "pass", "token", "role", "ua");
        userDto.setId(userId);
        char[] newPassword = "samePassword".toCharArray();
        char[] oldPassword = "oldPassword".toCharArray();
        WebUser webUser = new WebUser();
        webUser.setId(userId);
        webUser.setPassword("encodedPassword");

        when(webUserRepository.findById(userDto.getId())).thenReturn(Optional.of(webUser));
        when(passwordEncoder.matches(CharBuffer.wrap(oldPassword), webUser.getPassword())).thenReturn(false);


        WrongPasswordException exception = assertThrows(WrongPasswordException.class,
                () -> userService.updateUserPassword(oldPassword, newPassword, userDto));


        assertEquals("app.user.error.wrong.password", exception.getMessage());
        verify(webUserRepository, times(1)).findById(userId);
        verify(passwordEncoder, times(1)).matches(CharBuffer.wrap(oldPassword), webUser.getPassword());
        verify(passwordEncoder, times(0)).matches(CharBuffer.wrap(newPassword), webUser.getPassword());
    }

    @Test
    void testUpdateUserPassword_whenPasswordIsSame_shouldThrowUserDataNotChangedException() {
        long userId = 1L;
        UserDto userDto = new UserDto("email", "pass", "token", "role", "ua");
        userDto.setId(userId);
        char[] newPassword = "samePassword".toCharArray();
        char[] oldPassword = "oldPassword".toCharArray();
        WebUser webUser = new WebUser();
        webUser.setId(userId);
        webUser.setPassword("encodedPassword");

        when(webUserRepository.findById(userDto.getId())).thenReturn(Optional.of(webUser));
        when(passwordEncoder.matches(CharBuffer.wrap(oldPassword), webUser.getPassword())).thenReturn(true);
        when(passwordEncoder.matches(CharBuffer.wrap(newPassword), webUser.getPassword())).thenReturn(true);


        UserDataNotChangedException exception = assertThrows(UserDataNotChangedException.class,
                () -> userService.updateUserPassword(oldPassword, newPassword, userDto));


        assertEquals("app.user.error.did.not.changed", exception.getMessage());
        verify(webUserRepository, times(1)).findById(userId);
        verify(passwordEncoder, times(1)).matches(CharBuffer.wrap(newPassword), webUser.getPassword());
    }

    @Test
    void testUpdateUserPassword_whenPasswordIsDifferent_shouldUpdatePassword() {
        long userId = 1L;
        UserDto userDto = new UserDto("email", "pass", "token", "role", "ua");
        userDto.setId(userId);
        char[] oldPassword = "oldPassword".toCharArray();
        char[] newPassword = "newPassword".toCharArray();
        WebUser webUser = new WebUser();
        webUser.setId(userId);
        webUser.setPassword("oldEncodedPassword");

        WebUser updatedUser = new WebUser();
        updatedUser.setId(userId);
        updatedUser.setPassword("newEncodedPassword");

        UserDto updatedUserDto = new UserDto("email", "pass", "token", "role", "ua");
        updatedUserDto.setId(userId);

        when(webUserRepository.findById(userId)).thenReturn(Optional.of(webUser));
        when(passwordEncoder.matches(CharBuffer.wrap(oldPassword), webUser.getPassword())).thenReturn(true);
        when(passwordEncoder.matches(CharBuffer.wrap(newPassword), webUser.getPassword())).thenReturn(false);
        when(passwordEncoder.encode(CharBuffer.wrap(newPassword))).thenReturn("newEncodedPassword");
        when(webUserRepository.save(webUser)).thenReturn(updatedUser);
        when(modelMapper.map(updatedUser, UserDto.class)).thenReturn(updatedUserDto);

        UserDto result = userService.updateUserPassword(oldPassword, newPassword, userDto);

        assertNotNull(result);
        assertEquals(userId, result.getId());
        verify(webUserRepository, times(1)).findById(userId);
        verify(passwordEncoder, times(1)).encode(CharBuffer.wrap(newPassword));
        verify(webUserRepository, times(1)).save(webUser);
        verify(modelMapper, times(1)).map(updatedUser, UserDto.class);
    }

    @Test
    void testLogin_whenSuccess_shouldReturnUserDto() {
        Credentials credentials = new Credentials("user@example.com", "password".toCharArray(), false);

        WebUser user = new WebUser();
        user.setActivated(true);
        user.setLocked(false);
        user.setPassword("encodedPassword");

        when(webUserRepository.findByEmail(credentials.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(CharBuffer.wrap(credentials.getPassword()), user.getPassword())).thenReturn(true);
        when(webUserRepository.save(user)).thenReturn(user);


        WebUser result = userService.login(credentials);


        assertNotNull(result);
        verify(webUserRepository).findByEmail(credentials.getEmail());
        verify(passwordEncoder).matches(CharBuffer.wrap(credentials.getPassword()), user.getPassword());
    }

    @Test
    void testLogin_whenUserNotActivated_shouldThrowUserNotActivatedException() {
        Credentials credentials = new Credentials("user@example.com", null, false);

        WebUser user = new WebUser();
        user.setActivated(false);
        when(webUserRepository.findByEmail(credentials.getEmail())).thenReturn(Optional.of(user));


        assertThrows(UserNotActivatedException.class, () -> userService.login(credentials));
    }

    @Test
    void testLogin_whenUserWasLocked_shouldUnlockAndReturnUserDto() {
        Credentials credentials = new Credentials("user@example.com", "password".toCharArray(), false);

        WebUser lockedUser = new WebUser();
        lockedUser.setActivated(true);
        lockedUser.setLocked(true);
        lockedUser.setPassword("encodedPassword");
        lockedUser.setFailedAttempt(3);
        lockedUser.setLockTime(new Date(new Date().getTime() - (Constants.LOCK_TIME_DURATION * 2)));

        WebUser expectedUSerAfterUnlock = new WebUser();
        expectedUSerAfterUnlock.setActivated(true);
        expectedUSerAfterUnlock.setLocked(false);
        expectedUSerAfterUnlock.setPassword("encodedPassword");

        when(webUserRepository.findByEmail(credentials.getEmail())).thenReturn(Optional.of(lockedUser));
        when(passwordEncoder.matches(CharBuffer.wrap(credentials.getPassword()), lockedUser.getPassword())).thenReturn(true);
        UserDto userDto = new UserDto("username", "password", "token", "role", "ua");
        when(modelMapper.map(lockedUser, UserDto.class)).thenReturn(userDto);
        when(webUserRepository.save(expectedUSerAfterUnlock)).thenReturn(expectedUSerAfterUnlock);


        WebUser result = userService.login(credentials);


        assertEquals(expectedUSerAfterUnlock, result);
        verify(webUserRepository).findByEmail(credentials.getEmail());
        verify(webUserRepository).save(expectedUSerAfterUnlock);
        verify(passwordEncoder).matches(CharBuffer.wrap(credentials.getPassword()), lockedUser.getPassword());
    }

    @Test
    void testLogin_whenWrongCredentials_shouldIncrementFailedAttempts() {
        Credentials credentials = new Credentials("user@example.com", "password".toCharArray(), false);

        WebUser user = WebUser.builder()
                .isActivated(true)
                .isLocked(false)
                .failedAttempt(0)
                .password("encodedPassword").build();

        when(webUserRepository.findByEmail(credentials.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(CharBuffer.wrap(credentials.getPassword()), user.getPassword())).thenReturn(false);
        UserDto userDto = new UserDto("username", "password", "token", "role", "ua");
        when(modelMapper.map(user, UserDto.class)).thenReturn(userDto);


        LoginFailedException exception = assertThrows(LoginFailedException.class,
                () -> userService.login(credentials));


        assertEquals("app.user.error.login.error", exception.getMessage());
        verify(webUserRepository).findByEmail(credentials.getEmail());
        verify(webUserRepository).save(user);
        verify(passwordEncoder).matches(CharBuffer.wrap(credentials.getPassword()), user.getPassword());
        assertEquals(1, user.getFailedAttempt());
        assertFalse(user.isAccountLocked());
    }

    @Test
    void testLogin_whenWrongCredentialsAndMaxFailedAttemptsReached_shouldLockUser() {
        Credentials credentials = new Credentials("user@example.com", "password".toCharArray(), false);

        WebUser user = WebUser.builder()
                .isActivated(true)
                .isLocked(false)
                .failedAttempt(2)
                .password("encodedPassword").build();

        when(webUserRepository.findByEmail(credentials.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(CharBuffer.wrap(credentials.getPassword()), user.getPassword())).thenReturn(false);
        UserDto userDto = new UserDto("username", "password", "token", "role", "ua");
        when(modelMapper.map(user, UserDto.class)).thenReturn(userDto);


        LoginFailedException exception = assertThrows(LoginFailedException.class,
                () -> userService.login(credentials));


        assertEquals("app.user.error.login.error", exception.getMessage());
        verify(webUserRepository).findByEmail(credentials.getEmail());
        verify(webUserRepository).save(user);
        verify(passwordEncoder).matches(CharBuffer.wrap(credentials.getPassword()), user.getPassword());
        assertEquals(3, user.getFailedAttempt());
        assertTrue(user.isAccountLocked());
    }

    @Test
    void testLogin_whenUserIsLocked_shouldThrowUserLockedException() {
        Credentials credentials = new Credentials("user@example.com", null, false);

        WebUser user = new WebUser();
        user.setActivated(true);
        user.setLocked(true);
        user.setLockTime(new Date(System.currentTimeMillis() + 10000)); // Future lock time
        when(webUserRepository.findByEmail(credentials.getEmail())).thenReturn(Optional.of(user));


        assertThrows(UserLockedException.class, () -> userService.login(credentials));
    }

    @Test
    void testUpdateUserEmail_whenSuccess_shouldReturnUpdatedUserDto() {
        String newEmail = "newemail@example.com";

        WebUser webUser = new WebUser();
        when(webUserRepository.save(any(WebUser.class))).thenReturn(webUser);

        UserDto updatedUserDto = new UserDto("username", "password", "token", "role", "ua");
        updatedUserDto.setEmail(newEmail);
        when(modelMapper.map(webUser, UserDto.class)).thenReturn(updatedUserDto);


        UserDto result = userService.updateUserEmail(newEmail, webUser);


        assertNotNull(result);
        assertEquals(newEmail, result.getEmail());
        verify(webUserRepository).save(any(WebUser.class));
        verify(modelMapper).map(webUser, UserDto.class);
    }
}
