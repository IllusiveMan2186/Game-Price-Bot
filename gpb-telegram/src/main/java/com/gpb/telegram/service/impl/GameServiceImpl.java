package com.gpb.telegram.service.impl;

import com.gpb.common.entity.game.GameInfoDto;
import com.gpb.common.entity.game.GameListPageDto;
import com.gpb.common.service.BasicGameService;
import com.gpb.common.service.RestTemplateHandlerService;
import com.gpb.telegram.service.GameService;
import com.gpb.telegram.util.Constants;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;


@Slf4j
@Component
@AllArgsConstructor
public class GameServiceImpl implements GameService {

    private final RestTemplateHandlerService restTemplateHandler;
    private final BasicGameService basicGameService;

    @Override
    public GameInfoDto getById(long gameId, long userId) {
        return basicGameService.getById(gameId, userId);
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
        basicGameService.setFollowGameOption(gameId, userId, isFollow);
    }

    @Override
    public GameListPageDto getUserGames(long basicUserId, int pageNum) {
        return basicGameService.getUserGames(basicUserId, 2, pageNum, "gamesInShop.price-ASC");
    }
}
