package com.gpb.game.service.impl;

import com.gpb.common.exception.NotFoundException;
import com.gpb.game.entity.game.Game;
import com.gpb.game.entity.game.GameInShop;
import com.gpb.game.resolver.StoreServiceResolver;
import com.gpb.game.service.StoreAggregatorService;
import com.gpb.game.service.StoreService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
public class StoreAggregatorServiceImpl implements StoreAggregatorService {

    private final StoreServiceResolver serviceResolver;

    private final ThreadPoolTaskExecutor threadPoolTaskExecutor;

    public StoreAggregatorServiceImpl(StoreServiceResolver serviceResolver, ThreadPoolTaskExecutor threadPoolTaskExecutor) {
        this.serviceResolver = serviceResolver;
        this.threadPoolTaskExecutor = threadPoolTaskExecutor;
    }

    @Override
    public List<Game> findGameByName(String name) {
        log.info("Searching game in stores with name: '{}'", name);
        List<Game> newGames = new ArrayList<>();

        for (StoreService service : serviceResolver.getAllServices()) {
            List<Game> found = service.findUncreatedGameByName(name);
            if (found.isEmpty()) continue;

            found.stream()
                    .filter(game -> isNameUnique(newGames, game))
                    .forEach(game -> {
                        setGameFromAllStores(game, service);
                        newGames.add(game);
                    });

            if (!newGames.isEmpty()) {
                log.info("{} new games were found by name '{}'", newGames.size(), name);
                return newGames;
            }
        }

        return newGames;
    }


    private boolean isNameUnique(List<Game> games, Game createdGame) {
        return games.stream()
                .map(Game::getName)
                .noneMatch(name -> name.equals(createdGame.getName()));
    }

    @Override
    public Game findGameByUrl(String link) {
        StoreService storeService = serviceResolver.getByUrl(link);
        Optional<Game> gameOptional = storeService.findUncreatedGameByUrl(link);

        return gameOptional.map(game -> {
            setGameFromAllStores(game, storeService);
            return game;
        }).orElseThrow(() -> new NotFoundException("app.game.error.url.not.found"));
    }

    @Override
    public GameInShop findGameInShopByUrl(String url) {
        StoreService storeService = serviceResolver.getByUrl(url);
        return storeService.findByUrl(url)
                .orElseThrow(() -> new NotFoundException("app.game.error.url.not.found"));
    }

    @Override
    public List<GameInShop> checkGameInStoreForChange(List<GameInShop> games) {
        log.info("Checking {} games in stores for changes", games.size());

        List<CompletableFuture<List<GameInShop>>> futures = serviceResolver.getAllHosts().stream()
                .map(host -> CompletableFuture.supplyAsync(() -> {
                    List<GameInShop> gameListForHost = games.stream()
                            .filter(g -> isNeededHost(host, g.getUrl()))
                            .toList();

                    StoreService storeService = serviceResolver.getByHost(host);
                    return storeService.checkGameInStoreForChange(gameListForHost);
                }, threadPoolTaskExecutor))
                .toList();

        return futures.stream()
                .map(CompletableFuture::join)
                .flatMap(List::stream)
                .toList();
    }


    private boolean isNeededHost(String storeHost, String gameUrl) {
        try {
            URL url = new URL(gameUrl);
            return storeHost.equals(url.getHost());
        } catch (MalformedURLException e) {
            log.info("URL '{}' is invalid for host comparison: {}", gameUrl, e.getMessage());
            return false;
        }
    }

    private void setGameFromAllStores(Game game, StoreService serviceToSkip) {
        log.info("Enriching game '{}' with data from other stores", game.getName());

        Collection<StoreService> services = serviceResolver.getAllExcept(serviceToSkip);

        List<CompletableFuture<Optional<GameInShop>>> futures = services.stream()
                .map(service -> CompletableFuture
                        .supplyAsync(() -> service.findByName(game.getName()), threadPoolTaskExecutor))
                .toList();

        List<GameInShop> gameInShopList = futures.stream()
                .map(CompletableFuture::join)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();

        game.getGamesInShop().addAll(gameInShopList);
        gameInShopList.forEach(inShop -> inShop.setGame(game));
    }
}
