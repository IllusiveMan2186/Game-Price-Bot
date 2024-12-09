package com.gpb.web.service.impl;

import com.gpb.web.bean.event.GameFollowEvent;
import com.gpb.web.bean.game.GameInfoDto;
import com.gpb.web.bean.game.GameListPageDto;
import com.gpb.web.bean.game.Genre;
import com.gpb.web.bean.game.ProductType;
import com.gpb.web.rest.RestTemplateHandler;
import com.gpb.web.service.GameService;
import com.gpb.web.util.Constants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.http.*;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.net.URI;
import java.util.List;
import java.util.UUID;

@Service
public class GameServiceImpl implements GameService {

    private final KafkaTemplate<String, GameFollowEvent> gameFollowEventKafkaTemplate;
    private final KafkaTemplate<String, Long> kafkaTemplate;
    private final RestTemplateHandler restTemplateHandler;

    @Value("${GAME_SERVICE_URL}")
    private String gameServiceUrl;

    public GameServiceImpl(KafkaTemplate<String, GameFollowEvent> gameFollowEventKafkaTemplate,
                           KafkaTemplate<String, Long> kafkaTemplate, RestTemplateHandler restTemplateHandler) {
        this.gameFollowEventKafkaTemplate = gameFollowEventKafkaTemplate;
        this.kafkaTemplate = kafkaTemplate;
        this.restTemplateHandler = restTemplateHandler;
    }


    @Override
    public GameInfoDto getById(long gameId, long userId) {
        String url = gameServiceUrl + "/" + gameId;
        HttpHeaders headers = new HttpHeaders();
        headers.add("BASIC-USER-ID", String.valueOf(userId));

        return restTemplateHandler.executeRequest(url, HttpMethod.GET, headers, GameInfoDto.class);
    }

    @Override
    public GameListPageDto getByName(String name, int pageSize, int pageNum, Sort sort) {
        String url = gameServiceUrl + "/name/" + name + "?pageSize=" + pageSize + "&pageNum=" + pageNum + "&sortBy="
                + getSortParam(sort);
        return restTemplateHandler.executeRequest(url, HttpMethod.GET, null, GameListPageDto.class);

    }

    @Override
    public GameInfoDto getByUrl(String url) {
        String apiUrl = gameServiceUrl + "/url?url=" + url;

        return restTemplateHandler.executeRequest(apiUrl, HttpMethod.GET, null, GameInfoDto.class);
    }

    @Override
    public GameListPageDto getByGenre(List<Genre> genres, List<ProductType> types, int pageSize, int pageNum,
                                      BigDecimal minPrice, BigDecimal maxPrice, Sort sort) {
        URI uri = UriComponentsBuilder.fromHttpUrl(gameServiceUrl+ "/genre")
                .queryParam("genre", genres.toArray())
                .queryParam("type", types.toArray())
                .queryParam("pageSize", pageSize)
                .queryParam("pageNum", pageNum)
                .queryParam("minPrice", minPrice)
                .queryParam("maxPrice", maxPrice)
                .queryParam("sortBy", sort)
                .build()
                .toUri();

        return restTemplateHandler.executeRequest(uri.toString(), HttpMethod.GET, null, GameListPageDto.class);
    }

    @Override
    public GameListPageDto getUserGames(long userId, int pageSize, int pageNum, Sort sort) {
        String url = gameServiceUrl + "/user/games?pageSize=" + pageSize + "&pageNum=" + pageNum + "&sortBy=" + getSortParam(sort);
        HttpHeaders headers = new HttpHeaders();
        headers.add("BASIC-USER-ID", String.valueOf(userId));

        return restTemplateHandler.executeRequest(url, HttpMethod.GET, headers, GameListPageDto.class);
    }

    @Override
    public void removeGame(long gameId) {
        String key = UUID.randomUUID().toString();
        kafkaTemplate.send(Constants.GAME_REMOVE_TOPIC, key, gameId);

    }

    @Override
    public void removeGameInStore(long gameInStoreId) {
        String key = UUID.randomUUID().toString();
        kafkaTemplate.send(Constants.GAME_IN_STORE_REMOVE_TOPIC, key, gameInStoreId);
    }

    @Override
    public void setFollowGameOption(long gameId, long userId, boolean isFollow) {
        String key = UUID.randomUUID().toString();
        if (isFollow) {
            gameFollowEventKafkaTemplate.send(Constants.GAME_FOLLOW_TOPIC, key, new GameFollowEvent(userId, gameId));
        } else {
            gameFollowEventKafkaTemplate.send(Constants.GAME_UNFOLLOW_TOPIC, key, new GameFollowEvent(userId, gameId));
        }
    }

    private String getSortParam(Sort sort) {
        if (sort == null || sort.isUnsorted()) {
            return "gamesInShop.price-ASC";
        }
        Sort.Order order = sort.iterator().next();
        return order.getProperty() + "-" + (order.isAscending() ? "ASC" : "DESC");
    }
}
