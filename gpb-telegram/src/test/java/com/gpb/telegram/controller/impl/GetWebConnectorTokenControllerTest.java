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

class GetWebConnectorTokenControllerTest {

    TelegramUserService telegramUserService = mock(TelegramUserService.class);

    MessageSource messageSource = mock(MessageSource.class);

    GetWebConnectorTokenController controller = new GetWebConnectorTokenController(messageSource, telegramUserService);

    @Test
    void testGetDescription_shouldReturnDescription() {
        Locale locale = new Locale("");
        when(messageSource.getMessage("accounts.synchronization.get.token.description", null, locale))
                .thenReturn("messages");
        String description = controller.getDescription(locale);

        assertEquals("messages", description);
    }

    @Test
    void testApply_shouldReturnTokenToNeededChat() {
        Locale locale = new Locale("");
        long userId = 123456;
        Update update = new Update();
        Message message = new Message();
        User user = new User();

        update.setMessage(message);
        message.setFrom(user);
        user.setId(userId);

        String token = "mockToken";
        when(telegramUserService.getWebUserConnectorToken(userId)).thenReturn(token);


        SendMessage sendMessage = controller.apply("chatId", update, locale);


        assertEquals("chatId", sendMessage.getChatId());
        assertEquals(token, sendMessage.getText());
    }
}