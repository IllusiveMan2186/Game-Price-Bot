package com.gpb.web.service.impl;

import com.gpb.web.bean.Game;
import com.gpb.web.bean.GameInShop;
import com.gpb.web.exception.NotFoundException;
import com.gpb.web.service.GameStoresService;
import com.gpb.web.service.StoreService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class GameStoresServiceImpl implements GameStoresService {

    private final Map<String, StoreService> storeService;

    public GameStoresServiceImpl(Map<String, StoreService> storeService) {
        this.storeService = storeService;
    }

    @Override
    public Game findGameByName(String name) {
        for (StoreService service : storeService.values()) {
            Game game = service.findUncreatedGameByName(name);
            if (game != null) {
                setGameFromAllStores(game, service);
                return game;
            }
        }
        throw new NotFoundException(String.format("Game with name '%s' not found", name));
    }

    @Override
    public Game findGameByUrl(String url) {
        for (StoreService service : storeService.values()) {
            Game game = service.findUncreatedGameByUrl(url);
            if (game != null) {
                setGameFromAllStores(game, service);
                return game;
            }
        }
        throw new NotFoundException(String.format("Game with url '%s' not found", url));
    }

    private void setGameFromAllStores(Game game, StoreService serviceToSkip) {
        Collection<StoreService> services = storeService.values();
        services.remove(serviceToSkip);

        List<GameInShop> gameInShopList = services.stream()
                .map(storeService1 -> storeService1.findByName(game.getName()))
                .toList();

        game.getGamesInShop().addAll(gameInShopList);
    }
}
