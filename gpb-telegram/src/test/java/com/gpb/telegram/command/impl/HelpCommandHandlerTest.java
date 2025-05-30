package com.gpb.telegram.command.impl;

import com.gpb.telegram.command.CommandHandler;
import com.gpb.telegram.entity.TelegramRequest;
import com.gpb.telegram.entity.TelegramResponse;
import com.gpb.telegram.util.UpdateCreator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.MessageSource;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Collections;
import java.util.Locale;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class HelpCommandHandlerTest {

    @Mock
    MessageSource messageSource = mock(MessageSource.class);
    Map<String, CommandHandler> controllerMap;

    @InjectMocks
    HelpCommandHandler controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        controllerMap = Collections.singletonMap("synchronizeToWeb", new GetWebConnectorTokenCommandHandler(messageSource, null));
        controller = new HelpCommandHandler(messageSource, controllerMap);
    }

    @Test
    void testGetDescription_whenSuccess_shouldReturnDescription() {
        Locale locale = new Locale("");
        when(messageSource.getMessage("help.command.description", null, locale))
                .thenReturn("messages");
        String description = controller.getDescription(locale);

        assertEquals("messages", description);
    }

    @Test
    void testApply_whenSuccess_shouldReturnMessageAndSSynchronizeAccounts() {
        Locale locale = new Locale("");
        when(messageSource.getMessage("help.menu.header.message", null, locale))
                .thenReturn("messages");
        when(messageSource.getMessage("accounts.synchronization.get.token.description", null, locale))
                .thenReturn("- description");

        Update update = UpdateCreator.getUpdateWithoutCallback("/synchronizeToWeb mockToken", 123);
        TelegramRequest request = TelegramRequest.builder().update(update).locale(locale).build();


        TelegramResponse response = controller.apply(request);


        SendMessage sendMessage = (SendMessage) response.getMessages().get(0);
        assertEquals("123", sendMessage.getChatId());
        assertEquals("messages" + System.lineSeparator() +
                "/synchronizeToWeb - description", sendMessage.getText());
    }
}