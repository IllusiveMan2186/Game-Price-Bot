package com.gpb.telegram.callback.impl;

import com.gpb.telegram.bean.Game;
import com.gpb.telegram.bean.TelegramRequest;
import com.gpb.telegram.bean.TelegramResponse;
import com.gpb.telegram.service.GameService;
import com.gpb.telegram.service.GameStoresService;
import com.gpb.telegram.service.TelegramUserService;
import com.gpb.telegram.util.UpdateCreator;
import org.junit.jupiter.api.Test;
import org.springframework.context.MessageSource;
import org.telegram.telegrambots.meta.api.objects.Update;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class GameFollowCallbackHandlerTest {

    GameService gameService = mock(GameService.class);
    GameStoresService storesService = mock(GameStoresService.class);
    TelegramUserService telegramUserService = mock(TelegramUserService.class);
    MessageSource messageSource = mock(MessageSource.class);
    GameFollowCallbackHandler callbackHandler = new GameFollowCallbackHandler(telegramUserService, gameService, storesService, messageSource);

    @Test
    void testApply_whenGameAlreadyFollowedByAnotherUser_shouldReturnResponseWithoutCallingStoreService() {
        String chatId = "123456";
        int gameId = 12;
        Locale locale = new Locale("");
        Game game = Game.builder().name("name").isFollowed(true).build();
        Update update = UpdateCreator.getUpdateWithCallback("/subscribe " + gameId, Long.parseLong(chatId));
        when(gameService.getById(gameId)).thenReturn(game);
        when(messageSource.getMessage("game.subscribe.success.message", null, locale)).thenReturn("message");
        TelegramRequest request = TelegramRequest.builder().update(update).locale(locale).build();


        TelegramResponse response = callbackHandler.apply(request);


        verify(telegramUserService).subscribeToGame(123456, gameId);
        verify(storesService, times(0)).subscribeToGame(gameId);
        assertEquals("SendMessage(chatId=123456, messageThreadId=null, text=messagename, parseMode=null, " +
                "disableWebPagePreview=null, disableNotification=null, replyToMessageId=null, replyMarkup=null, " +
                "entities=null, allowSendingWithoutReply=null, protectContent=null)", response.getMessages().get(0).toString());
    }

    @Test
    void testApply_whenGameNotFollowedByAnotherUser_shouldReturnResponseWithCallingStoreService() {
        String chatId = "123456";
        int gameId = 12;
        Locale locale = new Locale("");
        Game game = Game.builder().name("name").isFollowed(false).build();
        Update update = UpdateCreator.getUpdateWithCallback("/subscribe " + gameId, Long.parseLong(chatId));
        when(gameService.getById(gameId)).thenReturn(game);
        when(messageSource.getMessage("game.subscribe.success.message", null, locale)).thenReturn("message");
        TelegramRequest request = TelegramRequest.builder().update(update).locale(locale).build();


        TelegramResponse response = callbackHandler.apply(request);


        verify(telegramUserService).subscribeToGame(123456, gameId);
        verify(storesService).subscribeToGame(gameId);
        assertEquals("SendMessage(chatId=123456, messageThreadId=null, text=messagename, parseMode=null, " +
                "disableWebPagePreview=null, disableNotification=null, replyToMessageId=null, replyMarkup=null, " +
                "entities=null, allowSendingWithoutReply=null, protectContent=null)", response.getMessages().get(0).toString());
    }
}