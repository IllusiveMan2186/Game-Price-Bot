package com.gpb.game.unit.service.impl.store;

import com.gpb.common.entity.game.Genre;
import com.gpb.common.entity.game.ProductType;
import com.gpb.game.entity.game.Game;
import com.gpb.game.entity.game.GameInShop;
import com.gpb.game.parser.StorePageParser;
import com.gpb.game.parser.StoreParser;
import com.gpb.game.service.impl.store.CommonStoreServiceImpl;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommonStoreServiceImplTest {

    private static final String TEST_URL = "https://example.com/game";
    private static final String TEST_NAME = "Test Game";

    @Mock
    private StorePageParser pageFetcher;

    @Mock
    private StoreParser storeParser;

    @Mock
    private Document document;

    @Mock
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    @InjectMocks
    private CommonStoreServiceImpl storeService;

    private GameInShop gameInShop;
    private Game game;

    @BeforeEach
    void setUp() {
        gameInShop = GameInShop.builder()
                .nameInStore(TEST_NAME)
                .price(BigDecimal.TEN)
                .discountPrice(BigDecimal.valueOf(8))
                .isAvailable(true)
                .url(TEST_URL)
                .build();

        game = Game.builder()
                .name(TEST_NAME)
                .gamesInShop(Set.of(gameInShop))
                .build();
    }

    @Test
    void testFindUncreatedGameByUrl_whenGameFound_shouldReturnGame() {
        when(pageFetcher.getPage(TEST_URL)).thenReturn(Optional.of(document));
        when(storeParser.parseGameInShopFromPage(document)).thenReturn(gameInShop);
        when(storeParser.getGenres(document)).thenReturn(List.of(Genre.ACTION, Genre.ADVENTURES));
        when(storeParser.getProductType(document)).thenReturn(ProductType.GAME);


        Optional<Game> result = storeService.findUncreatedGameByUrl(TEST_URL);


        assertTrue(result.isPresent());
        assertNotNull(result);
        assertEquals(TEST_NAME, result.get().getName());
        assertEquals(1, result.get().getGamesInShop().size());
        verify(storeParser).saveImage(document);
    }

    @Test
    void testFindUncreatedGameByUrl_whenGameNotFound_shouldOptionalEmpty() {
        when(pageFetcher.getPage(TEST_URL)).thenReturn(Optional.of(document));
        when(storeParser.parseGameInShopFromPage(document)).thenReturn(null);


        Optional<Game> result = storeService.findUncreatedGameByUrl(TEST_URL);


        assertTrue(result.isEmpty());
    }

    @Test
    void testFindByUrl_whenGameFound_shouldReturnGameInShop() {
        when(pageFetcher.getPage(TEST_URL)).thenReturn(Optional.of(document));
        when(storeParser.parseGameInShopFromPage(document)).thenReturn(gameInShop);


        Optional<GameInShop> result = storeService.findByUrl(TEST_URL);


        assertTrue(result.isPresent());
        assertNotNull(result);
        assertEquals(TEST_URL, result.get().getUrl());
    }

    @Test
    void testFindByUrl_whenGameNotFound_shouldReturnOptionalEmpty() {
        when(pageFetcher.getPage(TEST_URL)).thenReturn(Optional.of(document));
        when(storeParser.parseGameInShopFromPage(document)).thenReturn(null);


        Optional<GameInShop> result = storeService.findByUrl(TEST_URL);


        assertTrue(result.isEmpty());
    }

    @Test
    void testFindUncreatedGameByName_whenGameFound_shouldReturnGame() {
        String secondUrl = "https://example.com/invalid";

        when(storeParser.parseSearchResults(TEST_NAME, pageFetcher)).thenReturn(List.of(TEST_URL, secondUrl));

        when(pageFetcher.getPage(TEST_URL)).thenReturn(Optional.of(document));
        when(storeParser.parseGameInShopFromPage(document)).thenReturn(gameInShop);
        when(storeParser.getGenres(document)).thenReturn(List.of(Genre.ACTION));
        when(storeParser.getProductType(document)).thenReturn(ProductType.GAME);

        when(pageFetcher.getPage(secondUrl)).thenReturn(Optional.empty());
        doAnswer(invocation -> {
            Runnable runnable = invocation.getArgument(0);
            runnable.run();
            return null;
        }).when(threadPoolTaskExecutor).execute(any(Runnable.class));

        List<Game> results = storeService.findUncreatedGameByName(TEST_NAME);

        assertEquals(1, results.size());
        assertEquals(TEST_NAME, results.get(0).getName());
    }

    @Test
    void testFindByName_whenGameFound_shouldReturnGameInShop() {
        when(storeParser.parseSearchResults(TEST_NAME, pageFetcher)).thenReturn(List.of(TEST_URL));
        when(pageFetcher.getPage(TEST_URL)).thenReturn(Optional.of(document));
        when(storeParser.parseGameInShopFromPage(document)).thenReturn(gameInShop);


        Optional<GameInShop> result = storeService.findByName(TEST_NAME);


        assertTrue(result.isPresent());
        assertNotNull(result);
        assertEquals(TEST_URL, result.get().getUrl());
    }

    @Test
    void testFindByName_whenPageNotFound_shouldReturnOptionalEmpty() {
        when(storeParser.parseSearchResults(TEST_NAME, pageFetcher)).thenReturn(Collections.emptyList());


        Optional<GameInShop> result = storeService.findByName(TEST_NAME);


        assertTrue(result.isEmpty());
    }

    @Test
    void testCheckGameInStoreForChange_whenNoChanges_shouldReturnEmptyList() {
        when(pageFetcher.getPage(TEST_URL)).thenReturn(Optional.of(document));
        when(storeParser.parseGameInShopFromPage(document)).thenReturn(gameInShop);
        doAnswer(invocation -> {
            Runnable runnable = invocation.getArgument(0);
            runnable.run();
            return null;
        }).when(threadPoolTaskExecutor).execute(any(Runnable.class));

        List<GameInShop> results = storeService.checkGameInStoreForChange(List.of(gameInShop));

        assertTrue(results.isEmpty());
    }

    @Test
    void testCheckGameInStoreForChange_whenWithChanges_shouldReturnUpdatedGame() {
        GameInShop updatedGame = GameInShop.builder()
                .nameInStore(TEST_NAME)
                .price(BigDecimal.valueOf(12))
                .discountPrice(BigDecimal.valueOf(10))
                .isAvailable(true)
                .url(TEST_URL)
                .build();

        when(pageFetcher.getPage(TEST_URL)).thenReturn(Optional.of(document));
        when(storeParser.parseGameInShopFromPage(document)).thenReturn(updatedGame);
        doAnswer(invocation -> {
            Runnable runnable = invocation.getArgument(0);
            runnable.run();
            return null;
        }).when(threadPoolTaskExecutor).execute(any(Runnable.class));


        List<GameInShop> results = storeService.checkGameInStoreForChange(List.of(gameInShop));


        assertFalse(results.isEmpty());
        assertEquals(1, results.size());
        assertEquals(BigDecimal.valueOf(12), results.get(0).getPrice());
    }
}
