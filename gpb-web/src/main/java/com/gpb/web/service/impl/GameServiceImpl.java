package com.gpb.web.service.impl;

import com.gpb.web.bean.Game;
import com.gpb.web.bean.GameInShop;
import com.gpb.web.bean.Genre;
import com.gpb.web.exception.GameAlreadyRegisteredException;
import com.gpb.web.exception.NotFoundException;
import com.gpb.web.repository.GameInShopRepository;
import com.gpb.web.repository.GameRepository;
import com.gpb.web.service.GameService;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.PageRequest;
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
    public Game getById(final long gameId) {
        log.info(String.format("Get game by id : %s", gameId));

        final Game game = gameRepository.findById(gameId);
        if (game == null) {
            throw new NotFoundException(String.format("Game with id '%s' not found", gameId));
        }
        return game;
    }

    @Override
    public Game getByName(final String name) {

        log.info(String.format("Get game by name : %s", name));

        final Game game = gameRepository.findByName(name);
        if (game == null) {
            throw new NotFoundException(String.format("Game with name '%s' not found", name));
        }

        return game;
    }

    @Override
    public Game getByUrl(String url) {
        log.info(String.format("Get game by url : %s", url));

        final GameInShop gameInShop = gameInShopRepository.findByUrl(url);
        if (gameInShop == null) {
            throw new NotFoundException(String.format("Game with url '%s' not found", url));
        }

        return gameInShop.getGame();
    }

    @Override
    public List<Game> getByGenre(final Genre genre, final int pageSize, final int pageNum) {
        log.info(String.format("Get games by genre : %s", genre));

        return gameRepository.findByGenre(genre, PageRequest.of(pageNum - 1, pageSize));
    }

    @Override
    public Game create(Game game) {
        log.info(String.format("Create game : %s", game));

        if (gameRepository.findByName(game.getName()) != null) {
            throw new GameAlreadyRegisteredException();
        }

        return gameRepository.save(game);
    }
}
