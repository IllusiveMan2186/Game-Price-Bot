package com.gpb.game.service.impl;

import com.gpb.game.entity.game.Game;
import com.gpb.game.entity.game.GameInShop;
import com.gpb.game.service.GameStoresService;
import com.gpb.game.service.StoreService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Service
public class GameStoresServiceImpl implements GameStoresService {

    private final Map<String, StoreService> storeServices;

    public GameStoresServiceImpl(Map<String, StoreService> storeServices) {
        this.storeServices = storeServices;
    }

    @Override
    public List<Game> findGameByName(String name) {

        log.info("Getting host from link : '{}'", name);
        List<Game> games = new ArrayList<>();
        for (StoreService service : storeServices.values()) {

            List<Game> createdGames = service.findUncreatedGameByName(name);
            for (Game createdGame : createdGames) {
                if (isName(games, createdGame)) {
                    setGameFromAllStores(createdGame, service);
                    games.add(createdGame);
                }
            }
        }
        return games;
    }

    private boolean isName(List<Game> games, Game createdGame) {
        return games.stream()
                .map(Game::getName)
                .noneMatch(gameName -> gameName.equals(createdGame.getName()));
    }

    @Override
    public Game findGameByUrl(String link) {
        try {
            log.info("Getting host from link : '{}'", link);
            URL url = new URL(link);
            StoreService storeService = storeServices.get(url.getHost());

            log.info("Getting store service from link : '{}'", url.getHost());
            Game game = storeService.findUncreatedGameByUrl(link);
            setGameFromAllStores(game, storeService);
            return game;
        } catch (MalformedURLException e) {
            log.info("Game with url {} not found cause of exception : {}}", link, e.getMessage());
        }
        return null;
    }

    @Override
    public List<GameInShop> checkGameInStoreForChange(List<GameInShop> games) {
        log.info("Check {} games from wishlist in stores for changes ", games.size());
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
            log.info("Game with url '{}' not found during comparison with host '{}' " +
                    "cause of exception : '{}'", gameUrl, storeHost, e.getMessage());
        }
        return false;
    }

    private void setGameFromAllStores(Game game, StoreService serviceToSkip) {
        log.info("Set game from all stores for  : '{}'", game.getName());
        ArrayList<StoreService> services = new ArrayList<>(storeServices.values());
        services.remove(serviceToSkip);

        List<GameInShop> gameInShopList = services.stream()
                .map(storeService1 -> storeService1.findByName(game.getName()))
                .filter(Objects::nonNull)
                .toList();

        game.getGamesInShop().addAll(gameInShopList);
        gameInShopList.stream().forEach(gameInShop -> gameInShop.setGame(game));
    }
}
