package com.gpb.telegram.service.impl;

import com.gpb.common.entity.game.GameInfoDto;
import com.gpb.common.entity.game.GameListPageDto;
import com.gpb.common.service.BasicGameService;
import com.gpb.common.service.RestTemplateHandlerService;
import com.gpb.common.util.CommonConstants;
import com.gpb.telegram.util.Constants;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GameServiceImplTest {

    @Mock
    private RestTemplateHandlerService restTemplateHandler;

    @Mock
    private BasicGameService basicGameService;

    @InjectMocks
    private GameServiceImpl gameService;

    @Test
    void testGetById_whenSuccess_shouldReturnGameInfoDto() {
        long gameId = 1L;
        long userId = 123L;
        GameInfoDto mockResponse = new GameInfoDto();


        when(basicGameService.getById(gameId, userId)).thenReturn(mockResponse);

        GameInfoDto result = gameService.getById(gameId, userId);

        assertEquals(mockResponse, result);
        verify(basicGameService).getById(gameId, userId);
    }

    @Test
    void testGetByName_whenSuccess_shouldReturnGameListPageDto() {
        String name = "TestGame";
        int pageNum = 1;
        long basicUserId = 1L;
        GameListPageDto mockResponse = new GameListPageDto();
        String sort = CommonConstants.NAME_SORT_PARAM + "-" + CommonConstants.SORT_DIRECTION_ASCENDING;


        when(basicGameService.getByName(name, Constants.GAMES_AMOUNT_IN_LIST, pageNum, sort, basicUserId))
                .thenReturn(mockResponse);

        GameListPageDto result = gameService.getByName(name, pageNum, basicUserId);

        assertEquals(mockResponse, result);
        verify(basicGameService).getByName(name, Constants.GAMES_AMOUNT_IN_LIST, pageNum, sort, basicUserId);
    }

    @Test
    void testGetGameList_whenSuccess_shouldReturnGameListPageDto() {
        String name = "TestGame";
        int pageNum = 1;
        long basicUserId = 1L;
        GameListPageDto mockResponse = new GameListPageDto();
        String sort = "name-ASC";

        String url = "/game/name/" + name + "?pageSize=" + 2 + "&pageNum=" + pageNum + "&sortBy=name-ASC";

        when(basicGameService.getByGenre(
                any(ArrayList.class),
                any(ArrayList.class),
                eq(Constants.GAMES_AMOUNT_IN_LIST),
                eq(pageNum),
                eq(new BigDecimal(Constants.GAMES_MIN_PRICE)),
                eq(new BigDecimal(Constants.GAMES_MAX_PRICE)),
                eq(sort),
                eq(basicUserId))).thenReturn(mockResponse);

        GameListPageDto result = gameService.getGameList(pageNum, sort, basicUserId);

        assertEquals(mockResponse, result);
        verify(basicGameService).getByGenre(
                any(ArrayList.class),
                any(ArrayList.class),
                eq(Constants.GAMES_AMOUNT_IN_LIST),
                eq(pageNum),
                eq(new BigDecimal(Constants.GAMES_MIN_PRICE)),
                eq(new BigDecimal(Constants.GAMES_MAX_PRICE)),
                eq(sort),
                eq(basicUserId));
    }

    @Test
    void testGetUserGame_whenSuccess_shouldReturnGameListPageDto() {
        int pageNum = 1;
        GameListPageDto mockResponse = new GameListPageDto();
        String sort = CommonConstants.PRICE_SORT_PARAM + "-" + CommonConstants.SORT_DIRECTION_ASCENDING;
        long basicUserId = 1L;

        when(basicGameService.getUserGames(basicUserId, Constants.GAMES_AMOUNT_IN_LIST, pageNum, sort))
                .thenReturn(mockResponse);

        GameListPageDto result = gameService.getUserGames(basicUserId, pageNum);

        assertEquals(mockResponse, result);
        verify(basicGameService).getUserGames(basicUserId, Constants.GAMES_AMOUNT_IN_LIST, pageNum, sort);
    }

    @Test
    void testSetFollowGameOption_whenSuccess_shouldCallBasicGameService() {
        long gameId = 1L;
        long userId = 123L;
        boolean isFollow = true;

        gameService.setFollowGameOption(gameId, userId, isFollow);

        verify(basicGameService).setFollowGameOption(gameId, userId, isFollow);
    }
}
