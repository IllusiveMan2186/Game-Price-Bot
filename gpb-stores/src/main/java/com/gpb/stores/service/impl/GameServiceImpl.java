package com.gpb.stores.service.impl;

import com.google.common.collect.Lists;
import com.gpb.stores.bean.game.Game;
import com.gpb.stores.bean.game.GameInShop;
import com.gpb.stores.bean.user.BasicUser;
import com.gpb.stores.exception.NotFoundException;
import com.gpb.stores.repository.GameInShopRepository;
import com.gpb.stores.repository.GameRepository;
import com.gpb.stores.service.GameService;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Log4j2
public class GameServiceImpl implements GameService {

    private final GameRepository gameRepository;

    private final GameInShopRepository gameInShopRepository;


    public GameServiceImpl(GameRepository gameRepository, GameInShopRepository gameInShopRepository) {
        this.gameRepository = gameRepository;
        this.gameInShopRepository = gameInShopRepository;
    }

    @Override
    public Game getById(long gameId) {
        log.info(String.format("Get game by id : %s", gameId));

        final Game game = gameRepository.findById(gameId);
        if (game == null) {
            log.info(String.format("Game with id : '%s' not found", gameId));
            throw new NotFoundException("app.game.error.id.not.found");
        }
        return game;
    }

    @Override
    public List<GameInShop> getSubscribedGames() {
        log.info("Get game for which subscribe users");
        return gameInShopRepository.findSubscribedGames();
    }

    @Override
    public void changeInfo(List<GameInShop> changedGames) {
        log.info(String.format("Save games in store changes for %s elements", changedGames.size()));

        gameInShopRepository.saveAll(changedGames);
    }

    @Override
    public List<GameInShop> getUsersChangedGames(BasicUser user, List<GameInShop> changedGames) {
        List<Long> changedGamesIds = changedGames.stream()
                .map(GameInShop::getId)
                .toList();
        return gameInShopRepository.findSubscribedGames(user.getId(), changedGamesIds);
    }

    @Override
    public List<Long> addGames(List<Game> games) {
        log.info("Add games to repository");

        List<Game> addedGames = Lists.newArrayList(gameRepository.saveAll(games));

        return addedGames.stream()
                .map(Game::getId)
                .toList();
    }

    @Override
    public Game setFollowGameOption(long gameId, boolean isFollow) {
        Game game = getById(gameId);
        game.setFollowed(isFollow);
        return gameRepository.save(game);
    }
}
