package com.gpb.telegram.service.impl;

import com.gpb.common.entity.game.GameInfoDto;
import com.gpb.common.entity.game.GameListPageDto;
import com.gpb.common.service.BasicGameService;
import com.gpb.common.util.CommonConstants;
import com.gpb.telegram.service.GameService;
import com.gpb.telegram.util.Constants;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;


@Slf4j
@Component
@AllArgsConstructor
public class GameServiceImpl implements GameService {
    private final BasicGameService basicGameService;

    @Override
    public GameInfoDto getById(long gameId, long userId) {
        return basicGameService.getById(gameId, userId);
    }

    @Override
    public GameListPageDto getByName(final String name, final int pageNum) {
        log.info("Get game by name '{}' for page {}", name, pageNum);
        return basicGameService.getByName(name, Constants.GAMES_AMOUNT_IN_LIST, pageNum, "name-ASC");
    }

    @Override
    public GameListPageDto getGameList(int pageNum, String sort) {
        log.info("Get game for page {} and sort by {}", pageNum, sort);
        return basicGameService.getByGenre(
                new ArrayList<>(),
                new ArrayList<>(),
                Constants.GAMES_AMOUNT_IN_LIST,
                pageNum,
                new BigDecimal(Constants.GAMES_MIN_PRICE),
                new BigDecimal(Constants.GAMES_MAX_PRICE),
                sort);
    }

    @Override
    public void setFollowGameOption(long gameId, long userId, boolean isFollow) {
        log.info("Set game {} for user {} follow option to {}", gameId, userId, isFollow);
        basicGameService.setFollowGameOption(gameId, userId, isFollow);
    }

    @Override
    public GameListPageDto getUserGames(long basicUserId, int pageNum) {
        log.info("Get user {} game list for page {}", basicUserId, pageNum);
        String sort = String.format("%s-%s", CommonConstants.PRICE_SORT_PARAM, CommonConstants.SORT_DIRECTION_ASCENDING);
        return basicGameService.getUserGames(basicUserId, Constants.GAMES_AMOUNT_IN_LIST, pageNum, sort);
    }
}
