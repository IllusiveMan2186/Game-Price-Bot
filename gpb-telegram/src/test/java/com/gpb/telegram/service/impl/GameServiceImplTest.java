package com.gpb.telegram.service.impl;

import com.gpb.common.entity.game.GameInfoDto;
import com.gpb.common.entity.game.GameListPageDto;
import com.gpb.common.service.BasicGameService;
import com.gpb.common.service.RestTemplateHandlerService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpMethod;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
    void testGetByName() {
        String name = "TestGame";
        int pageNum = 1;
        GameListPageDto mockResponse = new GameListPageDto();

        String url = "/game/name/" + name + "?pageSize=" + 2 + "&pageNum=" + pageNum + "&sortBy=name-ASC";

        when(restTemplateHandler.executeRequest(url, HttpMethod.GET, null, GameListPageDto.class)).thenReturn(mockResponse);

        GameListPageDto result = gameService.getByName(name, pageNum);

        assertEquals(mockResponse, result);
        verify(restTemplateHandler).executeRequest(url, HttpMethod.GET, null, GameListPageDto.class);
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
