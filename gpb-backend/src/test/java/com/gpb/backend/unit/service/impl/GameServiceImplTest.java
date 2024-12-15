package com.gpb.backend.unit.service.impl;

import com.gpb.backend.bean.event.GameFollowEvent;
import com.gpb.backend.bean.game.GameInfoDto;
import com.gpb.backend.bean.game.GameListPageDto;
import com.gpb.backend.bean.game.Genre;
import com.gpb.backend.bean.game.ProductType;
import com.gpb.backend.rest.RestTemplateHandler;
import com.gpb.backend.service.impl.GameServiceImpl;
import com.gpb.backend.util.Constants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
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

class GameServiceImplTest {

    private GameServiceImpl gameService;

    @Mock
    private KafkaTemplate<String, GameFollowEvent> gameFollowEventKafkaTemplate;

    @Mock
    private KafkaTemplate<String, Long> kafkaTemplate;

    @Mock
    private RestTemplateHandler restTemplateHandler;

    private static final String GAME_SERVICE_URL = "";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        gameService = new GameServiceImpl(gameFollowEventKafkaTemplate, kafkaTemplate, restTemplateHandler);
    }

    @Test
    void testGetById() {
        long gameId = 1L;
        long userId = 123L;
        GameInfoDto mockResponse = new GameInfoDto();

        String url = GAME_SERVICE_URL + "/game/" + gameId;
        HttpHeaders headers = new HttpHeaders();
        headers.add("BASIC-USER-ID", String.valueOf(userId));

        when(restTemplateHandler.executeRequest(url, HttpMethod.GET, headers, GameInfoDto.class)).thenReturn(mockResponse);

        GameInfoDto result = gameService.getById(gameId, userId);

        assertEquals(mockResponse, result);
        verify(restTemplateHandler).executeRequest(url, HttpMethod.GET, headers, GameInfoDto.class);
    }

    @Test
    void testGetByName() {
        String name = "TestGame";
        int pageSize = 10;
        int pageNum = 1;
        String sort = "gamesInShop.price-ASC";
        GameListPageDto mockResponse = new GameListPageDto();

        String url = GAME_SERVICE_URL + "/game/name/" + name + "?pageSize=" + pageSize + "&pageNum=" + pageNum + "&sortBy=gamesInShop.price-ASC";

        when(restTemplateHandler.executeRequest(url, HttpMethod.GET, null, GameListPageDto.class)).thenReturn(mockResponse);

        GameListPageDto result = gameService.getByName(name, pageSize, pageNum, sort);

        assertEquals(mockResponse, result);
        verify(restTemplateHandler).executeRequest(url, HttpMethod.GET, null, GameListPageDto.class);
    }

    @Test
    void testGetByUrl() {
        String url = "/game/genre?pageSize=10&pageNum=1&minPrice=0&" +
                "maxPrice=100&sortBy=name-ASC&genre=ACTION&type=GAME";
        GameInfoDto mockResponse = new GameInfoDto();

        String serverUrl = GAME_SERVICE_URL + "/game/url?url=" + url;

        when(restTemplateHandler.executeRequest(serverUrl, HttpMethod.GET, null, GameInfoDto.class))
                .thenReturn(mockResponse);

        GameInfoDto result = gameService.getByUrl(url);

        assertEquals(mockResponse, result);
        String expectedUrl = "/game/url?url=/game/genre?pageSize=10&pageNum=1" +
                "&minPrice=0&maxPrice=100&sortBy=name-ASC&genre=ACTION&type=GAME";
        verify(restTemplateHandler).executeRequest(expectedUrl, HttpMethod.GET, null, GameInfoDto.class);
    }

    @Test
    void testSetFollowGameOption_whenFollowEvent_shouldCallFollowEvent() {
        long gameId = 1L;
        long userId = 123L;
        boolean isFollow = true;

        gameService.setFollowGameOption(gameId, userId, isFollow);

        verify(gameFollowEventKafkaTemplate)
                .send(eq(Constants.GAME_FOLLOW_TOPIC), anyString(), eq(new GameFollowEvent(userId, gameId)));
    }

    @Test
    void testSetFollowGameOption_whenUnfollowEvent_shouldCallUnfollowEvent() {
        long gameId = 1L;
        long userId = 123L;
        boolean isFollow = false;

        gameService.setFollowGameOption(gameId, userId, isFollow);

        verify(gameFollowEventKafkaTemplate)
                .send(eq(Constants.GAME_UNFOLLOW_TOPIC), anyString(), eq(new GameFollowEvent(userId, gameId)));
    }

    @Test
    void testGetUserGames() {
        long userId = 123L;
        int pageSize = 10;
        int pageNum = 1;
        String sort = "name-ASC";


        gameService.getUserGames(userId, pageSize, pageNum, sort);


        HttpHeaders headers = new HttpHeaders();
        headers.add("BASIC-USER-ID", String.valueOf(userId));
        String url = "/game/user/games?pageSize=10&pageNum=1&sortBy=name-ASC";
        verify(restTemplateHandler).executeRequest(url, HttpMethod.GET, headers, GameListPageDto.class);
    }

    @Test
    void testRemoveGame() {
        long gameId = 1L;
        String expectedTopic = Constants.GAME_REMOVE_TOPIC;

        gameService.removeGame(gameId);

        verify(kafkaTemplate).send(eq(expectedTopic), anyString(), eq(gameId));
    }

    @Test
    void testRemoveGameGameInStore() {
        long gameInStoreId = 1L;
        String expectedTopic = Constants.GAME_IN_STORE_REMOVE_TOPIC;

        gameService.removeGameInStore(gameInStoreId);

        verify(kafkaTemplate).send(eq(expectedTopic), anyString(), eq(gameInStoreId));
    }

    @Test
    void testGetByGenre_whenAllParameters_shouldCallRequest() {
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
    void testGetByGenre_whenFewGenresAndZeroGenres_shouldCallRequest() {
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
    void testGetByGenre_whenFewTypesAndZeroGenres_shouldCallRequest() {
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
