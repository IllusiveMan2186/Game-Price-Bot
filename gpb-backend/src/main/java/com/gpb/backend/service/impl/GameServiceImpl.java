package com.gpb.backend.service.impl;

import com.gpb.backend.bean.event.GameFollowEvent;
import com.gpb.backend.bean.game.GameInfoDto;
import com.gpb.backend.bean.game.GameListPageDto;
import com.gpb.backend.bean.game.Genre;
import com.gpb.backend.bean.game.ProductType;
import com.gpb.backend.rest.RestTemplateHandler;
import com.gpb.backend.service.GameService;
import com.gpb.backend.util.Constants;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.net.URI;
import java.util.List;
import java.util.UUID;


@Slf4j
@Data
@Service
public class GameServiceImpl implements GameService {

    private final KafkaTemplate<String, GameFollowEvent> gameFollowEventKafkaTemplate;
    private final KafkaTemplate<String, Long> removeKafkaTemplate;
    private final RestTemplateHandler restTemplateHandler;

    public GameServiceImpl(KafkaTemplate<String, GameFollowEvent> gameFollowEventKafkaTemplate,
                           KafkaTemplate<String, Long> removeKafkaTemplate, RestTemplateHandler restTemplateHandler) {
        this.gameFollowEventKafkaTemplate = gameFollowEventKafkaTemplate;
        this.removeKafkaTemplate = removeKafkaTemplate;
        this.restTemplateHandler = restTemplateHandler;
    }


    @Override
    public GameInfoDto getById(long gameId, long userId) {
        String url = "/game/" + gameId;
        HttpHeaders headers = new HttpHeaders();
        if (userId > 0) {
            headers.add("BASIC-USER-ID", String.valueOf(userId));
        }

        return restTemplateHandler.executeRequest(url, HttpMethod.GET, headers, GameInfoDto.class);
    }

    @Override
    public GameListPageDto getByName(String name, int pageSize, int pageNum, String sort) {
        String url = "/game/name/" + name + "?pageSize=" + pageSize + "&pageNum=" + pageNum
                + "&sortBy=" + sort;
        return restTemplateHandler.executeRequest(url, HttpMethod.GET, null, GameListPageDto.class);

    }

    @Override
    public GameInfoDto getByUrl(String url) {
        String apiUrl = "/game/url?url=" + url;

        return restTemplateHandler.executeRequest(apiUrl, HttpMethod.GET, null, GameInfoDto.class);
    }

    @Override
    public GameListPageDto getByGenre(List<Genre> genres, List<ProductType> types, int pageSize, int pageNum,
                                      BigDecimal minPrice, BigDecimal maxPrice, String sort) {
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.newInstance()
                .path("/game/genre")
                .queryParam("pageSize", pageSize)
                .queryParam("pageNum", pageNum)
                .queryParam("minPrice", minPrice)
                .queryParam("maxPrice", maxPrice)
                .queryParam("sortBy", sort);

        if (genres != null && !genres.isEmpty()) {
            uriBuilder.queryParam("genre", genres.toArray());
        }

        if (types != null && !types.isEmpty()) {
            uriBuilder.queryParam("type", types.toArray());
        }
        URI uri = uriBuilder.build().toUri();

        return restTemplateHandler.executeRequest(uri.getPath() + "?" + uri.getQuery(), HttpMethod.GET, null,
                GameListPageDto.class);
    }

    @Override
    public GameListPageDto getUserGames(long userId, int pageSize, int pageNum, String sort) {
        String url = "/game/user/games?pageSize=" + pageSize + "&pageNum=" + pageNum
                + "&sortBy=" + sort;
        HttpHeaders headers = new HttpHeaders();
        headers.add("BASIC-USER-ID", String.valueOf(userId));

        return restTemplateHandler.executeRequest(url, HttpMethod.GET, headers, GameListPageDto.class);
    }

    @Override
    public void removeGame(long gameId) {
        String key = UUID.randomUUID().toString();
        log.info("Send remove game event for game {} ", gameId);
        removeKafkaTemplate.send(Constants.GAME_REMOVE_TOPIC, key, gameId);
    }

    @Override
    public void removeGameInStore(long gameInStoreId) {
        String key = UUID.randomUUID().toString();
        log.info("Send remove game in store event for game {} ", gameInStoreId);
        removeKafkaTemplate.send(Constants.GAME_IN_STORE_REMOVE_TOPIC, key, gameInStoreId);
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
