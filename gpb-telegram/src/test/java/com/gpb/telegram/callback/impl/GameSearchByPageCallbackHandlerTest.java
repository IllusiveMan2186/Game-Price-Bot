package com.gpb.telegram.callback.impl;

import com.gpb.telegram.bean.TelegramRequest;
import com.gpb.telegram.bean.TelegramResponse;
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

import java.util.Collections;
import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GameSearchByPageCallbackHandlerTest {
    @Mock
    GameService gameService;
    @Mock
    MessageSource messageSource;
    @Mock
    GameListMapper gameListMapper;
    @InjectMocks
    GameSearchByPageCallbackHandler controller;

    @Test
    void apply_ShouldReturnGameList_WhenGamesFound() {
        String chatId = "123456";
        String gameName = "exampleGame";
        int pageNum = 1;
        Update update = UpdateCreator.getUpdateWithCallback("/searchByPage " + pageNum + " " + gameName, Long.parseLong(chatId));
        TelegramRequest request = TelegramRequest.builder().update(update).locale(Locale.ENGLISH).user(null).build();
        GameListPageDto page = new GameListPageDto(1, List.of(new GameDto()));
        SendMessage message = new SendMessage();

        when(gameService.getByName(gameName, pageNum)).thenReturn(page);
        when(gameListMapper.gameSearchListToTelegramPage(page.getGames(), request, page.getElementAmount(), pageNum, gameName))
                .thenReturn(Collections.singletonList(message));


        TelegramResponse response = controller.apply(request);


        assertEquals(message, response.getMessages().get(0));
        verify(gameService, times(1)).getByName(gameName, pageNum);
    }

    @Test
    void apply_ShouldReturnErrorMessage_WhenNoGamesFound() {
        String chatId = "123456";
        GameListPageDto emptyPage = new GameListPageDto(0, Collections.emptyList());
        String gameName = "exampleGame";
        String errorMessage = "message";
        int pageNum = 1;
        SendMessage message = new SendMessage(chatId, errorMessage);
        Update update = UpdateCreator.getUpdateWithCallback("/searchByPage " + pageNum + " " + gameName,
                Long.parseLong(chatId));
        TelegramRequest request = TelegramRequest.builder().update(update).locale(Locale.ENGLISH).user(null).build();

        when(gameService.getByName(gameName, pageNum)).thenReturn(emptyPage);
        when(messageSource.getMessage("game.search.not.found.game", null, Locale.ENGLISH))
                .thenReturn(errorMessage);


        TelegramResponse response = controller.apply(request);


        assertEquals(message, response.getMessages().get(0));
        verify(gameService, times(1)).getByName(gameName, pageNum);
    }
}