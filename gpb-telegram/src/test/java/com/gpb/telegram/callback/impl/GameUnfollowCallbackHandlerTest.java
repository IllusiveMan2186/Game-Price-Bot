package com.gpb.telegram.callback.impl;

import com.gpb.telegram.bean.TelegramRequest;
import com.gpb.telegram.bean.TelegramResponse;
import com.gpb.telegram.bean.TelegramUser;
import com.gpb.telegram.service.GameService;
import com.gpb.telegram.util.UpdateCreator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GameUnfollowCallbackHandlerTest {

    @Mock
    GameService gameService;
    @Mock
    MessageSource messageSource;
    @InjectMocks
    GameUnfollowCallbackHandler callbackHandler;

    @Test
    void testApply_whenSuccessfully_shouldReturnResponse() {
        String chatId = "123456";
        int gameId = 12;
        Locale locale = new Locale("");
        TelegramUser user = TelegramUser.builder().basicUserId(123456L).build();
        Update update = UpdateCreator.getUpdateWithCallback("/subscribe " + gameId, Long.parseLong(chatId));
        TelegramRequest request = TelegramRequest.builder().update(update).user(user).locale(locale).build();
        when(messageSource.getMessage("game.unsubscribe.success.message", null, locale)).thenReturn("message");


        TelegramResponse response = callbackHandler.apply(request);


        verify(gameService).setFollowGameOption(gameId, request.getUserId(), false);
        assertEquals("SendMessage(chatId=123456, messageThreadId=null, text=message, parseMode=null, " +
                "disableWebPagePreview=null, disableNotification=null, replyToMessageId=null, replyMarkup=null, " +
                "entities=null, allowSendingWithoutReply=null, protectContent=null)", response.getMessages().get(0).toString());
    }
}