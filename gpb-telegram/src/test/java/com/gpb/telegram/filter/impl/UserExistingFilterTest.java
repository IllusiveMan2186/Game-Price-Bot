package com.gpb.telegram.filter.impl;

import com.gpb.telegram.bean.TelegramUser;
import com.gpb.telegram.service.TelegramUserService;
import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
class UserExistingFilterTest {

    private static final String SUCCESSFULLY_CONNECTED = "Successfully connected";

    TelegramUserService telegramUserService = mock(TelegramUserService.class);

    UserExistingFilter filter = new UserExistingFilter(telegramUserService);

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


        filter.checkFilter(update);


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

        when(telegramUserService.isUserRegistered(userId)).thenReturn(false);
        TelegramUser expectedUser = TelegramUser.builder().telegramId(userId).build();


        filter.checkFilter(update);


        verify(telegramUserService, times(1)).createTelegramUser(expectedUser);
    }
}