package com.gpb.telegram.controller.impl;

import com.gpb.telegram.controller.TelegramController;
import org.junit.jupiter.api.Test;
import org.springframework.context.MessageSource;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Collections;
import java.util.Locale;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


class HelpControllerTest {

    MessageSource messageSource = mock(MessageSource.class);
    Map<String, TelegramController> controllerMap
            = Collections.singletonMap("synchronizeToWeb", new SynchronizeToWebUserController(messageSource, null));
    HelpController controller = new HelpController(controllerMap, messageSource);

    @Test
    void testGetDescription_shouldReturnDescription() {
        Locale locale = new Locale("");
        when(messageSource.getMessage("help.command.description", null, locale))
                .thenReturn("messages");
        String description = controller.getDescription(locale);

        assertEquals("messages", description);
    }

    @Test
    void testApply_shouldReturnMessageAndSSynchronizeAccounts() {
        Locale locale = new Locale("");
        when(messageSource.getMessage("help.menu.header.message", null, locale))
                .thenReturn("messages");
        when(messageSource.getMessage("accounts.synchronization.description", null, locale))
                .thenReturn(" - description");

        Update update = new Update();
        Message message = new Message();

        update.setMessage(message);
        message.setText("/synchronizeToWeb mockToken");


        SendMessage sendMessage = controller.apply("chatId", update, locale);


        assertEquals("chatId", sendMessage.getChatId());
        assertEquals("messages" + System.lineSeparator() +
                "/synchronizeToWeb - description", sendMessage.getText());
    }
}