package com.gpb.telegram.filter.impl;

import com.gpb.telegram.entity.TelegramRequest;
import com.gpb.telegram.entity.TelegramUser;
import com.gpb.telegram.service.TelegramUserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.Locale;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserExistingFilterTest {

    @Mock
    TelegramUserService telegramUserService;
    @InjectMocks
    UserExistingFilter filter;

    @Test
    void testApply_whenUserIsRegistered_shouldNotCreateNewUser() {
        long userId = 123456;
        Update update = new Update();
        Message message = new Message();
        User user = new User();

        update.setMessage(message);
        message.setFrom(user);
        user.setId(userId);

        when(telegramUserService.isUserRegistered(userId)).thenReturn(true);
        TelegramUser expectedUser = TelegramUser.builder().telegramId(userId).build();
        TelegramRequest request = TelegramRequest.builder().update(update).build();


        filter.checkFilter(request);


        verify(telegramUserService, times(0)).createTelegramUser(expectedUser);
    }

    @Test
    void testApply_whenUserIsNotRegistered_shouldCreateUser() {
        long userId = 123456;
        Update update = new Update();
        Message message = new Message();
        User user = new User();

        update.setMessage(message);
        message.setFrom(user);
        user.setId(userId);
        user.setLanguageCode("");
        Locale locale = new Locale("");
        when(telegramUserService.isUserRegistered(userId)).thenReturn(false);
        TelegramUser expectedUser = TelegramUser.builder()
                .telegramId(userId)
                .locale(locale)
                .build();
        TelegramRequest request = TelegramRequest.builder().update(update).locale(locale).build();


        filter.checkFilter(request);


        verify(telegramUserService, times(1)).createTelegramUser(expectedUser);
    }
}