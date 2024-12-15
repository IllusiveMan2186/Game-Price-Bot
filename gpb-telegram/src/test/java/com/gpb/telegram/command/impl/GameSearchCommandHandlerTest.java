package com.gpb.telegram.command.impl;

import com.gpb.telegram.bean.TelegramRequest;
import com.gpb.telegram.bean.TelegramResponse;
import com.gpb.telegram.bean.TelegramUser;
import com.gpb.telegram.bean.game.GameDto;
import com.gpb.telegram.bean.game.GameListPageDto;
import com.gpb.telegram.mapper.GameListMapper;
import com.gpb.telegram.service.GameService;
import com.gpb.telegram.util.UpdateCreator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GameSearchCommandHandlerTest {

    @Mock
    GameService gameService;
    @Mock
    MessageSource messageSource;
    @Mock
    GameListMapper gameListMapper;
    @InjectMocks
    GameSearchCommandHandler controller;

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
        List<GameDto> games = Collections.singletonList(new GameDto());
        TelegramUser user = new TelegramUser();
        TelegramRequest request = TelegramRequest.builder().update(update).locale(locale).user(user).build();
        GameListPageDto page = GameListPageDto.builder()
                .games(games)
                .elementAmount(gameAmount)
                .build();
        when(gameService.getByName(name, 1)).thenReturn(page);
        when(gameListMapper.gameSearchListToTelegramPage(games, request, gameAmount, 1, name))
                .thenReturn(Collections.singletonList(message));


        TelegramResponse response = controller.apply(request);


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
        TelegramRequest request = TelegramRequest.builder()
                .update(update)
                .locale(locale)
                .build();
        GameListPageDto page = GameListPageDto.builder()
                .games(new ArrayList<>())
                .elementAmount(0)
                .build();
        when(messageSource.getMessage("game.search.not.found.game", null, locale)).thenReturn(errorMessage);
        when(gameService.getByName(name, 1)).thenReturn(page);


        TelegramResponse response = controller.apply(request);


        assertEquals(message, response.getMessages().get(0));
    }
}