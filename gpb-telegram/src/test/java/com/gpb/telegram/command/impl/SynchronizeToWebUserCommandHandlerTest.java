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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SynchronizeToWebUserCommandHandlerTest {

    TelegramUserService telegramUserService = mock(TelegramUserService.class);
    MessageSource messageSource = mock(MessageSource.class);
    SynchronizeToWebUserCommandHandler controller = new SynchronizeToWebUserCommandHandler(messageSource, telegramUserService);

    @Test
    void testGetDescription_shouldReturnDescription() {
        Locale locale = new Locale("");
        when(messageSource.getMessage("accounts.synchronization.description", null, locale))
                .thenReturn("messages");
        String description = controller.getDescription(locale);

        assertEquals("messages", description);
    }

    @Test
    void testApply_shouldReturnMessageAndSSynchronizeAccounts() {
        Locale locale = new Locale("");
        long userId = 123456;
        Update update = UpdateCreator.getUpdateWithoutCallback("/synchronizeToWeb mockToken",123);
        when(messageSource.getMessage("accounts.synchronization.token.connected.message", null, locale))
                .thenReturn("messages");

        String token = "mockToken";
        TelegramRequest request = TelegramRequest.builder().update(update).locale(locale).build();
        when(telegramUserService.getWebUserConnectorToken(userId)).thenReturn(token);


        TelegramResponse response = controller.apply(request);


        SendMessage sendMessage = (SendMessage) response.getMessages().get(0);
        verify(telegramUserService).synchronizeTelegramUser(token, userId);
        assertEquals("123", sendMessage.getChatId());
        assertEquals("messages", sendMessage.getText());
    }
}