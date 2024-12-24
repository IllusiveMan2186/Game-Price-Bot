package com.gpb.common.service.impl;

import com.gpb.common.entity.event.GameFollowEvent;
import com.gpb.common.entity.game.GameInfoDto;
import com.gpb.common.service.BasicGameService;
import com.gpb.common.service.RestTemplateHandlerService;
import com.gpb.common.util.CommonConstants;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.UUID;


@Slf4j
@AllArgsConstructor
public class BasicGameServiceImpl implements BasicGameService {

    private final RestTemplateHandlerService templateHandlerService;
    private final KafkaTemplate<String, GameFollowEvent> gameFollowEventKafkaTemplate;

    @Override
    public GameInfoDto getById(long gameId, long userId) {
        log.info("Get game by id '{}' and '{}'", gameId, userId);
        String url = "/game/" + gameId;
        HttpHeaders headers = new HttpHeaders();
        if (userId > 0) {
            headers.add(CommonConstants.BASIC_USER_ID_HEADER, String.valueOf(userId));
        }

        return templateHandlerService.executeRequest(url, HttpMethod.GET, headers, GameInfoDto.class);
    }

    @Override
    public void setFollowGameOption(long gameId, long userId, boolean isFollow) {
        String key = UUID.randomUUID().toString();
        if (isFollow) {
            log.info("Send game follow request for game {} for user {}", userId, gameId);
            gameFollowEventKafkaTemplate.send(CommonConstants.GAME_FOLLOW_TOPIC, key, new GameFollowEvent(userId, gameId));
        } else {
            log.info("Send game unfollow request for game {} for user {}", userId, gameId);
            gameFollowEventKafkaTemplate.send(CommonConstants.GAME_UNFOLLOW_TOPIC, key, new GameFollowEvent(userId, gameId));
        }
    }
}
