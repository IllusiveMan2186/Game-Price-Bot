package com.gpb.telegram.controller.impl;

import com.gpb.telegram.controller.TelegramController;
import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;


class HelpControllerTest {

    Map<String, TelegramController> controllerMap
            = Collections.singletonMap("synchronizeToWeb", new SynchronizeToWebUserController(null));

    HelpController controller = new HelpController(controllerMap);

    @Test
    void testGetDescription_shouldReturnDescription() {
        String description = controller.getDescription();

        assertEquals(" - help command", description);
    }

    @Test
    void testApply_shouldReturnMessageAndSSynchronizeAccounts() {
        Update update = new Update();
        Message message = new Message();

        update.setMessage(message);
        message.setText("/synchronizeToWeb mockToken");


        SendMessage sendMessage = controller.apply("chatId", update);


        assertEquals("chatId", sendMessage.getChatId());
        assertEquals("You could use one of available commands:" + System.lineSeparator() +
                "/synchronizeToWeb {token} - synchronize telegram with web part by token", sendMessage.getText());
    }
}