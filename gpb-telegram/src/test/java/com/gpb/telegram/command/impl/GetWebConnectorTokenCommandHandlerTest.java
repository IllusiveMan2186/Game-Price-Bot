package com.gpb.telegram.command.impl;

import com.gpb.telegram.bean.TelegramRequest;
import com.gpb.telegram.bean.TelegramResponse;
import com.gpb.telegram.bean.TelegramUser;
import com.gpb.telegram.service.UserLinkerService;
import com.gpb.telegram.util.UpdateCreator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.MessageSource;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class GetWebConnectorTokenCommandHandlerTest {

    @Mock
    UserLinkerService userLinkerService;
    @Mock
    MessageSource messageSource;

    @InjectMocks
    GetWebConnectorTokenCommandHandler controller ;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        controller = new GetWebConnectorTokenCommandHandler(messageSource, userLinkerService);
    }

    @Test
    void testGetDescription_whenSuccess_shouldReturnDescription() {
        Locale locale = new Locale("");
        when(messageSource.getMessage("accounts.synchronization.get.token.description", null, locale))
                .thenReturn("messages");
        String description = controller.getDescription(locale);

        assertEquals("messages", description);
    }

    @Test
    void testApply_whenSuccess_shouldReturnTokenToNeededChat() {
        Locale locale = new Locale("");
        long userId = 123456;
        Update update = UpdateCreator.getUpdateWithoutCallback("", 123);
        String token = "mockToken";
        TelegramUser user = TelegramUser.builder().basicUserId(123456L).build();
        TelegramRequest request = TelegramRequest.builder().update(update).user(user).locale(locale).build();
        when(userLinkerService.getAccountsLinkerToken(userId)).thenReturn(token);


        TelegramResponse response = controller.apply(request);


        SendMessage sendMessage = (SendMessage) response.getMessages().get(0);
        assertEquals("123", sendMessage.getChatId());
        assertEquals(token, sendMessage.getText());
    }
}