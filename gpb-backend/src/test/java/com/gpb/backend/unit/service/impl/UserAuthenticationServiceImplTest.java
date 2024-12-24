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
import com.gpb.backend.repository.WebUserRepository;
import com.gpb.backend.service.impl.UserAuthenticationServiceImpl;
import com.gpb.backend.util.Constants;
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
    void testGetUserByEmail_whenSuccess_shouldReturnUser() {
        String email = "email";
        WebUser webUser = new WebUser();
        UserDto userDto = new UserDto("email", "pass", "token", "role", "ua");
        when(webUserRepository.findByEmail(email)).thenReturn(Optional.of(webUser));
        when(modelMapper.map(webUser, UserDto.class)).thenReturn(userDto);


        UserDto result = userService.getUserByEmail(email);


        assertEquals(userDto, result);
        verify(webUserRepository).findByEmail(email);
    }

    @Test
    void testGetUserByEmail_whenUserNotFound_shouldThrowNotFoundException() {
        String email = "email";

        when(webUserRepository.findByEmail(email)).thenReturn(Optional.empty());


        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> userService.getUserByEmail(email)
        );


        assertEquals("app.user.error.email.not.found", exception.getMessage());
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
        when(restTemplateHandler.executeRequest(anyString(), eq(HttpMethod.POST), isNull(), eq(Long.class)))
                .thenReturn(1L);
        when(webUserRepository.save(any(WebUser.class))).thenReturn(webUser);


        WebUser result = userService.createUser(registration);


        assertNotNull(result);
        verify(webUserRepository).findByEmail(registration.getEmail());
        verify(passwordEncoder).encode(CharBuffer.wrap(registration.getPassword()));
        verify(restTemplateHandler).executeRequest(contains("/user"), eq(HttpMethod.POST), isNull(), eq(Long.class));
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

        when(webUserRepository.findById(userId)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> userService.updateUserPassword(newPassword, userDto));

        assertEquals("app.user.error.id.not.found", exception.getMessage());
        verify(webUserRepository, times(1)).findById(userId);
    }

    @Test
    void testUpdateUserPassword_whenPasswordIsSame_shouldThrowUserDataNotChangedException() {
        long userId = 1L;
        UserDto userDto = new UserDto("email", "pass", "token", "role", "ua");
        userDto.setId(userId);
        char[] newPassword = "samePassword".toCharArray();
        WebUser webUser = new WebUser();
        webUser.setId(userId);
        webUser.setPassword("encodedPassword");

        when(webUserRepository.findById(userDto.getId())).thenReturn(Optional.of(webUser));
        when(passwordEncoder.matches(CharBuffer.wrap(newPassword), webUser.getPassword())).thenReturn(true);


        UserDataNotChangedException exception = assertThrows(UserDataNotChangedException.class,
                () -> userService.updateUserPassword(newPassword, userDto));


        assertEquals("app.user.error.did.not.changed", exception.getMessage());
        verify(webUserRepository, times(1)).findById(userId);
        verify(passwordEncoder, times(1)).matches(CharBuffer.wrap(newPassword), webUser.getPassword());
    }

    @Test
    void testUpdateUserPassword_whenPasswordIsDifferent_shouldUpdatePassword() {
        long userId = 1L;
        UserDto userDto = new UserDto("email", "pass", "token", "role", "ua");
        userDto.setId(userId);
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
        when(passwordEncoder.matches(CharBuffer.wrap(newPassword), webUser.getPassword())).thenReturn(false);
        when(passwordEncoder.encode(CharBuffer.wrap(newPassword))).thenReturn("newEncodedPassword");
        when(webUserRepository.save(webUser)).thenReturn(updatedUser);
        when(modelMapper.map(updatedUser, UserDto.class)).thenReturn(updatedUserDto);

        UserDto result = userService.updateUserPassword(newPassword, userDto);

        assertNotNull(result);
        assertEquals(userId, result.getId());
        verify(webUserRepository, times(1)).findById(userId);
        verify(passwordEncoder, times(1)).encode(CharBuffer.wrap(newPassword));
        verify(webUserRepository, times(1)).save(webUser);
        verify(modelMapper, times(1)).map(updatedUser, UserDto.class);
    }

    @Test
    void testLogin_whenSuccess_shouldReturnUserDto() {
        Credentials credentials = new Credentials("user@example.com", "password".toCharArray());

        WebUser user = new WebUser();
        user.setActivated(true);
        user.setLocked(false);
        user.setPassword("encodedPassword");

        when(webUserRepository.findByEmail(credentials.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(CharBuffer.wrap(credentials.getPassword()), user.getPassword())).thenReturn(true);
        UserDto userDto = new UserDto("username", "password", "token", "role", "ua");
        when(modelMapper.map(user, UserDto.class)).thenReturn(userDto);


        UserDto result = userService.login(credentials);


        assertNotNull(result);
        verify(webUserRepository).findByEmail(credentials.getEmail());
        verify(passwordEncoder).matches(CharBuffer.wrap(credentials.getPassword()), user.getPassword());
        verify(modelMapper).map(user, UserDto.class);
    }

    @Test
    void testLogin_whenUserNotActivated_shouldThrowUserNotActivatedException() {
        Credentials credentials = new Credentials("user@example.com", null);

        WebUser user = new WebUser();
        user.setActivated(false);
        when(webUserRepository.findByEmail(credentials.getEmail())).thenReturn(Optional.of(user));


        assertThrows(UserNotActivatedException.class, () -> userService.login(credentials));
    }

    @Test
    void testLogin_whenUserWasLocked_shouldUnlockAndReturnUserDto() {
        Credentials credentials = new Credentials("user@example.com", "password".toCharArray());

        WebUser lockedUser = new WebUser();
        lockedUser.setActivated(true);
        lockedUser.setLocked(true);
        lockedUser.setPassword("encodedPassword");
        lockedUser.setFailedAttempt(3);
        lockedUser.setLockTime(new Date(new Date().getTime() - (Constants.LOCK_TIME_DURATION * 2 )));

        WebUser expectedUSerAfterUnlock = new WebUser();
        expectedUSerAfterUnlock.setActivated(true);
        expectedUSerAfterUnlock.setLocked(false);
        expectedUSerAfterUnlock.setPassword("encodedPassword");

        when(webUserRepository.findByEmail(credentials.getEmail())).thenReturn(Optional.of(lockedUser));
        when(passwordEncoder.matches(CharBuffer.wrap(credentials.getPassword()), lockedUser.getPassword())).thenReturn(true);
        UserDto userDto = new UserDto("username", "password", "token", "role", "ua");
        when(modelMapper.map(lockedUser, UserDto.class)).thenReturn(userDto);


        UserDto result = userService.login(credentials);


        assertNotNull(result);
        verify(webUserRepository).findByEmail(credentials.getEmail());
        verify(webUserRepository).save(expectedUSerAfterUnlock);
        verify(passwordEncoder).matches(CharBuffer.wrap(credentials.getPassword()), lockedUser.getPassword());
        verify(modelMapper).map(lockedUser, UserDto.class);
    }

    @Test
    void testLogin_whenWrongCredentials_shouldIncrementFailedAttempts() {
        Credentials credentials = new Credentials("user@example.com", "password".toCharArray());

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
        Credentials credentials = new Credentials("user@example.com", "password".toCharArray());

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
        Credentials credentials = new Credentials("user@example.com", null);

        WebUser user = new WebUser();
        user.setActivated(true);
        user.setLocked(true);
        user.setLockTime(new Date(System.currentTimeMillis() + 10000)); // Future lock time
        when(webUserRepository.findByEmail(credentials.getEmail())).thenReturn(Optional.of(user));


        assertThrows(UserLockedException.class, () -> userService.login(credentials));
    }

    @Test
    void testUpdateUserEmail_whenEmailAlreadyExists_shouldThrowEmailAlreadyExistException() {
        String newEmail = "newemail@example.com";
        UserDto userDto = new UserDto("username", "password", "token", "role", "ua");
        userDto.setId(1L);

        when(webUserRepository.findByEmail(newEmail)).thenReturn(Optional.of(new WebUser()));

        assertThrows(EmailAlreadyExistException.class, () -> userService.updateUserEmail(newEmail, userDto));
    }

    @Test
    void testUpdateUserEmail_whenEmailAlreadyExist_shouldReturnUpdatedUserDto() {
        String newEmail = "newemail@example.com";
        UserDto userDto = new UserDto("username", "password", "token", "role", "ua");
        userDto.setId(1L);

        WebUser webUser = new WebUser();
        when(webUserRepository.findById(userDto.getId())).thenReturn(Optional.of(webUser));
        when(webUserRepository.findByEmail(newEmail)).thenReturn(Optional.empty());
        when(webUserRepository.save(any(WebUser.class))).thenReturn(webUser);

        UserDto updatedUserDto = new UserDto("username", "password", "token", "role", "ua");
        updatedUserDto.setEmail(newEmail);
        when(modelMapper.map(webUser, UserDto.class)).thenReturn(updatedUserDto);


        UserDto result = userService.updateUserEmail(newEmail, userDto);


        assertNotNull(result);
        assertEquals(newEmail, result.getEmail());
        verify(webUserRepository).findById(userDto.getId());
        verify(webUserRepository).findByEmail(newEmail);
        verify(webUserRepository).save(any(WebUser.class));
        verify(modelMapper).map(webUser, UserDto.class);
    }
}
