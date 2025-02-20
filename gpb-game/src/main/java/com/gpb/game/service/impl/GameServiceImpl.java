package com.gpb.game.service.impl;

import com.gpb.common.entity.game.GameDto;
import com.gpb.common.entity.game.GameInfoDto;
import com.gpb.common.entity.game.GameListPageDto;
import com.gpb.common.entity.game.Genre;
import com.gpb.common.entity.game.ProductType;
import com.gpb.common.exception.NotFoundException;
import com.gpb.game.entity.game.Game;
import com.gpb.game.entity.game.GameInShop;
import com.gpb.game.entity.user.BasicUser;
import com.gpb.game.repository.GameInShopRepository;
import com.gpb.game.repository.GameRepository;
import com.gpb.game.repository.GameRepositoryCustom;
import com.gpb.game.service.GameService;
import com.gpb.game.service.GameStoresService;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Log4j2
@AllArgsConstructor
public class GameServiceImpl implements GameService {

    private final GameRepository gameRepository;
    private final GameStoresService gameStoresService;
    private final GameRepositoryCustom gameRepositoryCustom;
    private final ModelMapper modelMapper;

    @Override
    public Game getById(long gameId) {
        log.info("Get game by id : {}", gameId);

        final Game game = gameRepository.findById(gameId);
        if (game == null) {
            log.info("Game with id : '{}' not found", gameId);
            throw new NotFoundException("app.game.error.id.not.found");
        }
        return game;
    }

    @Override
    public GameInfoDto getDtoById(final long gameId) {
        log.info("Get game info dto by id : {}", gameId);
        final Game game = getById(gameId);

        return modelMapper.map(game, GameInfoDto.class);
    }

    @Override
    public GameListPageDto getByName(final String name, final int pageSize, final int pageNum, Sort sort) {
        log.info("Get game by name : {}", name);
        PageRequest pageRequest = PageRequest.of(pageNum - 1, pageSize, sort);
        long elementAmount;
        List<Game> games;

        Page<Game> gamePage = gameRepositoryCustom.searchByNameFullText(name, pageRequest);
        if (gamePage.isEmpty()) {
            List<Game> foundedGames = gameStoresService.findGameByName(name);
            games = addGames(foundedGames);
            elementAmount = games.size();
        } else {
            games = gamePage.toList();
            elementAmount = gamePage.getTotalElements();
        }

        List<GameDto> gameDtos = games.stream()
                .map(this::gameMap)
                .toList();

        return new GameListPageDto(elementAmount, gameDtos);
    }


    @Override
    public Game getByUrl(String url) {
        log.info("Get game by url : {}", url);

        Game game = gameStoresService.findGameByUrl(url);
        return gameRepository.save(game);
    }

    @Override
    public GameListPageDto getByGenre(List<Genre> genre, List<ProductType> typesToExclude, final int pageSize,
                                      final int pageNum, BigDecimal minPrice, BigDecimal maxPrice, Sort sort) {
        log.info("Get games by genres : '{}',types to exclude - '{}',price '{}' - '{}' with '{}' " +
                "element on page for '{}' page ", genre, typesToExclude, minPrice, maxPrice, pageSize, pageNum);
        PageRequest pageRequest = PageRequest.of(pageNum - 1, pageSize, sort);
        Page<Game> games;
        List<ProductType> types = getProductTypeThatNotExcluded(typesToExclude);

        games = gameRepositoryCustom.findGamesByGenreAndTypeWithSorting(genre, types, minPrice
                , maxPrice, pageRequest);

        List<GameDto> gameDtos = games.stream()
                .map(this::gameMap)
                .toList();

        return new GameListPageDto(games.getTotalElements(), gameDtos);
    }

    @Override
    public GameListPageDto getUserGames(long userId, int pageSize, int pageNum, Sort sort) {
        log.info("Get games for user '{}' with '{}' element on page for '{}' page ",
                userId, pageSize, pageNum);
        PageRequest pageRequest = PageRequest.of(pageNum - 1, pageSize, sort);

        Page<Game> games = gameRepositoryCustom.findGamesByUserWithSorting(userId, pageRequest);

        List<GameDto> gameDtos = games.stream()
                .map(this::gameMap)
                .toList();
        return new GameListPageDto(games.getTotalElements(), gameDtos);
    }

    @Override
    public void removeGame(long gameId) {
        log.info("Remove game by id : {}", gameId);

        gameRepository.deleteById(gameId);
    }

    @Override
    public Game setFollowGameOption(long gameId, boolean isFollow) {
        Game game = getById(gameId);
        game.setFollowed(isFollow);
        return gameRepository.save(game);
    }

    @Override
    public void removeGameInShopFromGame(GameInShop gameInShop) {
        Game game = gameInShop.getGame();

        if (game.getGamesInShop().size() <= 1) {
            log.info("Removes game due to last game in store info removed : {}", game.getId());
            removeGame(game.getId());
        } else {
            log.info("Removes game due to last game in store info removed : {}", game.getId());
            game.getGamesInShop().remove(gameInShop);
            gameRepository.save(game);
            log.info("Game in store by id {} successfully removed", gameInShop.getId());
        }
    }

    private List<Game> addGames(List<Game> games) {
        log.info("Add games to repository");

        List<Game> addedGames = new ArrayList<>();

        for (Game game : games) {
            if (gameRepository.findByName(game.getName()) == null) {
                log.info("Game added '{}' with game in shop {}", game.getName(), game.getGamesInShop());
                addedGames.add(gameRepository.save(game));
            } else {
                log.info("Game with name '{}' already exists and will not be added.", game.getName());
            }
        }

        return addedGames;
    }

    private GameDto gameMap(Game game) {
        return modelMapper.map(game, GameDto.class);
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
