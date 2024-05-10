package com.gpb.telegram.callback.impl;

import com.gpb.telegram.bean.Game;
import com.gpb.telegram.bean.TelegramRequest;
import com.gpb.telegram.bean.TelegramResponse;
import com.gpb.telegram.bean.TelegramUser;
import com.gpb.telegram.mapper.GameInfoMapper;
import com.gpb.telegram.service.GameService;
import com.gpb.telegram.service.TelegramUserService;
import com.gpb.telegram.util.UpdateCreator;
import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GameInfoCallbackHandlerTest {

    GameService gameService = mock(GameService.class);
    GameInfoMapper gameInfoMapper = mock(GameInfoMapper.class);
    GameInfoCallbackHandler callbackHandler = new GameInfoCallbackHandler(gameService, gameInfoMapper);

    @Test
    void testApply_shouldReturnCorrectMessage() {
        String chatId = "123456";
        int gameId = 12;
        Locale locale = new Locale("");
        TelegramUser user = new TelegramUser();
        Game game = new Game();
        List<PartialBotApiMethod> partialBotApiMethodList = new ArrayList<>();
        Update update = UpdateCreator.getUpdateWithCallback("/gameInfo " + gameId, Long.parseLong(chatId));
        when(gameService.getById(gameId)).thenReturn(game);
        TelegramRequest request = TelegramRequest.builder().update(update).locale(locale).user(user).build();
        when(gameInfoMapper.gameInfoToTelegramPage(game, request)).thenReturn(partialBotApiMethodList);


        TelegramResponse response = callbackHandler.apply(request);


        assertEquals(partialBotApiMethodList, response.getMessages());
    }
}