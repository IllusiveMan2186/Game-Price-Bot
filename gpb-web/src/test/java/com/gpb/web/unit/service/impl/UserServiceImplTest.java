package com.gpb.web.unit.service.impl;

import com.gpb.web.bean.user.Credentials;
import com.gpb.web.bean.user.UserDto;
import com.gpb.web.bean.user.UserNotificationType;
import com.gpb.web.bean.user.UserRegistration;
import com.gpb.web.bean.user.WebUser;
import com.gpb.web.configuration.MapperConfig;
import com.gpb.web.exception.EmailAlreadyExistException;
import com.gpb.web.exception.LoginFailedException;
import com.gpb.web.exception.NotFoundException;
import com.gpb.web.exception.UserDataNotChangedException;
import com.gpb.web.exception.UserLockedException;
import com.gpb.web.exception.UserNotActivatedException;
import com.gpb.web.repository.WebUserRepository;
import com.gpb.web.service.UserService;
import com.gpb.web.service.impl.UserServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.nio.CharBuffer;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

import static com.gpb.web.util.Constants.USER_ROLE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    private static final String ENCODED_PASSWORD = "$2a$04$6B90esin.A8CPQ7PY2EheOu7nFzKBrHGlWlNyKlmtRCPPiikObH/W";

    WebUserRepository repository = mock(WebUserRepository.class);

    UserRepository userRepository = mock(UserRepository.class);

    WebMessengerConnectorRepository connectorRepository = mock(WebMessengerConnectorRepository.class);

    PasswordEncoder encoder = mock(PasswordEncoder.class);

    private final ModelMapper modelMapper = new MapperConfig().modelMapper();

    UserService userService = new UserServiceImpl(userRepository, repository, encoder, modelMapper, connectorRepository);

    private final WebUser user =
            new WebUser(0, new BasicUser(), "email", ENCODED_PASSWORD, true, false, 0, null,
                    USER_ROLE, new Locale("ua"));

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void createUserSuccessfullyShouldSaveAndReturnUser() {
        BasicUser basicUser = new BasicUser();
        when(userRepository.save(any())).thenReturn(basicUser);
        when(repository.findByEmail(user.getEmail())).thenReturn(Optional.empty());
        user.setActivated(false);
        user.setLocale(new Locale("ua"));
        when(repository.save(user)).thenReturn(user);
        UserRegistration userRegistration = new UserRegistration(user);
        when(encoder.encode(CharBuffer.wrap(userRegistration.getPassword()))).thenReturn(user.getPassword());

        WebUser result = userService.createUser(userRegistration);

        assertEquals(user, result);
    }

    @Test
    void createUserWithRegisteredEmailShouldThrowException() {
        user.setLocale(new Locale("ua"));
        when(repository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        assertThrows(EmailAlreadyExistException.class, () -> userService.createUser(new UserRegistration(user)),
                "User with this email already exist");
    }

    @Test
    void updateUserEmailSuccessfullyShouldSaveAndReturnUser() {
        WebUser newUser = new WebUser(0, new BasicUser(), "email2", "password2", true,
                false, 0, null, USER_ROLE, new Locale("ua"));
        newUser.setId(1);
        when(repository.findByEmail(user.getEmail())).thenReturn(Optional.empty());
        when(repository.findById(user.getId())).thenReturn(Optional.of(user));
        WebUser updatedUser = new WebUser(0, new BasicUser(), "email2", ENCODED_PASSWORD, true,
                false, 0, null, USER_ROLE, new Locale("ua"));
        when(repository.save(updatedUser)).thenReturn(newUser);

        UserDto result = userService.updateUserEmail(newUser.getEmail(), modelMapper.map(user, UserDto.class));

        assertEquals(modelMapper.map(newUser, UserDto.class), result);
    }

    @Test
    void updateUserEmailThatDidNotChangedInfoShouldThrowException() {
        WebUser newUser = new WebUser(0, new BasicUser(), "email", "pass", false,
                false, 0, null, USER_ROLE, new Locale("ua"));
        when(repository.findById(1)).thenReturn(Optional.of(user));

        assertThrows(UserDataNotChangedException.class, () -> userService
                        .updateUserEmail(newUser.getEmail(), modelMapper.map(user, UserDto.class)),
                "User didn't changed during update operation");
    }

    @Test
    void updateUserEmailWithRegisteredEmailShouldThrowException() {
        WebUser newUser = new WebUser(0, new BasicUser(), "email2", "password2", false,
                false, 0, null, USER_ROLE, new Locale("ua"));
        when(repository.findByEmail(newUser.getEmail())).thenReturn(Optional.of(new WebUser()));
        when(repository.findById(1)).thenReturn(Optional.of(user));

        assertThrows(EmailAlreadyExistException.class, () -> userService
                        .updateUserEmail(newUser.getEmail(), modelMapper.map(user, UserDto.class)),
                "User with this email already exist");
    }

    @Test
    void updateUserPasswordSuccessfullyShouldSaveAndReturnUser() {
        WebUser newUser = new WebUser(0, new BasicUser(), "email2", "password2", true,
                false, 0, null, USER_ROLE, new Locale("ua"));
        newUser.setId(1);
        when(repository.findByEmail(user.getEmail())).thenReturn(Optional.empty());
        when(repository.findById(user.getId())).thenReturn(Optional.of(user));
        WebUser updatedUser = new WebUser(0, new BasicUser(), "email", newUser.getPassword(), true,
                false, 0, null, USER_ROLE, new Locale("ua"));
        when(repository.save(updatedUser)).thenReturn(updatedUser);
        when(encoder.encode(CharBuffer.wrap(newUser.getPassword()))).thenReturn(newUser.getPassword());

        UserDto result = userService.updateUserPassword(newUser.getPassword().toCharArray(), modelMapper.map(user, UserDto.class));

        assertEquals(modelMapper.map(updatedUser, UserDto.class), result);
    }

    @Test
    void updateUserPasswordThatDidNotChangedInfoShouldThrowException() {
        WebUser newUser = new WebUser(0, new BasicUser(), "email", "pass", false,
                false, 0, null, USER_ROLE, new Locale("ua"));
        user.setId(1);
        when(repository.findById(1)).thenReturn(Optional.of(user));
        when(encoder.matches(CharBuffer.wrap(newUser.getPassword()), user.getPassword())).thenReturn(true);
        when(encoder.encode(CharBuffer.wrap(newUser.getPassword()))).thenReturn(newUser.getPassword());

        assertThrows(UserDataNotChangedException.class, () -> userService
                        .updateUserPassword("pass".toCharArray(), modelMapper.map(user, UserDto.class)),
                "User didn't changed during update operation");
    }

    @Test
    void addGameToUserListOfGamesShouldCallAddToListRepositoryMethod() {
        BasicUser basicUser = BasicUser.builder().id(2).build();
        user.setBasicUser(basicUser);
        when(repository.findById(1)).thenReturn(Optional.of(user));

        userService.subscribeToGame(1, 3);

        verify(userRepository).addGameToUserListOfGames(2, 3);
    }

    @Test
    void removeGameFromUserListOfGamesShouldCallRemoveFromListRepositoryMethod() {
        BasicUser basicUser = BasicUser.builder().id(2).build();
        user.setBasicUser(basicUser);
        when(repository.findById(1)).thenReturn(Optional.of(user));

        userService.unsubscribeFromGame(1, 1);

        verify(userRepository).removeGameFromUserListOfGames(2, 1);
    }

    @Test
    void loginUserSuccessfullyShouldReturnUser() {
        Credentials credentials = new Credentials("email", "pass".toCharArray());
        WebUser user = new WebUser(0, new BasicUser(), "email", "pass", true, false,
                0, null, USER_ROLE, new Locale("ua"));
        when(repository.findByEmail(credentials.getEmail())).thenReturn(Optional.of(user));
        when(encoder.matches(CharBuffer.wrap(credentials.getPassword()), user.getPassword()))
                .thenReturn(true);

        UserDto result = userService.login(credentials);

        assertEquals(modelMapper.map(user, UserDto.class), result);
    }

    @Test
    void loginUserWithWrongEmailShouldThrowException() {
        Credentials credentials = new Credentials("email", "pass".toCharArray());
        when(repository.findByEmail(credentials.getEmail())).thenReturn(Optional.empty());

        assertThrows(LoginFailedException.class, () -> userService.login(new Credentials("email", "pass".toCharArray())),
                "Invalid email or password");
    }

    @Test
    void loginWithNotActivatedEmailShouldThrowException() {
        user.setActivated(false);
        Credentials credentials = new Credentials("email", "pass".toCharArray());
        when(repository.findByEmail(credentials.getEmail())).thenReturn(Optional.of(user));

        assertThrows(UserNotActivatedException.class, () -> userService.login(new Credentials("email", "pass".toCharArray())),
                "Invalid email or password");
    }

    @Test
    void loginUserWithWithWrongPasswordShouldThrowExceptionAndIncreaseFailedAttempt() {
        Credentials credentials = new Credentials("email", "pass".toCharArray());
        WebUser newUser = new WebUser(0, new BasicUser(), "email", "pass", true,
                false, 0, null, USER_ROLE, new Locale("ua"));
        when(repository.findByEmail(credentials.getEmail())).thenReturn(Optional.of(newUser));


        assertThrows(LoginFailedException.class, () -> userService.login(new Credentials("email", "pass".toCharArray())),
                "Invalid email or password");
        verify(repository)
                .save(new WebUser(0, new BasicUser(), "email", "pass", true, false,
                        1, null, USER_ROLE, new Locale("ua")));
    }

    @Test
    void loginUserWithWrongPasswordAndTooManyFailedAttemptsShouldThrowExceptionAndLockUser() {
        Credentials credentials = new Credentials("email", "pass".toCharArray());
        user.setFailedAttempt(4);
        when(repository.findByEmail(credentials.getEmail())).thenReturn(Optional.of(user));


        assertThrows(LoginFailedException.class, () -> userService
                        .login(new Credentials("email", "pass".toCharArray())),
                "Invalid email or password");
        verify(repository)
                .save(new WebUser(0, new BasicUser(), "email", "pass", true, true,
                        5, any(), USER_ROLE, new Locale("ua")));
    }

    @Test
    void loginUserSuccessfullyThatWasLockedShouldUnlockAndReturnUser() {
        Credentials credentials = new Credentials("email", "pass".toCharArray());
        Calendar lockTime = Calendar.getInstance();
        lockTime.setTime(new Date());
        lockTime.add(Calendar.DATE, -2);
        WebUser user = new WebUser(0, new BasicUser(), "email", "pass", true, true,
                5, lockTime.getTime(), USER_ROLE, new Locale("ua"));
        when(repository.findByEmail(credentials.getEmail())).thenReturn(Optional.of(user));
        when(encoder.matches(CharBuffer.wrap(credentials.getPassword()), user.getPassword()))
                .thenReturn(true);

        UserDto result = userService.login(credentials);

        assertEquals(modelMapper.map(user, UserDto.class), result);
        verify(repository, times(1))
                .save(new WebUser(0, new BasicUser(), "email", "pass", true, false,
                        0, null, USER_ROLE, new Locale("ua")));
    }

    @Test
    void loginLockedUserShouldThrowException() {
        Credentials credentials = new Credentials("email", "pass".toCharArray());
        Calendar lockTime = Calendar.getInstance();
        lockTime.setTime(new Date());
        WebUser user = new WebUser(0, new BasicUser(), "email", "pass", true, true,
                5, lockTime.getTime(), USER_ROLE, new Locale("ua"));
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

        assertEquals(modelMapper.map(user, UserDto.class), result);
    }

    @Test
    void getWebUserByEmailShouldReturnWebUser() {
        when(repository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        WebUser result = userService.getWebUserByEmail(user.getEmail());

        assertEquals(user, result);
    }

    @Test
    void getWebUserByEmailShouldTrowException() {
        when(repository.findByEmail(user.getEmail()))
                .thenThrow(new NotFoundException("app.user.error.email.not.found"));

        assertThrows(NotFoundException.class, () -> userService.getWebUserByEmail(user.getEmail()),
                "app.user.error.email.not.found");
    }

    @Test
    void getUserByEmailNotExistingEmailShouldThrowException() {
        when(repository.findByEmail("email")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.getUserByEmail(user.getEmail()),
                "User with email 'email' not found");
    }

    @Test
    void updateLocaleShouldUpdateLocale() {
        when(repository.findById(1)).thenReturn(Optional.of(user));
        String locale = "en";
        user.setLocale(new Locale(locale));

        userService.updateLocale(locale, 1);

        verify(repository).save(user);
    }

    @Test
    void connectTelegramUserShouldUpdateWebUserData() {
        String token = "token";
        long webUserId = 123;
        Set<Game> gameList = new HashSet<>();
        gameList.add(new Game());
        Set<UserNotificationType> notificationTypes = new HashSet<>();
        notificationTypes.add(UserNotificationType.TELEGRAM);
        WebMessengerConnector connector = new WebMessengerConnector();
        connector.setUserId(456);
        BasicUser user = BasicUser.builder()
                .id(456)
                .gameList(gameList)
                .notificationTypes(notificationTypes).build();
        user.setId(456);

        Set<Game> gameListWeb = new HashSet<>();
        gameList.add(new Game());
        Set<UserNotificationType> notificationTypesWeb = new HashSet<>();
        notificationTypes.add(UserNotificationType.TELEGRAM);
        BasicUser oldUser = BasicUser.builder()
                .id(456)
                .gameList(gameList)
                .notificationTypes(notificationTypes).build();
        user.setId(456);
        WebUser webUser = WebUser.builder()
                .basicUser(oldUser)
                .build();
        webUser.setId(webUserId);

        when(connectorRepository.findById(token)).thenReturn(Optional.of(connector));
        when(repository.findById(webUserId)).thenReturn(Optional.of(webUser));
        when(userRepository.findById(connector.getUserId())).thenReturn(user);


        userService.connectTelegramUser(token, webUserId);


        verify(repository).save(webUser);
        verify(connectorRepository).deleteById(token);
        verify(userRepository).deleteById(user.getId());

        assertTrue(webUser.getBasicUser().getGameList().containsAll(user.getGameList()));
        assertTrue(webUser.getBasicUser().getNotificationTypes().containsAll(user.getNotificationTypes()));
    }

    @Test
    void testGetTelegramUserConnectorTokenShouldReturnToken() {
        long webUserId = 123;
        String expectedToken = "mockedToken";
        WebMessengerConnector connector = mock(WebMessengerConnector.class);
        when(connector.getToken()).thenReturn(expectedToken);
        when(connectorRepository.save(any(WebMessengerConnector.class))).thenReturn(connector);
        when(repository.findById(webUserId)).thenReturn(Optional.of(user));


        String result = userService.getTelegramUserConnectorToken(webUserId);


        assertEquals(expectedToken, result);
    }
}