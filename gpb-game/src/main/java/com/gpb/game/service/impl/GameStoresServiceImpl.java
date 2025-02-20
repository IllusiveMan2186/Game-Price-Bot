package com.gpb.game.service.impl;

import com.gpb.common.exception.NotFoundException;
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
        List<Game> newGames = new ArrayList<>();
        for (StoreService service : storeServices.values()) {

            List<Game> foundedGames = service.findUncreatedGameByName(name);
            if (!foundedGames.isEmpty()) {
                for (Game createdGame : foundedGames) {
                    if (isName(newGames, createdGame)) {
                        setGameFromAllStores(createdGame, service);
                        newGames.add(createdGame);
                    }
                }
                log.info("{} new games was founded by name {}", newGames.size(), name);
                return newGames;
            }
        }
        return newGames;
    }

    private boolean isName(List<Game> games, Game createdGame) {
        return games.stream()
                .map(Game::getName)
                .noneMatch(gameName -> gameName.equals(createdGame.getName()));
    }

    @Override
    public Game findGameByUrl(String link) {
        StoreService storeService = getStoreFromHostUrl(link);
        Game game = storeService.findUncreatedGameByUrl(link);
        setGameFromAllStores(game, storeService);
        return game;

    }

    @Override
    public GameInShop findGameInShopByUrl(String url) {
        StoreService storeService = getStoreFromHostUrl(url);
        return storeService.findByUrl(url);
    }

    private StoreService getStoreFromHostUrl(String link) {
        log.info("Getting host from link : '{}'", link);
        try {
            URL url = new URL(link);
            StoreService storeService = storeServices.get(url.getHost());

            log.info("Getting store service from link : '{}'", url.getHost());

            if(storeService!=null){
                return storeService;
            }
            log.error("Store host '{}' not support by app.", url.getHost());
            throw new NotFoundException("app.game.error.host.not.supported");
        } catch (MalformedURLException e) {
            log.info("Game with url {} not found cause of exception : {}", link, e.getMessage());
            throw new NotFoundException("app.game.error.url.not.found");
        }
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

    /**
     * Searches for the game in different stores besides the one where it was found
     *
     * @param game          game for search
     * @param serviceToSkip service where it was found and that need to skip
     */
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
