package com.gpb.telegram.command.impl;

import com.gpb.telegram.bean.TelegramRequest;
import com.gpb.telegram.bean.TelegramResponse;
import com.gpb.telegram.service.TelegramUserService;
import com.gpb.telegram.util.UpdateCreator;
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

class ChangeLocaleCommandHandlerTest {

    TelegramUserService telegramUserService = mock(TelegramUserService.class);

    MessageSource messageSource = mock(MessageSource.class);

    ChangeLocaleCommandHandler controller = new ChangeLocaleCommandHandler(messageSource, telegramUserService);

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
        Update update = UpdateCreator.getUpdateWithoutCallback("/changeLanguage language", 123);
        Locale newLocale = new Locale("new");
        TelegramRequest request = TelegramRequest.builder().update(update).locale(locale).build();
        when(telegramUserService.changeUserLocale(123456, new Locale("language"))).thenReturn(newLocale);
        when(messageSource.getMessage("change.language.command.successfully.message", null, newLocale))
                .thenReturn("messages");


        TelegramResponse response = controller.apply(request);
        SendMessage sendMessage = (SendMessage) response.getMessages().get(0);

        assertEquals("123", sendMessage.getChatId());
        assertEquals("messages", sendMessage.getText());
    }
}