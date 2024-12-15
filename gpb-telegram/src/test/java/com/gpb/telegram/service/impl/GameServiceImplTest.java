package com.gpb.telegram.service.impl;

import com.gpb.telegram.bean.event.GameFollowEvent;
import com.gpb.telegram.bean.game.GameInfoDto;
import com.gpb.telegram.bean.game.GameListPageDto;
import com.gpb.telegram.rest.RestTemplateHandler;
import com.gpb.telegram.util.Constants;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.kafka.core.KafkaTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GameServiceImplTest {

    @Mock
    private KafkaTemplate<String, GameFollowEvent> gameFollowEventKafkaTemplate;

    @Mock
    private RestTemplateHandler restTemplateHandler;

    @InjectMocks
    private GameServiceImpl gameService;

    @Test
    void testGetById() {
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
}
