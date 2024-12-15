package com.gpb.game.service.impl;

import com.gpb.game.bean.game.Game;
import com.gpb.game.bean.game.GameDto;
import com.gpb.game.bean.game.GameInShop;
import com.gpb.game.bean.game.GameInfoDto;
import com.gpb.game.bean.game.GameListPageDto;
import com.gpb.game.bean.game.Genre;
import com.gpb.game.bean.game.ProductType;
import com.gpb.game.bean.user.BasicUser;
import com.gpb.game.exception.NotFoundException;
import com.gpb.game.repository.GameInShopRepository;
import com.gpb.game.repository.GameRepository;
import com.gpb.game.service.GameService;
import com.gpb.game.service.GameStoresService;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@Log4j2
public class GameServiceImpl implements GameService {

    private final GameRepository gameRepository;
    private final GameInShopRepository gameInShopRepository;
    private final GameStoresService gameStoresService;
    private final ModelMapper modelMapper;


    public GameServiceImpl(GameRepository gameRepository,
                           GameInShopRepository gameInShopRepository,
                           GameStoresService gameStoresService,
                           ModelMapper modelMapper) {
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
    public GameInfoDto getDtoById(final long gameId) {
        final Game game = getById(gameId);

        return modelMapper.map(game, GameInfoDto.class);
    }

    @Override
    public GameListPageDto getByName(final String name, final int pageSize, final int pageNum, Sort sort) {
        log.info(String.format("Get game by name : %s", name));
        PageRequest pageRequest = PageRequest.of(pageNum - 1, pageSize, sort);
        long elementAmount;

        List<Game> games = gameRepository.findByNameContainingIgnoreCase(name, pageRequest);
        if (games.isEmpty()) {
            List<Game> foundedGames = gameStoresService.findGameByName(name);
            games = addGames(foundedGames);
            elementAmount = games.size();
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
            return modelMapper.map(game, GameInfoDto.class);
        }

        return modelMapper.map(gameInShop.getGame(), GameInfoDto.class);
    }

    @Override
    public GameListPageDto getByGenre(List<Genre> genre, List<ProductType> typesToExclude, final int pageSize,
                                      final int pageNum, BigDecimal minPrice, BigDecimal maxPrice, Sort sort) {
        log.info(String.format("Get games by genres : '%s',types to exclude - '%s',price '%s' - '%s' with '%s' " +
                "element on page for '%s' page ", genre, typesToExclude, minPrice, maxPrice, pageSize, pageNum));
        PageRequest pageRequest = PageRequest.of(pageNum - 1, pageSize, sort);
        List<Game> games;
        long elementAmount;
        List<ProductType> types = getProductTypeThatNotExcluded(typesToExclude);
        System.out.println(pageRequest + " " + types + " " + genre);
        if (genre == null) {
            games = gameRepository.findAllByTypeInAndGamesInShop_DiscountPriceBetween(pageRequest, types, minPrice
                    , maxPrice);
            elementAmount = gameRepository.countAllByTypeInAndGamesInShop_DiscountPriceBetween(types, minPrice, maxPrice);
        } else {
            games = gameRepository.findByGenresInAndTypeInAndGamesInShop_DiscountPriceBetween(genre, types, pageRequest
                    , minPrice, maxPrice);
            elementAmount = gameRepository.countByGenresInAndTypeIn(genre, types);
        }
        List<GameDto> gameDtos = games.stream()
                .map(this::gameMap)
                .toList();

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
                .toList();

        return new GameListPageDto(elementAmount, gameDtos);
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
    public void removeGame(long gameId) {
        log.info(String.format("Remove game by id : %s", gameId));

        gameRepository.deleteById(gameId);
    }

    @Override
    public void removeGameInStore(long gameInStoreId) {
        log.info(String.format("Remove game in store by id : %s", gameInStoreId));

        gameInShopRepository.deleteById(gameInStoreId);
    }

    @Override
    public Game setFollowGameOption(long gameId, boolean isFollow) {
        Game game = getById(gameId);
        game.setFollowed(isFollow);
        return gameRepository.save(game);
    }

    private List<Game> addGames(List<Game> games) {
        log.info("Add games to repository");

        List<Game> addedGames = new ArrayList<>();

        for (Game game : games) {
            if (gameRepository.findByName(game.getName()) == null) {
                addedGames.add(gameRepository.save(game));
            } else {
                log.info("Game with name '{}' already exists and will not be added.", game.getName());
            }
        }

        return addedGames;
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

    private List<ProductType> getProductTypeThatNotExcluded(List<ProductType> typesToExclude) {
        List<ProductType> types = new ArrayList<>();
        for (ProductType type : ProductType.values()) {
            if (typesToExclude == null || !typesToExclude.contains(type)) {
                types.add(type);
            }
        }
        return types;
    }
}
