package com.gpb.stores.service.impl;

import com.gpb.stores.bean.game.Game;
import com.gpb.stores.bean.game.GameInShop;
import com.gpb.stores.exception.NotFoundException;
import com.gpb.stores.service.GameStoresService;
import com.gpb.stores.service.StoreService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class GameStoresServiceImplTest {

    StoreService storeService1 = mock(StoreService.class);
    StoreService storeService2 = mock(StoreService.class);
    GameStoresService gameStoresService;

    @BeforeEach
    void beforeAllGame() {
        Map<String, StoreService> storeServiceMap = new HashMap<>();
        storeServiceMap.put("storeService1", storeService1);
        storeServiceMap.put("gamazey.com.ua", storeService2);
        gameStoresService = new GameStoresServiceImpl(storeServiceMap);
    }

    @Test
    void testGetGameByName_whenSuccessfully_ShouldReturnNewGame() {
        String name = "name";
        final Game game = new Game();
        final GameInShop gameInShop1 = new GameInShop();
        final GameInShop gameInShop2 = new GameInShop();
        game.setGamesInShop(Collections.singleton(gameInShop2));

        when(storeService2.findUncreatedGameByName(name)).thenReturn(List.of(game));
        when(storeService1.findUncreatedGameByName(name)).thenReturn(new ArrayList<>());
        when(storeService1.findByName(name)).thenReturn(gameInShop1);
        Set<GameInShop> copyOfList = new HashSet<>(Collections.singletonList(gameInShop2));
        copyOfList.add(gameInShop1);
        game.setGamesInShop(copyOfList);

        List<Game> result = gameStoresService.findGameByName(name);

        assertEquals(List.of(game), result);
    }

    //@Test
    void testGetGameByName_whenNotFound_ShouldThrowException() {
        final Game game = new Game();
        final GameInShop gameInShop1 = new GameInShop();
        game.setGamesInShop(Set.of(gameInShop1));
        String name = "name";
        when(storeService2.findUncreatedGameByName(name)).thenReturn(new ArrayList<>());

        assertThrows(NotFoundException.class, () -> gameStoresService.findGameByName(name), "app.game.error.name.not.found");
    }

    @Test
    void testGetGameByUrl_whenSuccessfully_ShouldReturnNewGame() {
        final Game game = new Game();
        final GameInShop gameInShop1 = new GameInShop();
        final GameInShop gameInShop2 = new GameInShop();
        game.setGamesInShop(Collections.singleton(gameInShop2));
        String url = "https://gamazey.com.ua/games/steam/sid-meiers-civilization-vi";
        when(storeService2.findUncreatedGameByUrl(url)).thenReturn(game);
        when(storeService1.findByUrl(url)).thenReturn(gameInShop1);
        Set<GameInShop> copyOfList = new HashSet<>(Collections.singletonList(gameInShop2));
        copyOfList.add(gameInShop1);
        game.setGamesInShop(copyOfList);

        Game result = gameStoresService.findGameByUrl(url);

        assertEquals(game, result);
    }

    //@Test
    void testGetGameByUrl_whenNotFound_thenShouldThrowException() {
        final Game game = new Game();
        final GameInShop gameInShop1 = new GameInShop();
        game.setGamesInShop(Set.of(gameInShop1));
        String url = "url";
        when(storeService2.findUncreatedGameByUrl(url)).thenReturn(null);

        assertThrows(NotFoundException.class, () -> gameStoresService.findGameByUrl(url), "");
    }

    @Test
    void testSubscribeToGame_whenSuccessfully_thenShouldSubscribeToGame() {
        final Game game = new Game();
        String url1 = "https://storeService1/games";
        String url2 = "https://gamazey.com.ua/games";
        final GameInShop gameInShop1 = new GameInShop();
        gameInShop1.setUrl(url1);
        final GameInShop gameInShop2 = new GameInShop();
        gameInShop2.setUrl(url2);
        game.setGamesInShop(Set.of(gameInShop1, gameInShop2));


        gameStoresService.subscribeToGame(game);

        verify(storeService1).subscribeToGame(gameInShop1);
        verify(storeService2).subscribeToGame(gameInShop2);
    }

    @Test
    void testUnsubscribeToGame_whenSuccessfully_thenShouldSubscribeToGame() {
        final Game game = new Game();
        String url1 = "https://storeService1/games";
        String url2 = "https://gamazey.com.ua/games";
        final GameInShop gameInShop1 = new GameInShop();
        gameInShop1.setUrl(url1);
        final GameInShop gameInShop2 = new GameInShop();
        gameInShop2.setUrl(url2);
        game.setGamesInShop(Set.of(gameInShop1, gameInShop2));

        gameStoresService.unsubscribeFromGame(game);

        verify(storeService1).unsubscribeFromGame(gameInShop1);
        verify(storeService2).unsubscribeFromGame(gameInShop2);
    }

    @Test
    void testCheckGamesInStoreForChange_whenSuccessfully_thenShouldReturnChangedGames() {
        String url1 = "https://storeService1/games";
        String url2 = "https://gamazey.com.ua/games";
        final GameInShop gameInShop1 = new GameInShop();
        gameInShop1.setUrl(url1);
        final GameInShop gameInShop2 = new GameInShop();
        gameInShop2.setUrl(url2);
        final GameInShop gameInShop3 = new GameInShop();
        gameInShop3.setUrl(url2);
        final GameInShop gameInShop4 = new GameInShop();
        List<GameInShop> expected = Collections.singletonList(gameInShop4);
        when(storeService1.checkGameInStoreForChange(List.of(gameInShop1))).thenReturn(new ArrayList<>());
        when(storeService2.checkGameInStoreForChange(List.of(gameInShop2, gameInShop3))).thenReturn(expected);

        List<GameInShop> result = gameStoresService
                .checkGameInStoreForChange(List.of(gameInShop1, gameInShop2, gameInShop3));

        assertEquals(expected, result);
    }
}