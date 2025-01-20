package com.gpb.telegram.service.impl;

import com.gpb.common.entity.user.NotificationRequestDto;
import com.gpb.common.entity.user.UserNotificationType;
import com.gpb.common.service.RestTemplateHandlerService;
import com.gpb.telegram.entity.TelegramUser;
import com.gpb.telegram.repository.TelegramUserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpMethod;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TelegramUserServiceImplTest {

    @Mock
    TelegramUserRepository telegramUserRepository;

    @Mock
    RestTemplateHandlerService restTemplateHandler;

    @InjectMocks
    TelegramUserServiceImpl telegramUserService;

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
        Long basicUserId = 1L;
        when(telegramUserRepository.save(newUser)).thenReturn(newUser);
        when(restTemplateHandler.executeRequestWithBody(
                "/user",
                HttpMethod.POST,
                null,
                new NotificationRequestDto(UserNotificationType.TELEGRAM),
                Long.class))
                .thenReturn(basicUserId);


        TelegramUser createdUser = telegramUserService.createTelegramUser(newUser);


        assertEquals(createdUser, newUser);
    }

    @Test
    void testChangeUserLocale_whenSuccess_shouldReturnNewLocale() {
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
    void testGetUserLocale_whenSuccess_shouldReturnUserLocale() {
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

    @Test
    void testGetUserById_whenSuccess_shouldReturnUser() {
        long telegramId = 123;
        TelegramUser telegramUser = new TelegramUser();
        when(telegramUserRepository.findByTelegramId(telegramId)).thenReturn(telegramUser);


        TelegramUser result = telegramUserService.getUserById(telegramId);


        assertEquals(telegramUser, result);
    }

    @Test
    void testGetByBasicUserId_whenSuccess_shouldReturnUser() {
        long basicUserId = 123;
        TelegramUser telegramUser = new TelegramUser();
        when(telegramUserRepository.findByBasicUserId(basicUserId)).thenReturn(telegramUser);


        TelegramUser result = telegramUserService.getByBasicUserId(basicUserId);


        assertEquals(telegramUser, result);
    }

    @Test
    void testSetBasicUserId_whenSuccess_shouldCAllRepositoryMethod() {
        long currentBasicUserId = 1L;
        long newBasicUserId = 1L;


        telegramUserService.setBasicUserId(currentBasicUserId, newBasicUserId);


        verify(telegramUserRepository).updateBasicUserIdByBasicUserId(currentBasicUserId, newBasicUserId);
    }
}