package com.gpb.telegram.command.impl;

import com.gpb.telegram.bean.TelegramRequest;
import com.gpb.telegram.bean.TelegramResponse;
import com.gpb.telegram.service.TelegramUserService;
import com.gpb.telegram.util.UpdateCreator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChangeLocaleCommandHandlerTest {

    @Mock
    TelegramUserService telegramUserService;
    @Mock
    MessageSource messageSource;
    @InjectMocks
    ChangeLocaleCommandHandler controller;

    @Test
    void testGetDescription_whenSuccess_shouldReturnDescription() {
        Locale locale = new Locale("");
        when(messageSource.getMessage("change.language.command.description", null, locale))
                .thenReturn("messages");
        String description = controller.getDescription(locale);

        assertEquals("messages", description);
    }

    @Test
    void testApply_whenSuccess_shouldReturnSuccessMessage() {
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