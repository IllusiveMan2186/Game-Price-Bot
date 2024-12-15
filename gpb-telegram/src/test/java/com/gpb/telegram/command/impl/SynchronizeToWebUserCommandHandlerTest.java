package com.gpb.telegram.command.impl;

import com.gpb.telegram.bean.TelegramRequest;
import com.gpb.telegram.bean.TelegramResponse;
import com.gpb.telegram.bean.TelegramUser;
import com.gpb.telegram.service.UserLinkerService;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SynchronizeToWebUserCommandHandlerTest {

    @Mock
    MessageSource messageSource;
    @Mock
    UserLinkerService userLinkerService;
    @InjectMocks
    SynchronizeToWebUserCommandHandler controller;

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
        TelegramUser user = TelegramUser.builder().basicUserId(123456L).build();
        TelegramRequest request = TelegramRequest.builder().update(update).user(user).locale(locale).build();


        TelegramResponse response = controller.apply(request);


        SendMessage sendMessage = (SendMessage) response.getMessages().get(0);
        verify(userLinkerService).linkAccounts(token, userId);
        assertEquals("123", sendMessage.getChatId());
        assertEquals("messages", sendMessage.getText());
    }
}