package com.gpb.telegram.controller.impl;

import com.gpb.telegram.service.TelegramUserService;
import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GetWebConnectorTokenControllerTest {

    TelegramUserService telegramUserService = mock(TelegramUserService.class);

    GetWebConnectorTokenController controller = new GetWebConnectorTokenController(telegramUserService);

    @Test
    void testApply_shouldReturnTokenToNeededChat() {
        long userId = 123456;
        Update update = new Update();
        Message message = new Message();
        User user = new User();

        update.setMessage(message);
        message.setFrom(user);
        user.setId(userId);

        String token = "mockToken";
        when(telegramUserService.getWebUserConnectorToken(userId)).thenReturn(token);


        SendMessage sendMessage = controller.apply("chatId", update);


        assertEquals("chatId", sendMessage.getChatId());
        assertEquals(token, sendMessage.getText());
    }
}