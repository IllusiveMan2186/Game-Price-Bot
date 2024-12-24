package com.gpb.common.service.impl;

import com.gpb.common.entity.event.GameFollowEvent;
import com.gpb.common.entity.game.GameInfoDto;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BasicGameServiceImplTest {

    @Mock
    private KafkaTemplate<String, GameFollowEvent> gameFollowEventKafkaTemplate;

    @Mock
    private RestTemplateHandlerService restTemplateHandlerServiceImpl;

    @InjectMocks
    private BasicGameServiceImpl gameService;

    @Test
    void testGetById() {
        long gameId = 1L;
        long userId = 123L;
        GameInfoDto mockResponse = new GameInfoDto();

        String url = "/game/" + gameId;
        HttpHeaders headers = new HttpHeaders();
        headers.add("BASIC-USER-ID", String.valueOf(userId));

        when(restTemplateHandlerServiceImpl.executeRequest(url, HttpMethod.GET, headers, GameInfoDto.class)).thenReturn(mockResponse);

        GameInfoDto result = gameService.getById(gameId, userId);

        assertEquals(mockResponse, result);
        verify(restTemplateHandlerServiceImpl).executeRequest(url, HttpMethod.GET, headers, GameInfoDto.class);
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
}
