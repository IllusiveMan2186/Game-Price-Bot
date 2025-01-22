package com.gpb.telegram.command.impl;

import com.gpb.telegram.entity.TelegramRequest;
import com.gpb.telegram.entity.TelegramResponse;
import com.gpb.telegram.util.UpdateCreator;
import org.junit.jupiter.api.Test;
import org.springframework.context.MessageSource;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GetWebUrlCommandHandlerTest {

    MessageSource messageSource = mock(MessageSource.class);

    GetWebUrlCommandHandler controller = new GetWebUrlCommandHandler(messageSource);

    @Test
    void testGetDescription_whenSuccess_shouldReturnDescription() {
        Locale locale = new Locale("");
        when(messageSource.getMessage("get.web.url.description", null, locale))
                .thenReturn("messages");
        String description = controller.getDescription(locale);

        assertEquals("messages", description);
    }

    @Test
    void testApply_whenSuccess_shouldReturnUrl() {
        String chatId = "123456";
        String url = "http://mocked-url.com";
        Update update = UpdateCreator.getUpdateWithoutCallback("/url ", Long.parseLong(chatId));
        TelegramRequest request = TelegramRequest.builder()
                .update(update)
                .build();

        controller.setFrontendServiceUrl(url);


        TelegramResponse response = controller.apply(request);


        SendMessage message = (SendMessage) response.getMessages().get(0);
        assertEquals(chatId, message.getChatId());
        assertEquals(url, message.getText());
    }
}