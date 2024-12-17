package com.gpb.telegram.callback.impl;

import com.gpb.telegram.bean.TelegramRequest;
import com.gpb.telegram.bean.TelegramResponse;
import com.gpb.telegram.bean.TelegramUser;
import com.gpb.telegram.bean.game.GameInfoDto;
import com.gpb.telegram.mapper.GameInfoMapper;
import com.gpb.telegram.service.GameService;
import com.gpb.telegram.util.UpdateCreator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GameInfoCallbackHandlerTest {
    @Mock
    GameService gameService;
    @Mock
    GameInfoMapper gameInfoMapper;
    @InjectMocks
    GameInfoCallbackHandler callbackHandler;

    @Test
    void testApply_whenSuccess_shouldReturnCorrectMessage() {
        String chatId = "123456";
        int gameId = 12;
        Locale locale = new Locale("");
        List<PartialBotApiMethod> partialBotApiMethodList = new ArrayList<>();
        GameInfoDto game = new GameInfoDto();
        Update update = UpdateCreator.getUpdateWithCallback("/gameInfo " + gameId, Long.parseLong(chatId));
        TelegramUser user = TelegramUser.builder().basicUserId(123456L).build();
        when(gameService.getById(gameId, 123456L)).thenReturn(game);
        TelegramRequest request = TelegramRequest.builder().update(update).user(user).locale(locale).user(user).build();
        when(gameInfoMapper.gameInfoToTelegramPage(game, request)).thenReturn(partialBotApiMethodList);


        TelegramResponse response = callbackHandler.apply(request);


        assertEquals(partialBotApiMethodList, response.getMessages());
    }
}