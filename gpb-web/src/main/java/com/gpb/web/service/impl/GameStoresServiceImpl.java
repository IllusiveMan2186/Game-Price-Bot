package com.gpb.web.service.impl;

import com.gpb.web.bean.game.Game;
import com.gpb.web.bean.game.GameInShop;
import com.gpb.web.exception.NotFoundException;
import com.gpb.web.service.GameStoresService;
import com.gpb.web.service.StoreService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
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
    public Game findGameByName(String name) {
        for (StoreService service : storeServices.values()) {
            Game game = service.findUncreatedGameByName(name);
            if (game != null) {
                setGameFromAllStores(game, service);
                return game;
            }
        }
        log.info(String.format("Game with name : '%s' not found", name));
        throw new NotFoundException("app.game.error.name.not.found");
    }

    @Override
    public Game findGameByUrl(String link) {
        try {
            log.info(String.format("Getting host from link : '%s'", link));
            URL url = new URL(link);
            StoreService storeService = storeServices.get(url.getHost());
            if (storeService != null) {
                log.info(String.format("Getting store service from link : '%s'", url.getHost()));
                Game game = storeService.findUncreatedGameByUrl(link);
                if (game != null) {
                    setGameFromAllStores(game, storeService);
                    return game;
                }
            }
            log.info(String.format("Game with url '%s' not found for host '%s'", link, url.getHost()));
        } catch (MalformedURLException e) {
            log.info(String.format("Game with url '%s' not found cause of exception : '%s'", link, e.getMessage()));
        }
        throw new NotFoundException("app.game.error.url.not.found");
    }

    private void setGameFromAllStores(Game game, StoreService serviceToSkip) {
        log.info(String.format("Set game from all stores for  : '%s'", game.getName()));
        Collection<StoreService> services = storeServices.values();
        services.remove(serviceToSkip);

        List<GameInShop> gameInShopList = services.stream()
                .map(storeService1 -> storeService1.findByName(game.getName()))
                .toList();

        game.getGamesInShop().addAll(gameInShopList);
    }
}
