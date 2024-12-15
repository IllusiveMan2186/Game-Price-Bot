package com.gpb.telegram.service.impl;

import com.gpb.telegram.bean.event.GameFollowEvent;
import com.gpb.telegram.bean.game.GameInfoDto;
import com.gpb.telegram.bean.game.GameListPageDto;
import com.gpb.telegram.rest.RestTemplateHandler;
import com.gpb.telegram.service.GameService;
import com.gpb.telegram.util.Constants;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;


@Slf4j
@Component
@AllArgsConstructor
public class GameServiceImpl implements GameService {

    private final RestTemplateHandler restTemplateHandler;
    private final KafkaTemplate<String, GameFollowEvent> gameFollowEventKafkaTemplate;

    @Override
    public GameInfoDto getById(long gameId, long userId) {
        log.info("Get game by id '{}' and '{}'", gameId, userId);
        String url = "/game/" + gameId;
        HttpHeaders headers = new HttpHeaders();
        if (userId > 0) {
            headers.add("BASIC-USER-ID", String.valueOf(userId));
        }

        return restTemplateHandler.executeRequest(url, HttpMethod.GET, headers, GameInfoDto.class);
    }

    @Override
    public GameListPageDto getByName(final String name, final int pageNum) {

        log.info("Get game by name : {}", name);
        String url = "/game/name/" + name + "?pageSize=" + Constants.GAMES_AMOUNT_IN_LIST + "&pageNum=" + pageNum
                + "&sortBy=name-ASC";
        return restTemplateHandler.executeRequest(url, HttpMethod.GET, null, GameListPageDto.class);
    }

    @Override
    public void setFollowGameOption(long gameId, long userId, boolean isFollow) {
        String key = UUID.randomUUID().toString();
        if (isFollow) {
            log.info("Send game follow request for game {} for user {}", userId, gameId);
            gameFollowEventKafkaTemplate.send(Constants.GAME_FOLLOW_TOPIC, key, new GameFollowEvent(userId, gameId));
        } else {
            log.info("Send game unfollow request for game {} for user {}", userId, gameId);
            gameFollowEventKafkaTemplate.send(Constants.GAME_UNFOLLOW_TOPIC, key, new GameFollowEvent(userId, gameId));
        }
    }
}
