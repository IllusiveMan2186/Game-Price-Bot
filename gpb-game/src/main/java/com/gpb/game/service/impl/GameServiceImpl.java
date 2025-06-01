package com.gpb.game.service.impl;

import com.gpb.common.entity.game.GameDto;
import com.gpb.common.entity.game.GameInfoDto;
import com.gpb.common.entity.game.GameListPageDto;
import com.gpb.common.entity.game.Genre;
import com.gpb.common.entity.game.ProductType;
import com.gpb.common.exception.NotFoundException;
import com.gpb.game.entity.game.Game;
import com.gpb.game.entity.game.GameInShop;
import com.gpb.game.entity.game.GameRepositorySearchFilter;
import com.gpb.game.repository.GameRepository;
import com.gpb.game.repository.advanced.GameRepositoryAdvance;
import com.gpb.game.service.GameService;
import com.gpb.game.service.StoreAggregatorService;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@Log4j2
@AllArgsConstructor
public class GameServiceImpl implements GameService {

    private final GameRepository gameRepository;
    private final StoreAggregatorService storeAggregatorService;
    private final GameRepositoryAdvance gameRepositoryAdvance;
    private final ModelMapper modelMapper;

    @Override
    public Game getById(long gameId) {
        log.info("Get game by id : {}", gameId);

        return gameRepository.findById(gameId)
                .orElseThrow(() -> new NotFoundException("app.game.error.id.not.found"));
    }

    @Override
    public Game getByIdWithLoadedUsers(long gameId) {
        log.info("Get game by id : {} with loaded users", gameId);

        return gameRepository.findByIdWithUsers(gameId)
                .orElseThrow(() -> new NotFoundException("app.game.error.id.not.found"));
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

        Page<Game> gamePage = gameRepositoryAdvance.searchByNameFullText(name, pageRequest);
        if (gamePage.isEmpty()) {
            List<Game> foundedGames = storeAggregatorService.findGameByName(name);
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
    public GameListPageDto getByGenre(List<Genre> genre, List<ProductType> typesToExclude, final int pageSize,
                                      final int pageNum, BigDecimal minPrice, BigDecimal maxPrice, Sort sort) {
        log.info("Get games by genres : '{}',types to exclude - '{}',price '{}' - '{}' with '{}' " +
                "element on page for '{}' page ", genre, typesToExclude, minPrice, maxPrice, pageSize, pageNum);
        PageRequest pageRequest = PageRequest.of(pageNum - 1, pageSize, sort);
        Page<Game> games;
        List<ProductType> types = getProductTypeThatNotExcluded(typesToExclude);

        GameRepositorySearchFilter gameRepositorySearchFilter = GameRepositorySearchFilter.builder()
                .genres(genre)
                .types(types)
                .minPrice(minPrice)
                .maxPrice(maxPrice)
                .build();

        games = gameRepositoryAdvance.findGames(gameRepositorySearchFilter, pageRequest);

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

        GameRepositorySearchFilter gameRepositorySearchFilter = GameRepositorySearchFilter.builder()
                .userId(userId)
                .build();

        Page<Game> games = gameRepositoryAdvance.findGames(gameRepositorySearchFilter, pageRequest);

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
            if (gameRepository.findByName(game.getName()).isEmpty()) {
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
