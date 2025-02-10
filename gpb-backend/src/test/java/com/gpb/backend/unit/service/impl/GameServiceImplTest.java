package com.gpb.backend.unit.service.impl;

import com.gpb.backend.service.impl.GameServiceImpl;
import com.gpb.common.entity.game.AddGameInStoreDto;
import com.gpb.common.entity.game.GameInfoDto;
import com.gpb.common.entity.game.GameListPageDto;
import com.gpb.common.entity.game.Genre;
import com.gpb.common.entity.game.ProductType;
import com.gpb.common.service.BasicGameService;
import com.gpb.common.service.RestTemplateHandlerService;
import com.gpb.common.util.CommonConstants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpMethod;
import org.springframework.kafka.core.KafkaTemplate;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class GameServiceImplTest {

    private KafkaTemplate<String, Long> kafkaTemplate = mock(KafkaTemplate.class);

    private KafkaTemplate<String, AddGameInStoreDto> addGameInStoreDtoKafkaTemplate = mock(KafkaTemplate.class);


    private RestTemplateHandlerService restTemplateHandler = mock(RestTemplateHandlerService.class);


    private BasicGameService basicGameService = mock(BasicGameService.class);

    private GameServiceImpl gameService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        gameService = new GameServiceImpl(kafkaTemplate, addGameInStoreDtoKafkaTemplate, restTemplateHandler, basicGameService);
    }

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
        int pageSize = 10;
        GameListPageDto mockResponse = new GameListPageDto();
        String sort = CommonConstants.NAME_SORT_PARAM + "-" + CommonConstants.SORT_DIRECTION_ASCENDING;


        when(basicGameService.getByName(name, pageSize, pageNum, sort, 0))
                .thenReturn(mockResponse);

        GameListPageDto result = gameService.getByName(name, pageSize, pageNum, sort);

        assertEquals(mockResponse, result);
        verify(basicGameService).getByName(name, pageSize, pageNum, sort, 0);
    }

    @Test
    void testGetByUrl_whenSuccess_shouldReturnGameInfoDto() {
        String url = "/game/genre?pageSize=10&pageNum=1&minPrice=0&" +
                "maxPrice=100&sortBy=name-ASC&genre=ACTION&type=GAME";
        GameInfoDto mockResponse = new GameInfoDto();

        String serverUrl = "/game/url?url=" + url;

        when(restTemplateHandler.executeRequest(serverUrl, HttpMethod.GET, null, GameInfoDto.class))
                .thenReturn(mockResponse);

        GameInfoDto result = gameService.getByUrl(url);

        assertEquals(mockResponse, result);
        String expectedUrl = "/game/url?url=/game/genre?pageSize=10&pageNum=1" +
                "&minPrice=0&maxPrice=100&sortBy=name-ASC&genre=ACTION&type=GAME";
        verify(restTemplateHandler).executeRequest(expectedUrl, HttpMethod.GET, null, GameInfoDto.class);
    }

    @Test
    void testAddGameInStore_whenSuccess_shouldMakeKafkaCall() {
        AddGameInStoreDto addGameInStoreDto = new AddGameInStoreDto();

        gameService.addGameInStore(addGameInStoreDto);

        verify(addGameInStoreDtoKafkaTemplate)
                .send(eq(CommonConstants.GAME_IN_STORE_ADD_TOPIC), anyString(), eq(addGameInStoreDto));
    }

    @Test
    void testSetFollowGameOption_whenSuccess_shouldCallBasicGameService() {
        long gameId = 1L;
        long userId = 123L;
        boolean isFollow = true;

        gameService.setFollowGameOption(gameId, userId, isFollow);

        verify(basicGameService).setFollowGameOption(gameId, userId, isFollow);
    }

    @Test
    void testGetUserGames_whenSuccess_shouldCallRequest() {
        long userId = 123L;
        int pageSize = 10;
        int pageNum = 1;
        String sort = "name-ASC";
        GameListPageDto mockResponse = new GameListPageDto();
        when(basicGameService.getUserGames(userId, pageSize, pageNum, sort)).thenReturn(mockResponse);


        GameListPageDto result = gameService.getUserGames(userId, pageSize, pageNum, sort);


        assertEquals(mockResponse, result);
        verify(basicGameService).getUserGames(userId, pageSize, pageNum, sort);
    }

    @Test
    void testRemoveGame_whenSuccess_shouldSendRemoveGameEvent() {
        long gameId = 1L;
        String expectedTopic = CommonConstants.GAME_REMOVE_TOPIC;

        gameService.removeGame(gameId);

        verify(kafkaTemplate).send(eq(expectedTopic), anyString(), eq(gameId));
    }

    @Test
    void testRemoveGameGameInStore_whenSuccess_shouldSendRemoveGameInStoreEvent() {
        long gameInStoreId = 1L;

        gameService.removeGameInStore(gameInStoreId);

        verify(kafkaTemplate).send(eq(CommonConstants.GAME_IN_STORE_REMOVE_TOPIC), anyString(), eq(gameInStoreId));
    }

    @Test
    void testGetByGenre_whenSuccess_shouldCallBasicServiceAndReturnGameListPageDto() {
        List<Genre> genres = List.of(Genre.ACTION);
        List<ProductType> types = List.of(ProductType.GAME);
        int pageSize = 10;
        int pageNum = 1;
        BigDecimal minPrice = BigDecimal.ZERO;
        BigDecimal maxPrice = BigDecimal.valueOf(100);
        String sort = "name-ASC";
        GameListPageDto mockResponse = new GameListPageDto();

        when(basicGameService.getByGenre(genres, types, pageSize, pageNum, minPrice, maxPrice, sort, 0))
                .thenReturn(mockResponse);

        GameListPageDto result = gameService.getByGenre(genres, types, pageSize, pageNum, minPrice, maxPrice, sort);

        assertEquals(mockResponse, result);
        verify(basicGameService)
                .getByGenre(genres, types, pageSize, pageNum, minPrice, maxPrice, sort, 0);
    }
}
