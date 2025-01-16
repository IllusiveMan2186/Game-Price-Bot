package com.gpb.telegram.service.impl;

import com.gpb.common.entity.game.GameDto;
import com.gpb.common.entity.game.GameListPageDto;
import com.gpb.telegram.entity.TelegramRequest;
import com.gpb.telegram.entity.TelegramResponse;
import com.gpb.telegram.entity.TelegramUser;
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
class CommonRequestHandlerServiceImplTest {

    @Mock
    GameService gameService;
    @Mock
    MessageSource messageSource;
    @Mock
    GameListMapper gameListMapper;
    @InjectMocks
    CommonRequestHandlerServiceImpl commonRequestHandlerService;

    @Test
    void testProcessUserGameListRequest_whenGamesFound_shouldReturnGameList() {
        String chatId = "123456";
        int pageNum = 1;
        TelegramUser user = TelegramUser.builder().basicUserId(123456L).build();
        Update update = UpdateCreator.getUpdateWithCallback("/userGameList " + pageNum, Long.parseLong(chatId));
        TelegramRequest request = TelegramRequest.builder().update(update).locale(Locale.ENGLISH).user(user).build();
        GameListPageDto page = new GameListPageDto(1, List.of(new GameDto()));
        SendMessage message = new SendMessage();

        when(gameService.getUserGames(123456L, pageNum)).thenReturn(page);
        when(gameListMapper.userGameListToTelegramPage(page.getGames(), request, page.getElementAmount(), pageNum))
                .thenReturn(Collections.singletonList(message));


        TelegramResponse response = commonRequestHandlerService.processUserGameListRequest(request, pageNum);


        assertEquals(message, response.getMessages().get(0));
        verify(gameService, times(1)).getUserGames(123456L, pageNum);
    }

    @Test
    void testProcessUserGameListRequest_whenNoGamesFound_shouldReturnErrorMessage() {
        String chatId = "123456";
        GameListPageDto emptyPage = new GameListPageDto(0, Collections.emptyList());
        String errorMessage = "message";
        int pageNum = 1;
        SendMessage message = new SendMessage(chatId, errorMessage);
        Update update = UpdateCreator.getUpdateWithCallback("/userGameList " + pageNum,
                Long.parseLong(chatId));
        TelegramUser user = TelegramUser.builder().basicUserId(123456L).build();
        TelegramRequest request = TelegramRequest.builder().update(update).locale(Locale.ENGLISH).user(user).build();

        when(gameService.getUserGames(123456L, pageNum)).thenReturn(emptyPage);
        when(messageSource.getMessage("user.game.list.empty", null, Locale.ENGLISH))
                .thenReturn(errorMessage);


        TelegramResponse response = commonRequestHandlerService.processUserGameListRequest(request, pageNum);


        assertEquals(message, response.getMessages().get(0));
        verify(gameService, times(1)).getUserGames(123456L, pageNum);
    }

    @Test
    void testProcessGameListRequest_whenGamesFound_shouldReturnGameList() {
        String chatId = "123456";
        int pageNum = 1;
        String sort = "sort";
        TelegramUser user = TelegramUser.builder().basicUserId(123456L).build();
        Update update = UpdateCreator.getUpdateWithCallback("/gameList " + pageNum, Long.parseLong(chatId));
        TelegramRequest request = TelegramRequest.builder().update(update).locale(Locale.ENGLISH).user(user).build();
        GameListPageDto page = new GameListPageDto(1, List.of(new GameDto()));
        SendMessage message = new SendMessage();

        when(gameService.getGameList(pageNum, sort)).thenReturn(page);
        when(gameListMapper.gameListToTelegramPage(page.getGames(), request, page.getElementAmount(), pageNum, sort))
                .thenReturn(Collections.singletonList(message));


        TelegramResponse response = commonRequestHandlerService.processGameListRequest(request, pageNum, sort);


        assertEquals(message, response.getMessages().get(0));
        verify(gameService, times(1)).getGameList(pageNum, sort);
    }

    @Test
    void testProcessGameListRequest_whenNoGamesFound_shouldReturnErrorMessage() {
        String chatId = "123456";
        GameListPageDto emptyPage = new GameListPageDto(0, Collections.emptyList());
        String errorMessage = "message";
        int pageNum = 1;
        String sort = "sort";
        SendMessage message = new SendMessage(chatId, errorMessage);
        Update update = UpdateCreator.getUpdateWithCallback("/gameList " + pageNum,
                Long.parseLong(chatId));
        TelegramUser user = TelegramUser.builder().basicUserId(123456L).build();
        TelegramRequest request = TelegramRequest.builder().update(update).locale(Locale.ENGLISH).user(user).build();

        when(gameService.getGameList(pageNum, sort)).thenReturn(emptyPage);
        when(messageSource.getMessage("game.list.not.found.game", null, Locale.ENGLISH))
                .thenReturn(errorMessage);


        TelegramResponse response = commonRequestHandlerService.processGameListRequest(request, pageNum, sort);


        assertEquals(message, response.getMessages().get(0));
        verify(gameService, times(1)).getGameList(pageNum, sort);
    }
}