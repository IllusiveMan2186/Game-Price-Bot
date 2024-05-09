package com.gpb.telegram.callback.impl;

import com.gpb.telegram.bean.Game;
import com.gpb.telegram.bean.TelegramResponse;
import com.gpb.telegram.service.GameService;
import com.gpb.telegram.service.GameStoresService;
import com.gpb.telegram.service.TelegramUserService;
import com.gpb.telegram.util.UpdateCreator;
import org.junit.jupiter.api.Test;
import org.springframework.context.MessageSource;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.ArrayList;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class GameUnfollowCallbackHandlerTest {

    GameService gameService = mock(GameService.class);
    GameStoresService storesService = mock(GameStoresService.class);
    TelegramUserService telegramUserService = mock(TelegramUserService.class);
    MessageSource messageSource = mock(MessageSource.class);
    GameUnfollowCallbackHandler callbackHandler = new GameUnfollowCallbackHandler(telegramUserService, gameService, storesService, messageSource);

    @Test
    void testApply_whenGameAlreadyFollowedByAnotherUser_shouldReturnResponseWithoutCallingStoreService() {
        String chatId = "123456";
        int gameId = 12;
        Locale locale = new Locale("");
        Game game = Game.builder().name("name").isFollowed(false).build();
        Update update = UpdateCreator.getUpdateWithCallback("/subscribe " + gameId, Long.parseLong(chatId));
        when(gameService.getById(gameId)).thenReturn(game);
        when(messageSource.getMessage("game.unsubscribe.success.message", null, locale)).thenReturn("message");


        TelegramResponse response = callbackHandler.apply(chatId, update, locale);


        verify(telegramUserService).unsubscribeFromGame(123456, gameId);
        verify(storesService, times(0)).unsubscribeFromGame(gameId);
        assertEquals("SendMessage(chatId=123456, messageThreadId=null, text=messagename, parseMode=null, " +
                "disableWebPagePreview=null, disableNotification=null, replyToMessageId=null, replyMarkup=null, " +
                "entities=null, allowSendingWithoutReply=null, protectContent=null)", response.getMessages().get(0).toString());
    }

    @Test
    void testApply_whenGameNotFollowedByAnotherUser_shouldReturnResponseWithCallingStoreService() {
        String chatId = "123456";
        int gameId = 12;
        Locale locale = new Locale("");
        Game game = Game.builder().name("name").userList(new ArrayList<>()).isFollowed(true).build();
        Update update = UpdateCreator.getUpdateWithCallback("/subscribe " + gameId, Long.parseLong(chatId));
        when(gameService.getById(gameId)).thenReturn(game);
        when(messageSource.getMessage("game.unsubscribe.success.message", null, locale)).thenReturn("message");


        TelegramResponse response = callbackHandler.apply(chatId, update, locale);


        verify(telegramUserService).unsubscribeFromGame(123456, gameId);
        verify(storesService).unsubscribeFromGame(gameId);
        assertEquals("SendMessage(chatId=123456, messageThreadId=null, text=messagename, parseMode=null, " +
                "disableWebPagePreview=null, disableNotification=null, replyToMessageId=null, replyMarkup=null, " +
                "entities=null, allowSendingWithoutReply=null, protectContent=null)", response.getMessages().get(0).toString());
    }
}