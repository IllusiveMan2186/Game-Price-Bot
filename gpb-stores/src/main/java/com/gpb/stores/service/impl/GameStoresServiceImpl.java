package com.gpb.stores.service.impl;

import com.gpb.stores.bean.game.Game;
import com.gpb.stores.bean.game.GameInShop;
import com.gpb.stores.service.GameStoresService;
import com.gpb.stores.service.StoreService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class GameStoresServiceImpl implements GameStoresService {

    private final Map<String, StoreService> storeServices;

    public GameStoresServiceImpl(Map<String, StoreService> storeServices) {
        this.storeServices = storeServices;
    }

    @Override
    public List<Game> findGameByName(String name) {

        log.info(String.format("Getting host from link : '%s'", name));
        List<Game> games = new ArrayList<>();
        for (StoreService service : storeServices.values()) {

            List<Game> createdGames = service.findUncreatedGameByName(name);
            for (Game createdGame : createdGames) {
                if (games.stream()
                        .map(Game::getName)
                        .noneMatch(gameName -> gameName.equals(createdGame.getName()))) {
                    setGameFromAllStores(createdGame, service);
                    games.add(createdGame);
                }
            }
        }
        return games;
    }

    @Override
    public Game findGameByUrl(String link) {
        try {
            log.info(String.format("Getting host from link : '%s'", link));
            URL url = new URL(link);
            StoreService storeService = storeServices.get(url.getHost());

            log.info(String.format("Getting store service from link : '%s'", url.getHost()));
            Game game = storeService.findUncreatedGameByUrl(link);
            setGameFromAllStores(game, storeService);
            return game;
        } catch (MalformedURLException e) {
            log.info("Game with url {} not found cause of exception : {}}", link, e.getMessage());
        }
        return null;
    }

    @Async
    @Override
    public void subscribeToGame(Game game) {
        try {
            for (GameInShop gameInShop : game.getGamesInShop()) {
                URL url = new URL(gameInShop.getUrl());
                StoreService storeService = storeServices.get(url.getHost());
                storeService.subscribeToGame(gameInShop);
            }
        } catch (MalformedURLException e) {
            log.info(String.format("Not found cause of exception : '%s'", e.getMessage()));
        }
    }

    @Async
    @Override
    public void unsubscribeFromGame(Game game) {
        try {
            for (GameInShop gameInShop : game.getGamesInShop()) {
                URL url = new URL(gameInShop.getUrl());
                StoreService storeService = storeServices.get(url.getHost());
                storeService.unsubscribeFromGame(gameInShop);
            }
        } catch (MalformedURLException e) {
            log.info(String.format("Not found cause of exception : '%s'", e.getMessage()));
        }
    }

    @Override
    public List<GameInShop> checkGameInStoreForChange(List<GameInShop> games) {
        log.info(String.format("Check %s games from wishlist in stores for changes ", games.size()));
        List<GameInShop> changedGameInShopList = new ArrayList<>();

        for (String host : storeServices.keySet()) {
            List<GameInShop> gameInShopListForHost = games.stream()
                    .filter(g -> isNeededHost(host, g.getUrl()))
                    .toList();

            StoreService storeService = storeServices.get(host);
            changedGameInShopList.addAll(storeService.checkGameInStoreForChange(gameInShopListForHost));
        }

        return changedGameInShopList;
    }

    private boolean isNeededHost(String storeHost, String gameUrl) {
        try {
            URL url = new URL(gameUrl);
            return storeHost.equals(url.getHost());
        } catch (MalformedURLException e) {
            log.info(String.format("Game with url '%s' not found during comparison with host '%s' " +
                    "cause of exception : '%s'", gameUrl, storeHost, e.getMessage()));
        }
        return false;
    }

    private void setGameFromAllStores(Game game, StoreService serviceToSkip) {
        log.info(String.format("Set game from all stores for  : '%s'", game.getName()));
        ArrayList<StoreService> services = new ArrayList<>(storeServices.values());
        services.remove(serviceToSkip);

        List<GameInShop> gameInShopList = services.stream()
                .map(storeService1 -> storeService1.findByName(game.getName()))
                .toList();

        game.getGamesInShop().addAll(gameInShopList);
    }
}
