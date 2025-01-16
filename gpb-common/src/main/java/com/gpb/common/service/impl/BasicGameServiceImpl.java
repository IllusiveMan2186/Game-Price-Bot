package com.gpb.common.service.impl;

import com.gpb.common.entity.event.GameFollowEvent;
import com.gpb.common.entity.game.GameInfoDto;
import com.gpb.common.entity.game.GameListPageDto;
import com.gpb.common.entity.game.Genre;
import com.gpb.common.entity.game.ProductType;
import com.gpb.common.service.BasicGameService;
import com.gpb.common.service.RestTemplateHandlerService;
import com.gpb.common.util.CommonConstants;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.net.URI;
import java.util.List;
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
    public GameListPageDto getByName(String name, int pageSize, int pageNum, String sort) {
        String url = "/game/name/" + name + "?pageSize=" + pageSize + "&pageNum=" + pageNum
                + "&sortBy=" + sort;
        return templateHandlerService.executeRequest(url, HttpMethod.GET, null, GameListPageDto.class);
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

        return templateHandlerService.executeRequest(
                uri.getPath() + "?" + uri.getQuery(),
                HttpMethod.GET,
                null,
                GameListPageDto.class);
    }

    @Override
    public GameListPageDto getUserGames(long userId, int pageSize, int pageNum, String sort) {
        String url = "/game/user/games?pageSize=" + pageSize + "&pageNum=" + pageNum
                + "&sortBy=" + sort;
        HttpHeaders headers = new HttpHeaders();
        headers.add("BASIC-USER-ID", String.valueOf(userId));

        return templateHandlerService.executeRequest(url, HttpMethod.GET, headers, GameListPageDto.class);
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
