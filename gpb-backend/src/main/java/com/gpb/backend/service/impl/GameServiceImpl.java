package com.gpb.backend.service.impl;

import com.gpb.backend.service.GameService;
import com.gpb.common.entity.game.AddGameInStoreDto;
import com.gpb.common.entity.game.GameInfoDto;
import com.gpb.common.entity.game.GameListPageDto;
import com.gpb.common.entity.game.Genre;
import com.gpb.common.entity.game.ProductType;
import com.gpb.common.service.BasicGameService;
import com.gpb.common.service.RestTemplateHandlerService;
import com.gpb.common.util.CommonConstants;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;


@Slf4j
@Data
@Service
@AllArgsConstructor
public class GameServiceImpl implements GameService {

    private final KafkaTemplate<String, Long> removeKafkaTemplate;
    private final KafkaTemplate<String, AddGameInStoreDto> addGameInStoreDtoKafkaTemplate;
    private final RestTemplateHandlerService restTemplateHandler;
    private final BasicGameService basicGameService;

    @Override
    public GameInfoDto getById(long gameId, long userId) {
        return basicGameService.getById(gameId, userId);
    }

    @Override
    public GameListPageDto getByName(String name, int pageSize, int pageNum, String sort) {
        return basicGameService.getByName(name, pageSize, pageNum, sort, 0);
    }

    @Override
    public GameInfoDto getByUrl(String url) {
        String apiUrl = "/game/url?url=" + url;

        return restTemplateHandler.executeRequest(apiUrl, HttpMethod.GET, null, GameInfoDto.class);
    }

    @Override
    public void addGameInStore(AddGameInStoreDto addGameInStoreDto) {
        log.info("Send adding game in store event with url '{}' to game {} ",
                addGameInStoreDto.getUrl(),
                addGameInStoreDto.getGameId());
        addGameInStoreDtoKafkaTemplate.send(CommonConstants.GAME_IN_STORE_ADD_TOPIC, "1", addGameInStoreDto);
    }

    @Override
    public GameListPageDto getByGenre(List<Genre> genres, List<ProductType> types, int pageSize, int pageNum,
                                      BigDecimal minPrice, BigDecimal maxPrice, String sort) {
        return basicGameService.getByGenre(genres, types, pageSize, pageNum, minPrice, maxPrice, sort, 0);
    }

    @Override
    public GameListPageDto getUserGames(long userId, int pageSize, int pageNum, String sort) {
        return basicGameService.getUserGames(userId, pageSize, pageNum, sort);
    }

    @Override
    public void removeGame(long gameId) {
        String key = UUID.randomUUID().toString();
        log.info("Send remove game event for game {} ", gameId);
        removeKafkaTemplate.send(CommonConstants.GAME_REMOVE_TOPIC, key, gameId);
    }

    @Override
    public void removeGameInStore(long gameInStoreId) {
        String key = UUID.randomUUID().toString();
        log.info("Send remove game in store event for game {} ", gameInStoreId);
        removeKafkaTemplate.send(CommonConstants.GAME_IN_STORE_REMOVE_TOPIC, key, gameInStoreId);
    }

    @Override
    public void setFollowGameOption(long gameId, long userId, boolean isFollow) {
        basicGameService.setFollowGameOption(gameId, userId, isFollow);
    }
}
