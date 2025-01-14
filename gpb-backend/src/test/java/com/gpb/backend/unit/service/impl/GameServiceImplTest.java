package com.gpb.backend.unit.service.impl;

import com.gpb.backend.service.impl.GameServiceImpl;
import com.gpb.common.entity.game.GameInfoDto;
import com.gpb.common.entity.game.GameListPageDto;
import com.gpb.common.entity.game.Genre;
import com.gpb.common.entity.game.ProductType;
import com.gpb.common.service.BasicGameService;
import com.gpb.common.service.RestTemplateHandlerService;
import com.gpb.common.util.CommonConstants;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.kafka.core.KafkaTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GameServiceImplTest {

    @Mock
    private KafkaTemplate<String, Long> kafkaTemplate;

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
        int pageSize = 10;
        int pageNum = 1;
        String sort = "gamesInShop.price-ASC";
        GameListPageDto mockResponse = new GameListPageDto();

        String url = "/game/name/" + name + "?pageSize=" + pageSize + "&pageNum=" + pageNum + "&sortBy=gamesInShop.price-ASC";

        when(restTemplateHandler.executeRequest(url, HttpMethod.GET, null, GameListPageDto.class)).thenReturn(mockResponse);

        GameListPageDto result = gameService.getByName(name, pageSize, pageNum, sort);

        assertEquals(mockResponse, result);
        verify(restTemplateHandler).executeRequest(url, HttpMethod.GET, null, GameListPageDto.class);
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
    void testSetFollowGameOption_whenSuccess_shouldCallBasicGameService() {
        long gameId = 1L;
        long userId = 123L;
        boolean isFollow = true;

        gameService.setFollowGameOption(gameId, userId, isFollow);

        verify(basicGameService).setFollowGameOption(gameId, userId, isFollow);
    }

    @Test
    void testGetUserGames_whenSuccess_shouldCallReqeust() {
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
        String expectedTopic = CommonConstants.GAME_IN_STORE_REMOVE_TOPIC;

        gameService.removeGameInStore(gameInStoreId);

        verify(kafkaTemplate).send(eq(expectedTopic), anyString(), eq(gameInStoreId));
    }

    @Test
    void testGetByGenre_whenAllParameters_shouldCallRequestAndReturnGameListPageDto() {
        List<Genre> genres = List.of(Genre.ACTION);
        List<ProductType> types = List.of(ProductType.GAME);
        int pageSize = 10;
        int pageNum = 1;
        BigDecimal minPrice = BigDecimal.ZERO;
        BigDecimal maxPrice = BigDecimal.valueOf(100);
        String sort = "name-ASC";
        GameListPageDto mockResponse = new GameListPageDto();

        when(restTemplateHandler.executeRequest(anyString(), eq(HttpMethod.GET), isNull(), eq(GameListPageDto.class)))
                .thenReturn(mockResponse);

        GameListPageDto result = gameService.getByGenre(genres, types, pageSize, pageNum, minPrice, maxPrice, sort);

        assertEquals(mockResponse, result);
        verify(restTemplateHandler)
                .executeRequest(eq("/game/genre?pageSize=10&pageNum=1&minPrice=0&" +
                                "maxPrice=100&sortBy=name-ASC&genre=ACTION&type=GAME"),
                        eq(HttpMethod.GET),
                        isNull(),
                        eq(GameListPageDto.class));
    }

    @Test
    void testGetByGenre_whenFewGenresAndZeroGenres_shouldCallRequestAndGameListPageDto() {
        List<Genre> genres = new ArrayList<>();
        genres.add(Genre.ACTION);
        genres.add(Genre.SIMULATORS);
        int pageSize = 10;
        int pageNum = 1;
        BigDecimal minPrice = BigDecimal.ZERO;
        BigDecimal maxPrice = BigDecimal.valueOf(100);
        String sort = "gamesInShop.name-ASC";
        GameListPageDto mockResponse = new GameListPageDto();

        when(restTemplateHandler.executeRequest(anyString(), eq(HttpMethod.GET), isNull(), eq(GameListPageDto.class))).thenReturn(mockResponse);

        GameListPageDto result = gameService.getByGenre(genres, null, pageSize, pageNum, minPrice, maxPrice, sort);

        assertEquals(mockResponse, result);
        verify(restTemplateHandler)
                .executeRequest(eq("/game/genre?pageSize=10&pageNum=1&minPrice=0&" +
                                "maxPrice=100&sortBy=gamesInShop.name-ASC&genre=ACTION&genre=SIMULATORS"),
                        eq(HttpMethod.GET),
                        isNull(),
                        eq(GameListPageDto.class));
    }

    @Test
    void testGetByGenre_whenFewTypesAndZeroGenres_shouldCallRequestAndGameListPageDto() {
        List<ProductType> genres = new ArrayList<>();
        genres.add(ProductType.GAME);
        genres.add(ProductType.ADDITION);
        int pageSize = 10;
        int pageNum = 1;
        BigDecimal minPrice = BigDecimal.ZERO;
        BigDecimal maxPrice = BigDecimal.valueOf(100);
        String sort = "name-ASC";
        GameListPageDto mockResponse = new GameListPageDto();

        when(restTemplateHandler.executeRequest(anyString(), eq(HttpMethod.GET), isNull(), eq(GameListPageDto.class))).thenReturn(mockResponse);

        GameListPageDto result = gameService.getByGenre(null, genres, pageSize, pageNum, minPrice, maxPrice, sort);

        assertEquals(mockResponse, result);
        verify(restTemplateHandler)
                .executeRequest(eq("/game/genre?pageSize=10&pageNum=1&minPrice=0" +
                                "&maxPrice=100&sortBy=name-ASC&type=GAME&type=ADDITION"),
                        eq(HttpMethod.GET),
                        isNull(),
                        eq(GameListPageDto.class));
    }
}
