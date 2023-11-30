package com.gpb.web.service.impl;

import com.google.common.collect.Lists;
import com.gpb.web.bean.game.Game;
import com.gpb.web.bean.game.GameDto;
import com.gpb.web.bean.game.GameInShop;
import com.gpb.web.bean.game.GameInfoDto;
import com.gpb.web.bean.game.GameListPageDto;
import com.gpb.web.bean.game.Genre;
import com.gpb.web.bean.user.BasicUser;
import com.gpb.web.bean.user.WebUser;
import com.gpb.web.exception.GameAlreadyRegisteredException;
import com.gpb.web.exception.NotFoundException;
import com.gpb.web.repository.GameInShopRepository;
import com.gpb.web.repository.GameRepository;
import com.gpb.web.service.GameService;
import com.gpb.web.service.GameStoresService;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Log4j2
public class GameServiceImpl implements GameService {

    private final GameRepository gameRepository;

    private final GameInShopRepository gameInShopRepository;

    private final GameStoresService gameStoresService;

    private final ModelMapper modelMapper;

    public GameServiceImpl(GameRepository gameRepository, GameInShopRepository gameInShopRepository,
                           GameStoresService gameStoresService, ModelMapper modelMapper) {
        this.gameRepository = gameRepository;
        this.gameInShopRepository = gameInShopRepository;
        this.gameStoresService = gameStoresService;
        this.modelMapper = modelMapper;
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
    public GameInfoDto getById(final long gameId, final long userId) {
        final Game game = getById(gameId);

        boolean isUserSubscribed = game.getUserList().stream().anyMatch(user -> user.getId() == userId);
        GameInfoDto gameInfoDto = modelMapper.map(game, GameInfoDto.class);
        gameInfoDto.setUserSubscribed(isUserSubscribed);
        return gameInfoDto;
    }

    @Override
    public GameListPageDto getByName(final String name, final int pageSize, final int pageNum, Sort sort) {

        log.info(String.format("Get game by name : %s", name));
        PageRequest pageRequest = PageRequest.of(pageNum - 1, pageSize, sort);
        long elementAmount;

        List<Game> games = gameRepository.findByNameContainingIgnoreCase(name, pageRequest);
        if (games.isEmpty()) {
            List<Game> createdGames = gameStoresService.findGameByName(name);
            elementAmount = createdGames.size();
            games = Lists.newArrayList(gameRepository.saveAll(createdGames));
        } else {
            elementAmount = gameRepository.countAllByNameContainingIgnoreCase(name);
        }

        List<GameDto> gameDtos = games.stream()
                .map(this::gameMap)
                .toList();

        return new GameListPageDto(elementAmount, gameDtos);
    }

    @Override
    public GameInfoDto getByUrl(String url) {
        log.info(String.format("Get game by url : %s", url));

        final GameInShop gameInShop = gameInShopRepository.findByUrl(url);
        if (gameInShop == null) {
            Game game = gameStoresService.findGameByUrl(url);
            return modelMapper.map(create(game), GameInfoDto.class);
        }

        return modelMapper.map(gameInShop.getGame(), GameInfoDto.class);
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
            games = gameRepository.findAllByGamesInShop_DiscountPriceBetween(pageRequest, minPrice, maxPrice);
            elementAmount = gameRepository.countAllByGamesInShop_DiscountPriceBetween(minPrice, maxPrice);
        } else {
            games = gameRepository.findByGenresInAndGamesInShop_DiscountPriceBetween(genre, pageRequest, minPrice, maxPrice);
            elementAmount = gameRepository.countByGenresIn(genre);
        }
        List<GameDto> gameDtos = games.stream()
                .map(this::gameMap)
                .collect(Collectors.toList());

        return new GameListPageDto(elementAmount, gameDtos);
    }

    @Override
    public GameListPageDto getUserGames(long userId, int pageSize, int pageNum, Sort sort) {
        log.info(String.format("Get games for user '%s' with '%s' element on page for '%s' page ",
                userId, pageSize, pageNum));
        PageRequest pageRequest = PageRequest.of(pageNum - 1, pageSize, sort);
        BasicUser user = new BasicUser();
        user.setId(userId);

        List<Game> games = gameRepository.findByUserList(user, pageRequest);
        long elementAmount = gameRepository.countAllByUserList(user);

        List<GameDto> gameDtos = games.stream()
                .map(this::gameMap)
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
    public List<GameInShop> getUsersChangedGames(WebUser user, List<GameInShop> changedGames) {
        List<Long> changedGamesIds = changedGames.stream()
                .map(GameInShop::getId)
                .toList();
        return gameInShopRepository.findSubscribedGames(user.getId(), changedGamesIds);
    }

    @Override
    public void removeGame(long gameId) {
        log.info(String.format("Remove game by id : %s", gameId));

        gameRepository.deleteById(gameId);
    }

    @Override
    public void removeGameInStore(long gameInStoreId) {
        log.info(String.format("Remove game in store by id : %s", gameInStoreId));

        gameInShopRepository.deleteById(gameInStoreId);
    }

    private GameDto gameMap(Game game) {
        GameDto gameDto = modelMapper.map(game, GameDto.class);
        BigDecimal minPrice = game.getGamesInShop().stream()
                .map(GameInShop::getDiscountPrice)
                .max(Comparator.naturalOrder())
                .orElse(null);
        BigDecimal maxPrice = game.getGamesInShop().stream()
                .map(GameInShop::getDiscountPrice)
                .min(Comparator.naturalOrder())
                .orElse(null);
        gameDto.setMinPrice(minPrice);
        gameDto.setMaxPrice(maxPrice);
        return gameDto;
    }
}
