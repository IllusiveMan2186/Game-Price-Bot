package com.gpb.telegram.command.impl;

import com.gpb.common.service.UserLinkerService;
import com.gpb.telegram.configuration.ResourceConfiguration;
import com.gpb.telegram.entity.TelegramRequest;
import com.gpb.telegram.entity.TelegramResponse;
import com.gpb.telegram.entity.TelegramUser;
import com.gpb.telegram.util.Constants;
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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetWebUrlCommandHandlerTest {

    @Mock
    ResourceConfiguration resourceConfiguration;
    @Mock
    MessageSource messageSource = mock(MessageSource.class);
    @Mock
    UserLinkerService userLinkerService = mock(UserLinkerService.class);

    @InjectMocks
    GetWebUrlCommandHandler controller;

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
        String token = "token";
        long basicUserId = 123L;
        Update update = UpdateCreator.getUpdateWithoutCallback("/url ", Long.parseLong(chatId));
        TelegramUser user = TelegramUser.builder()
                .basicUserId(basicUserId)
                .build();
        TelegramRequest request = TelegramRequest.builder()
                .update(update)
                .user(user)
                .build();
        when(userLinkerService.getAccountsLinkerToken(basicUserId)).thenReturn(token);

        when(resourceConfiguration.getFrontendServiceUrl()).thenReturn(url);


        TelegramResponse response = controller.apply(request);


        SendMessage message = (SendMessage) response.getMessages().get(0);
        assertEquals(chatId, message.getChatId());
        assertEquals(url + Constants.SET_LINK_TOKEN + token, message.getText());
    }
}