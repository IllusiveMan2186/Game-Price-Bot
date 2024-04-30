package com.gpb.telegram.service.impl;

import com.gpb.telegram.bean.BasicUser;
import com.gpb.telegram.bean.Game;
import com.gpb.telegram.bean.TelegramUser;
import com.gpb.telegram.bean.UserNotificationType;
import com.gpb.telegram.bean.WebMessengerConnector;
import com.gpb.telegram.repository.TelegramUserRepository;
import com.gpb.telegram.repository.UserRepository;
import com.gpb.telegram.repository.WebMessengerConnectorRepository;
import com.gpb.telegram.service.TelegramUserService;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class TelegramUserServiceImplTest {

    TelegramUserRepository telegramUserRepository = mock(TelegramUserRepository.class);

    UserRepository userRepository = mock(UserRepository.class);

    WebMessengerConnectorRepository connectorRepository = mock(WebMessengerConnectorRepository.class);

    TelegramUserService telegramUserService = new TelegramUserServiceImpl(telegramUserRepository, connectorRepository,
            userRepository);

    @Test
    void testIsUserRegistered_whenUserExists_shouldReturnTrue() {
        long telegramId = 123;
        when(telegramUserRepository.existsByTelegramId(telegramId)).thenReturn(true);


        boolean isRegistered = telegramUserService.isUserRegistered(telegramId);


        assertTrue(isRegistered);
    }

    @Test
    void testIsUserRegistered_whenUserDoesNotExist_shouldReturnFalse() {
        long telegramId = 456;
        when(telegramUserRepository.existsByTelegramId(telegramId)).thenReturn(false);


        boolean isRegistered = telegramUserService.isUserRegistered(telegramId);


        assertFalse(isRegistered);
    }

    @Test
    void testCreateTelegramUser_shouldReturnCreatedUser() {
        TelegramUser newUser = new TelegramUser();
        when(telegramUserRepository.save(newUser)).thenReturn(newUser);


        TelegramUser createdUser = telegramUserService.createTelegramUser(newUser);


        assertEquals(createdUser, newUser);
    }

    @Test
    void testConnectTelegramUser_whenSuccessfully_shouldUpdateWebUserData() {
        String token = "token";
        long telegramId = 123;
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

        gameList.add(new Game());
        notificationTypes.add(UserNotificationType.TELEGRAM);
        TelegramUser telegramUser = TelegramUser.builder()
                .basicUser(user)
                .build();

        when(connectorRepository.findById(token)).thenReturn(Optional.of(connector));
        when(telegramUserRepository.findByTelegramId(telegramId)).thenReturn(telegramUser);
        when(userRepository.findById(connector.getUserId())).thenReturn(user);


        telegramUserService.synchronizeTelegramUser(token, telegramId);


        verify(telegramUserRepository).save(telegramUser);
        verify(connectorRepository).deleteById(token);
        verify(userRepository).deleteById(user.getId());

        assertTrue(telegramUser.getBasicUser().getGameList().containsAll(user.getGameList()));
        assertTrue(telegramUser.getBasicUser().getNotificationTypes().containsAll(user.getNotificationTypes()));
    }

    @Test
    void testGetTelegramUserConnectorToken_whenSuccessfully_shouldReturnToken() {
        long telegramId = 123;
        String expectedToken = "mockedToken";
        BasicUser user = BasicUser.builder().id(456).build();
        TelegramUser telegramUser = TelegramUser.builder()
                .id(1)
                .basicUser(user)
                .build();
        when(telegramUserRepository.findByTelegramId(telegramId)).thenReturn(telegramUser);
        WebMessengerConnector connector = mock(WebMessengerConnector.class);
        when(connector.getToken()).thenReturn(expectedToken);
        when(connectorRepository.save(any(WebMessengerConnector.class))).thenReturn(connector);


        String result = telegramUserService.getWebUserConnectorToken(telegramId);


        assertEquals(expectedToken, result);
    }

    @Test
    void testChangeUserLocale_whenSuccessfully_shouldReturnNewLocale() {
        long telegramId = 123;
        Locale locale = new Locale("");
        TelegramUser telegramUser = TelegramUser.builder()
                .id(1)
                .locale(locale)
                .build();
        when(telegramUserRepository.findByTelegramId(telegramId)).thenReturn(telegramUser);
        Locale newLocale = new Locale("new");
        telegramUser.setLocale(newLocale);
        when(telegramUserRepository.save(telegramUser)).thenReturn(telegramUser);


        Locale result = telegramUserService.changeUserLocale(telegramId, newLocale);


        assertEquals(newLocale, result);
    }

    @Test
    void testGetUserLocale_whenSuccessfully_shouldReturnUserLocale() {
        long telegramId = 123;
        Locale locale = new Locale("");
        TelegramUser telegramUser = TelegramUser.builder()
                .id(1)
                .locale(locale)
                .build();
        when(telegramUserRepository.findByTelegramId(telegramId)).thenReturn(telegramUser);


        Locale result = telegramUserService.getUserLocale(telegramId);


        assertEquals(locale, result);
    }
}