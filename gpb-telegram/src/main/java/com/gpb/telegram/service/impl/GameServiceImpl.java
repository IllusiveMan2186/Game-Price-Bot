package com.gpb.telegram.service.impl;

import com.gpb.telegram.bean.Game;
import com.gpb.telegram.exception.NotFoundException;
import com.gpb.telegram.repository.GameRepository;
import com.gpb.telegram.service.GameService;
import com.gpb.telegram.service.GameStoresService;
import com.gpb.telegram.util.Constants;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;


@Slf4j
@Component
@AllArgsConstructor
public class GameServiceImpl implements GameService {

    private final GameRepository gameRepository;
    private final GameStoresService gameStoresService;

    @Override
    public Game getById(long gameId) {
        log.info(String.format("Get game by id : %s", gameId));

        final Optional<Game> game = gameRepository.findById(gameId);
        if (game.isEmpty()) {
            log.info(String.format("Game with id : '%s' not found", gameId));
            throw new NotFoundException("app.game.error.id.not.found");
        }
        return game.get();
    }

    @Override
    public List<Game> getByName(final String name, final int pageNum) {

        log.info(String.format("Get game by name : %s", name));
        PageRequest pageRequest = PageRequest
                .of(pageNum - 1, Constants.GAMES_AMOUNT_IN_LIST, Sort.by(Sort.Direction.ASC, "name"));

        List<Game> games = gameRepository.findByNameContainingIgnoreCase(name, pageRequest);
        if (games.isEmpty()) {
            List<Long> createdGamesIds = gameStoresService.findGameByName(name);
            games = gameRepository.findByIdIn(createdGamesIds, pageRequest);
        }

        return games;
    }

    @Override
    public long getGameAmountByName(String name) {
        return gameRepository.countAllByNameContainingIgnoreCase(name);
    }
}
