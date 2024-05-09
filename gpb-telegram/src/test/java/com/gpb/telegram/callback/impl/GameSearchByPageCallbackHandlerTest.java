package com.gpb.telegram.callback.impl;

import com.gpb.telegram.bean.Game;
import com.gpb.telegram.bean.TelegramResponse;
import com.gpb.telegram.bean.TelegramUser;
import com.gpb.telegram.mapper.GameListMapper;
import com.gpb.telegram.service.GameService;
import com.gpb.telegram.service.TelegramUserService;
import com.gpb.telegram.util.UpdateCreator;
import org.junit.jupiter.api.Test;
import org.springframework.context.MessageSource;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GameSearchByPageCallbackHandlerTest {
    GameService gameService = mock(GameService.class);
    TelegramUserService telegramUserService = mock(TelegramUserService.class);
    MessageSource messageSource = mock(MessageSource.class);
    GameListMapper gameListMapper = mock(GameListMapper.class);

    GameSearchByPageCallbackHandler controller = new GameSearchByPageCallbackHandler(gameService, telegramUserService,
            messageSource, gameListMapper);

    @Test
    void testApply_whenGamesFound_shouldReturnCorrectMessage() {
        String chatId = "123456";
        String name = "Some Game Name 2";
        long gameAmount = 1;
        int pageNum = 12;
        Locale locale = new Locale("");
        SendMessage message = new SendMessage();
        TelegramUser user = new TelegramUser();
        Update update = UpdateCreator.getUpdateWithCallback("/searchByPage " + pageNum + " " + name, Long.parseLong(chatId));
        List<Game> games = Collections.singletonList(new Game());
        when(telegramUserService.getUserById(123456)).thenReturn(user);
        when(gameService.getByName(name, pageNum)).thenReturn(games);
        when(gameService.getGameAmountByName(name)).thenReturn(gameAmount);
        when(gameListMapper.gameSearchListToTelegramPage(games, user, gameAmount, chatId, pageNum, name, locale))
                .thenReturn(Collections.singletonList(message));


        TelegramResponse response = controller.apply(chatId, update, locale);


        assertEquals(message, response.getMessages().get(0));
    }

    @Test
    void testApply_whenGamesNotFound_shouldGamesNotFoundMessage() {
        String chatId = "123456";
        String name = "Some Game Name 2";
        Locale locale = new Locale("");
        String errorMessage = "message";
        int pageNum = 12;
        SendMessage message = new SendMessage(chatId, errorMessage);
        Update update = UpdateCreator.getUpdateWithCallback("/searchByPage " + pageNum + " " + name, Long.parseLong(chatId));
        when(messageSource.getMessage("game.search.not.found.game", null, locale)).thenReturn(errorMessage);
        when(gameService.getByName(name, pageNum)).thenReturn(new ArrayList<>());


        TelegramResponse response = controller.apply(chatId, update, locale);


        assertEquals(message, response.getMessages().get(0));
    }
}