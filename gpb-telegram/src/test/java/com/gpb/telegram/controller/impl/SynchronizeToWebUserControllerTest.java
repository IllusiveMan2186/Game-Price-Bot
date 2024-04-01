package com.gpb.telegram.controller.impl;

import com.gpb.telegram.service.TelegramUserService;
import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SynchronizeToWebUserControllerTest {

    private static final String SUCCESSFULLY_CONNECTED = "Successfully connected";

    TelegramUserService telegramUserService = mock(TelegramUserService.class);

    SynchronizeToWebUserController controller = new SynchronizeToWebUserController(telegramUserService);

    @Test
    void testApply_shouldReturnMessageAndSSynchronizeAccounts() {
        long userId = 123456;
        Update update = new Update();
        Message message = new Message();
        User user = new User();

        update.setMessage(message);
        message.setFrom(user);
        message.setText("/synchronizeToWeb mockToken");
        user.setId(userId);

        String token = "mockToken";
        when(telegramUserService.getWebUserConnectorToken(userId)).thenReturn(token);


        SendMessage sendMessage = controller.apply("chatId", update);


        verify(telegramUserService).synchronizeTelegramUser(token, userId);
        assertEquals("chatId", sendMessage.getChatId());
        assertEquals(SUCCESSFULLY_CONNECTED, sendMessage.getText());
    }
}