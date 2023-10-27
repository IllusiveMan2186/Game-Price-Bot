package com.gpb.web.service.impl;

import com.gpb.web.bean.game.Game;
import com.gpb.web.bean.game.GameDto;
import com.gpb.web.bean.game.GameInShop;
import com.gpb.web.bean.game.GameInfoDto;
import com.gpb.web.bean.game.GameListPageDto;
import com.gpb.web.bean.game.Genre;
import com.gpb.web.exception.GameAlreadyRegisteredException;
import com.gpb.web.exception.NotFoundException;
import com.gpb.web.repository.GameInShopRepository;
import com.gpb.web.repository.GameRepository;
import com.gpb.web.service.GameService;
import com.gpb.web.service.GameStoresService;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Log4j2
public class GameServiceImpl implements GameService {

    private final GameRepository gameRepository;

    private final GameInShopRepository gameInShopRepository;

    private final GameStoresService gameStoresService;

    public GameServiceImpl(GameRepository gameRepository, GameInShopRepository gameInShopRepository, GameStoresService gameStoresService) {
        this.gameRepository = gameRepository;
        this.gameInShopRepository = gameInShopRepository;
        this.gameStoresService = gameStoresService;
    }


    @Override
    public GameInfoDto getById(final long gameId) {
        log.info(String.format("Get game by id : %s", gameId));

        final Game game = gameRepository.findById(gameId);
        if (game == null) {
            log.info(String.format("Game with id : '%s' not found", gameId));
            throw new NotFoundException("app.game.error.id.not.found");
        }
        return new GameInfoDto(game);
    }

    @Override
    public GameInfoDto getByName(final String name) {

        log.info(String.format("Get game by name : %s", name));

        Game game = gameRepository.findByName(name);
        if (game == null) {
            game = create(gameStoresService.findGameByName(name));
        }

        return new GameInfoDto(game);
    }

    @Override
    public GameInfoDto getByUrl(String url) {
        log.info(String.format("Get game by url : %s", url));

        final GameInShop gameInShop = gameInShopRepository.findByUrl(url);
        if (gameInShop == null) {
            Game game = gameStoresService.findGameByUrl(url);
            return new GameInfoDto(create(game));
        }

        return new GameInfoDto(gameInShop.getGame());
    }

    @Override
    public GameListPageDto getByGenre(List<Genre> genre, final int pageSize, final int pageNum) {
        log.info(String.format("Get games by genre : %s", genre));
        PageRequest pageRequest = PageRequest.of(pageNum - 1, pageSize);
        List<Game> games;
        long elementAmount;

        if (genre == null) {
            games = gameRepository.findAll(pageRequest);
            elementAmount = gameRepository.count();
        } else {
            games = gameRepository.findByGenresIn(genre, pageRequest);
            elementAmount = gameRepository.countByGenresIn(genre);
        }
        List<GameDto> gameDtos = games.stream()
                .map(GameDto::new)
                .collect(Collectors.toList());

        return new GameListPageDto(elementAmount, gameDtos);
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
