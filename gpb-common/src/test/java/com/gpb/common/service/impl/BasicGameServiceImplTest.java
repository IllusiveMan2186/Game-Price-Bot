package com.gpb.common.service.impl;

import com.gpb.common.entity.event.GameFollowEvent;
import com.gpb.common.entity.game.GameInfoDto;
import com.gpb.common.entity.game.GameListPageDto;
import com.gpb.common.entity.game.Genre;
import com.gpb.common.entity.game.ProductType;
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

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BasicGameServiceImplTest {

    @Mock
    private KafkaTemplate<String, GameFollowEvent> gameFollowEventKafkaTemplate;

    @Mock
    private RestTemplateHandlerService restTemplateHandler;

    @InjectMocks
    private BasicGameServiceImpl gameService;

    @Test
    void testGetById_whenWithBasicUserId_shouldReturnGameInfoDto() {
        long gameId = 1L;
        long userId = 123L;
        GameInfoDto mockResponse = new GameInfoDto();

        String url = "/game/" + gameId;
        HttpHeaders headers = new HttpHeaders();
        headers.add("BASIC-USER-ID", String.valueOf(userId));

        when(restTemplateHandler.executeRequest(url, HttpMethod.GET, headers, GameInfoDto.class)).thenReturn(mockResponse);

        GameInfoDto result = gameService.getById(gameId, userId);

        assertEquals(mockResponse, result);
        verify(restTemplateHandler).executeRequest(url, HttpMethod.GET, headers, GameInfoDto.class);
    }

    @Test
    void testGetById_whenBasicUserIdIsNull_shouldReturnGameInfoDto() {
        long gameId = 1L;
        long userId = 0L;
        GameInfoDto mockResponse = new GameInfoDto();

        String url = "/game/" + gameId;

        when(restTemplateHandler.executeRequest(url, HttpMethod.GET, null, GameInfoDto.class)).thenReturn(mockResponse);

        GameInfoDto result = gameService.getById(gameId, userId);

        assertEquals(mockResponse, result);
        verify(restTemplateHandler).executeRequest(url, HttpMethod.GET, null, GameInfoDto.class);
    }

    @Test
    void testGetByName_whenSuccess_shouldReturnGameListPageDto() {
        String name = "TestGame";
        int pageSize = 10;
        int pageNum = 1;
        long basicUserId = 1;
        String sort = "gamesInShop.price-ASC";
        GameListPageDto mockResponse = new GameListPageDto();

        String url = "/game/name/" + name + "?pageSize=" + pageSize + "&pageNum=" + pageNum + "&sortBy=gamesInShop.price-ASC";

        when(restTemplateHandler.executeRequest(
                url,
                HttpMethod.GET,
                getBasicUserIdHeader(basicUserId),
                GameListPageDto.class)).thenReturn(mockResponse);

        GameListPageDto result = gameService.getByName(name, pageSize, pageNum, sort, basicUserId);

        assertEquals(mockResponse, result);
        verify(restTemplateHandler).executeRequest(url, HttpMethod.GET, getBasicUserIdHeader(basicUserId), GameListPageDto.class);
    }


    @Test
    void testSetFollowGameOption_whenFollowEvent_shouldCallFollowEvent() {
        long gameId = 1L;
        long userId = 123L;
        boolean isFollow = true;

        gameService.setFollowGameOption(gameId, userId, isFollow);

        verify(gameFollowEventKafkaTemplate)
                .send(eq(CommonConstants.GAME_FOLLOW_TOPIC), anyString(), eq(new GameFollowEvent(userId, gameId)));
    }

    @Test
    void testSetFollowGameOption_whenUnfollowEvent_shouldCallUnfollowEvent() {
        long gameId = 1L;
        long userId = 123L;
        boolean isFollow = false;

        gameService.setFollowGameOption(gameId, userId, isFollow);

        verify(gameFollowEventKafkaTemplate)
                .send(eq(CommonConstants.GAME_UNFOLLOW_TOPIC), anyString(), eq(new GameFollowEvent(userId, gameId)));
    }

    @Test
    void testGetUserGames_whenSuccess_shouldCallRequest() {
        long userId = 123L;
        int pageSize = 10;
        int pageNum = 1;
        String sort = "name-ASC";
        HttpHeaders headers = new HttpHeaders();
        headers.add("BASIC-USER-ID", String.valueOf(userId));
        GameListPageDto mockResponse = new GameListPageDto();
        String url = "/game/user/games?pageSize=10&pageNum=1&sortBy=name-ASC";
        when(restTemplateHandler.executeRequest(url, HttpMethod.GET, headers, GameListPageDto.class)).thenReturn(mockResponse);


        GameListPageDto result = gameService.getUserGames(userId, pageSize, pageNum, sort);


        assertEquals(mockResponse, result);
        verify(restTemplateHandler).executeRequest(url, HttpMethod.GET, headers, GameListPageDto.class);
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
        long basicUserId = 1;
        GameListPageDto mockResponse = new GameListPageDto();

        when(restTemplateHandler.executeRequest(
                anyString(),
                eq(HttpMethod.GET),
                eq(getBasicUserIdHeader(basicUserId)),
                eq(GameListPageDto.class)))
                .thenReturn(mockResponse);

        GameListPageDto result = gameService
                .getByGenre(genres, types, pageSize, pageNum, minPrice, maxPrice, sort, basicUserId);

        assertEquals(mockResponse, result);
        verify(restTemplateHandler)
                .executeRequest(eq("/game/genre?pageSize=10&pageNum=1&minPrice=0&" +
                                "maxPrice=100&sortBy=name-ASC&genre=ACTION&type=GAME"),
                        eq(HttpMethod.GET),
                        eq(getBasicUserIdHeader(basicUserId)),
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
        long basicUserId = 1;
        GameListPageDto mockResponse = new GameListPageDto();

        when(restTemplateHandler.executeRequest(
                anyString(),
                eq(HttpMethod.GET),
                eq(getBasicUserIdHeader(basicUserId)),
                eq(GameListPageDto.class))).thenReturn(mockResponse);

        GameListPageDto result = gameService
                .getByGenre(genres, null, pageSize, pageNum, minPrice, maxPrice, sort, basicUserId);

        assertEquals(mockResponse, result);
        verify(restTemplateHandler)
                .executeRequest(eq("/game/genre?pageSize=10&pageNum=1&minPrice=0&" +
                                "maxPrice=100&sortBy=gamesInShop.name-ASC&genre=ACTION&genre=SIMULATORS"),
                        eq(HttpMethod.GET),
                        eq(getBasicUserIdHeader(basicUserId)),
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
        long basicUserId = 1;
        GameListPageDto mockResponse = new GameListPageDto();

        when(restTemplateHandler.executeRequest(
                anyString(),
                eq(HttpMethod.GET),
                eq(getBasicUserIdHeader(basicUserId)),
                eq(GameListPageDto.class))).thenReturn(mockResponse);


        GameListPageDto result = gameService
                .getByGenre(null, genres, pageSize, pageNum, minPrice, maxPrice, sort, basicUserId);


        assertEquals(mockResponse, result);
        verify(restTemplateHandler)
                .executeRequest(eq("/game/genre?pageSize=10&pageNum=1&minPrice=0" +
                                "&maxPrice=100&sortBy=name-ASC&type=GAME&type=ADDITION"),
                        eq(HttpMethod.GET),
                        eq(getBasicUserIdHeader(basicUserId)),
                        eq(GameListPageDto.class));
    }

    @Test
    void testGetGameImage_whenSuccess_shouldCallGameImageRequest() {
        String gameName = "testGame";
        byte[] expectedBytes = new byte[]{1, 2, 3};
        String expectedUrl = "/game/image/" + gameName;

        when(restTemplateHandler.executeRequest(
                expectedUrl,
                HttpMethod.GET,
                null,
                byte[].class
        )).thenReturn(expectedBytes);


        byte[] result = gameService.getGameImage(gameName);


        assertArrayEquals(expectedBytes, result, "The returned byte array should match the expected bytes");
        verify(restTemplateHandler).executeRequest(
                expectedUrl,
                HttpMethod.GET,
                null,
                byte[].class
        );
    }

    private HttpHeaders getBasicUserIdHeader(long basicUserId) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(CommonConstants.BASIC_USER_ID_HEADER, String.valueOf(basicUserId));
        return headers;
    }
}
