package com.gpb.telegram.controller.impl;

import com.gpb.telegram.service.TelegramUserService;
import org.junit.jupiter.api.Test;
import org.springframework.context.MessageSource;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ChangeLocaleControllerTest {

    TelegramUserService telegramUserService = mock(TelegramUserService.class);

    MessageSource messageSource = mock(MessageSource.class);

    ChangeLocaleController controller = new ChangeLocaleController(messageSource, telegramUserService);

    @Test
    void testGetDescription_shouldReturnDescription() {
        Locale locale = new Locale("");
        when(messageSource.getMessage("change.language.command.description", null, locale))
                .thenReturn("messages");
        String description = controller.getDescription(locale);

        assertEquals("messages", description);
    }

    @Test
    void testApply_shouldReturnSuccessMessage() {
        Locale locale = new Locale("");
        long userId = 123456;
        Update update = new Update();
        Message message = new Message();
        User user = new User();

        update.setMessage(message);
        message.setFrom(user);
        message.setText("/changeLanguage language");
        user.setId(userId);

        Locale newLocale = new Locale("new");
        when(telegramUserService.changeUserLocale(userId,new Locale("language"))).thenReturn(newLocale);
        when(messageSource.getMessage("change.language.command.successfully.message", null, newLocale))
                .thenReturn("messages");


        SendMessage sendMessage = controller.apply("chatId", update, locale);


        assertEquals("chatId", sendMessage.getChatId());
        assertEquals("messages", sendMessage.getText());
    }
}