
package com.gpb.game.unit.service.impl;

import com.gpb.common.exception.NotFoundException;
import com.gpb.game.entity.game.Game;
import com.gpb.game.entity.game.GameInShop;
import com.gpb.game.resolver.StoreServiceResolver;
import com.gpb.game.service.StoreService;
import com.gpb.game.service.impl.StoreAggregatorServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StoreAggregatorServiceImplTest {

    @Mock
    StoreService storeService1;

    @Mock
    StoreService storeService2;

    @Mock
    StoreServiceResolver storeServiceResolver;

    @Mock
    ThreadPoolTaskExecutor threadPoolTaskExecutor;

    @InjectMocks
    StoreAggregatorServiceImpl gameStoreAggregatorService;

    @Test
    void testGetGameByName_whenSuccess_shouldReturnNewGame() {
        String name = "name";
        final Game game = new Game();
        final GameInShop gameInShop1 = new GameInShop();
        gameInShop1.setId(1);
        final GameInShop gameInShop2 = new GameInShop();
        game.setGamesInShop(Collections.singleton(gameInShop2));

        when(storeServiceResolver.getAllServices()).thenReturn(List.of(storeService1, storeService2));
        when(storeService2.findUncreatedGameByName(name)).thenReturn(List.of(game));
        when(storeService1.findUncreatedGameByName(name)).thenReturn(new ArrayList<>());
        when(storeServiceResolver.getAllExcept(storeService2)).thenReturn(List.of(storeService1));

        doAnswer(invocation -> {
            Runnable runnable = invocation.getArgument(0);
            runnable.run();
            return null;
        }).when(threadPoolTaskExecutor).execute(any(Runnable.class));

        Set<GameInShop> combined = new HashSet<>(Set.of(gameInShop1, gameInShop2));
        game.setGamesInShop(combined);

        List<Game> result = gameStoreAggregatorService.findGameByName(name);

        assertEquals(List.of(game), result);
    }

    @Test
    void testGetGameByName_whenNotFound_shouldReturnEmptyList() {
        String name = "name";
        when(storeServiceResolver.getAllServices()).thenReturn(List.of(storeService1, storeService2));
        when(storeService1.findUncreatedGameByName(name)).thenReturn(new ArrayList<>());
        when(storeService2.findUncreatedGameByName(name)).thenReturn(new ArrayList<>());

        List<Game> games = gameStoreAggregatorService.findGameByName(name);

        assertEquals(Collections.emptyList(), games);
    }

    @Test
    void testFindGameByUrl_whenSuccess_ShouldReturnNewGame() {
        final Game game = new Game();
        game.setName("name");
        final GameInShop gameInShop1 = new GameInShop();
        gameInShop1.setUrl("url");
        final GameInShop gameInShop2 = new GameInShop();
        game.setGamesInShop(Collections.singleton(gameInShop2));
        String url = "https://gamazey.com.ua/games/steam/sid-meiers-civilization-vi";

        when(storeServiceResolver.getByUrl(url)).thenReturn(storeService2);
        when(storeService2.findUncreatedGameByUrl(url)).thenReturn(Optional.of(game));
        when(storeServiceResolver.getAllExcept(storeService2)).thenReturn(List.of(storeService1));
        when(storeService1.findByName(game.getName())).thenReturn(Optional.of(gameInShop1));

        doAnswer(invocation -> {
            Runnable runnable = invocation.getArgument(0);
            runnable.run();
            return null;
        }).when(threadPoolTaskExecutor).execute(any(Runnable.class));

        Set<GameInShop> combined = new HashSet<>(Set.of(gameInShop1, gameInShop2));
        game.setGamesInShop(combined);



        Game result = gameStoreAggregatorService.findGameByUrl(url);

        assertEquals(game, result);
    }

    @Test
    void testFindGameByUrl_whenNotFound_shouldThrowException() {
        String url = "https://gamazey.com.ua/games/nonexistent";
        when(storeServiceResolver.getByUrl(url)).thenReturn(storeService2);
        when(storeService2.findUncreatedGameByUrl(url)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> gameStoreAggregatorService.findGameByUrl(url));
    }

    @Test
    void testFindGameInShopByUrl_whenSuccess_ShouldReturnGameInShop() {
        final GameInShop gameInShop = new GameInShop();
        String url = "https://gamazey.com.ua/games/steam/sid-meiers-civilization-vi";
        when(storeServiceResolver.getByUrl(url)).thenReturn(storeService2);
        when(storeService2.findByUrl(url)).thenReturn(Optional.of(gameInShop));

        GameInShop result = gameStoreAggregatorService.findGameInShopByUrl(url);

        assertEquals(gameInShop, result);
    }

    @Test
    void testCheckGamesInStoreForChange_whenSuccess_shouldReturnChangedGames() {
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

        when(storeServiceResolver.getAllHosts()).thenReturn(Set.of("storeService1", "gamazey.com.ua"));
        when(storeServiceResolver.getByHost("storeService1")).thenReturn(storeService1);
        when(storeServiceResolver.getByHost("gamazey.com.ua")).thenReturn(storeService2);
        when(storeService1.checkGameInStoreForChange(List.of(gameInShop1))).thenReturn(new ArrayList<>());
        when(storeService2.checkGameInStoreForChange(List.of(gameInShop2, gameInShop3))).thenReturn(expected);

        doAnswer(invocation -> {
            Runnable runnable = invocation.getArgument(0);
            runnable.run();
            return null;
        }).when(threadPoolTaskExecutor).execute(any(Runnable.class));

        List<GameInShop> result = gameStoreAggregatorService
                .checkGameInStoreForChange(List.of(gameInShop1, gameInShop2, gameInShop3));

        assertEquals(expected, result);
    }
}
