package com.gpb.web.service.impl;

import com.google.common.collect.Lists;
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
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
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
    public List<GameDto> getByName(final String name, final int pageSize, final int pageNum, Sort sort) {

        log.info(String.format("Get game by name : %s", name));
        PageRequest pageRequest = PageRequest.of(pageNum - 1, pageSize, sort);


        List<Game> games = gameRepository.findByNameContainingIgnoreCase(name, pageRequest);
        if (games.isEmpty()) {
            List<Game> createdGames = gameStoresService.findGameByName(name);

            games = Lists.newArrayList(gameRepository.saveAll(createdGames));
        }

        return games.stream()
                .map(GameDto::new)
                .toList();
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
    public GameListPageDto getByGenre(List<Genre> genre, final int pageSize, final int pageNum, BigDecimal minPrice,
                                      BigDecimal maxPrice, Sort sort) {
        log.info(String.format("Get games by genres : '%s',price '%s' - '%s' with '%s' element on page for '%s' page ",
                genre, minPrice, maxPrice, pageSize, pageNum));
        PageRequest pageRequest = PageRequest.of(pageNum - 1, pageSize, sort);
        List<Game> games;
        long elementAmount;

        if (genre == null) {
            games = gameRepository.findAllByGamesInShop_PriceBetween(pageRequest, minPrice, maxPrice);
            elementAmount = gameRepository.countAllByGamesInShop_PriceBetween(minPrice, maxPrice);
        } else {
            games = gameRepository.findByGenresInAndGamesInShop_PriceBetween(genre, pageRequest, minPrice, maxPrice);
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
