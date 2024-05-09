package com.gpb.telegram.command.impl;

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

class GameSearchCommandHandlerTest {

    GameService gameService = mock(GameService.class);
    TelegramUserService telegramUserService = mock(TelegramUserService.class);
    MessageSource messageSource = mock(MessageSource.class);
    GameListMapper gameListMapper = mock(GameListMapper.class);

    GameSearchCommandHandler controller = new GameSearchCommandHandler(gameService, telegramUserService, messageSource, gameListMapper);

    @Test
    void testGetDescription_shouldReturnDescription() {
        Locale locale = new Locale("");
        when(messageSource.getMessage("game.search.command.description", null, locale))
                .thenReturn("messages");
        String description = controller.getDescription(locale);

        assertEquals("messages", description);
    }

    @Test
    void testApply_whenGamesFound_shouldReturnCorrectMessage() {
        String chatId = "123456";
        String name = "Some Game Name 2";
        long gameAmount = 1;
        Locale locale = new Locale("");
        SendMessage message = new SendMessage();
        Update update = UpdateCreator.getUpdateWithoutCallback("/search " + name, Long.parseLong(chatId));
        List<Game> games = Collections.singletonList(new Game());
        TelegramUser user = new TelegramUser();
        when(gameService.getByName(name, 1)).thenReturn(games);
        when(gameService.getGameAmountByName(name)).thenReturn(gameAmount);
        when(telegramUserService.getUserById(123456)).thenReturn(user);
        when(gameListMapper.gameSearchListToTelegramPage(games, user, gameAmount, chatId, 1, name, locale))
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
        SendMessage message = new SendMessage(chatId, errorMessage);
        Update update = UpdateCreator.getUpdateWithoutCallback("/search " + name, Long.parseLong(chatId));
        when(messageSource.getMessage("game.search.not.found.game", null, locale)).thenReturn(errorMessage);
        when(gameService.getByName(name, 1)).thenReturn(new ArrayList<>());


        TelegramResponse response = controller.apply(chatId, update, locale);


        assertEquals(message, response.getMessages().get(0));
    }
}