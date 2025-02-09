package com.gpb.game.unit.service.impl.store;

import com.gpb.common.entity.game.Genre;
import com.gpb.common.entity.game.ProductType;
import com.gpb.common.exception.NotFoundException;
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

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CommonStoreServiceImplTest {

    @Mock
    private StorePageParser pageFetcher;

    @Mock
    private StoreParser storeParser;

    @Mock
    private Document document;

    @InjectMocks
    private CommonStoreServiceImpl storeService;

    private final String TEST_URL = "https://example.com/game";
    private final String TEST_NAME = "Test Game";

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
    void findUncreatedGameByUrl_Success() {
        when(pageFetcher.getPage(TEST_URL)).thenReturn(document);
        when(storeParser.parseGameInShopFromPage(document)).thenReturn(gameInShop);
        when(storeParser.getGenres(document)).thenReturn(List.of(Genre.ACTION, Genre.ADVENTURES));
        when(storeParser.getProductType(document)).thenReturn(ProductType.GAME);

        Game result = storeService.findUncreatedGameByUrl(TEST_URL);

        assertNotNull(result);
        assertEquals(TEST_NAME, result.getName());
        assertEquals(1, result.getGamesInShop().size());
        verify(storeParser).saveImage(document);
    }

    @Test
    void findUncreatedGameByUrl_NotFound() {
        when(pageFetcher.getPage(TEST_URL)).thenReturn(document);
        when(storeParser.parseGameInShopFromPage(document)).thenReturn(null);

        assertThrows(NotFoundException.class, () -> storeService.findUncreatedGameByUrl(TEST_URL));
    }

    @Test
    void findByUrl_Success() {
        when(pageFetcher.getPage(TEST_URL)).thenReturn(document);
        when(storeParser.parseGameInShopFromPage(document)).thenReturn(gameInShop);

        GameInShop result = storeService.findByUrl(TEST_URL);

        assertNotNull(result);
        assertEquals(TEST_URL, result.getUrl());
    }

    @Test
    void findByUrl_NotFound() {
        when(pageFetcher.getPage(TEST_URL)).thenReturn(document);
        when(storeParser.parseGameInShopFromPage(document)).thenReturn(null);

        assertThrows(NotFoundException.class, () -> storeService.findByUrl(TEST_URL));
    }

    @Test
    void findUncreatedGameByName_Success() {
        when(storeParser.parseSearchResults(TEST_NAME, pageFetcher)).thenReturn(List.of(TEST_URL));
        when(pageFetcher.getPage(TEST_URL)).thenReturn(document);
        when(storeParser.parseGameInShopFromPage(document)).thenReturn(gameInShop);
        when(storeParser.getGenres(document)).thenReturn(List.of(Genre.ARCADE));
        when(storeParser.getProductType(document)).thenReturn(ProductType.GAME);

        List<Game> results = storeService.findUncreatedGameByName(TEST_NAME);

        assertFalse(results.isEmpty());
        assertEquals(1, results.size());
        assertEquals(TEST_NAME, results.get(0).getName());
    }

    @Test
    void findByName_Success() {
        when(storeParser.parseSearchResults(TEST_NAME, pageFetcher)).thenReturn(List.of(TEST_URL));
        when(pageFetcher.getPage(TEST_URL)).thenReturn(document);
        when(storeParser.parseGameInShopFromPage(document)).thenReturn(gameInShop);

        GameInShop result = storeService.findByName(TEST_NAME);

        assertNotNull(result);
        assertEquals(TEST_URL, result.getUrl());
    }

    @Test
    void checkGameInStoreForChange_NoChanges() {
        when(pageFetcher.getPage(TEST_URL)).thenReturn(document);
        when(storeParser.parseGameInShopFromPage(document)).thenReturn(gameInShop);

        List<GameInShop> results = storeService.checkGameInStoreForChange(List.of(gameInShop));

        assertTrue(results.isEmpty());
    }

    @Test
    void checkGameInStoreForChange_WithChanges() {
        GameInShop updatedGame = GameInShop.builder()
                .nameInStore(TEST_NAME)
                .price(BigDecimal.valueOf(12))
                .discountPrice(BigDecimal.valueOf(10))
                .isAvailable(true)
                .url(TEST_URL)
                .build();

        when(pageFetcher.getPage(TEST_URL)).thenReturn(document);
        when(storeParser.parseGameInShopFromPage(document)).thenReturn(updatedGame);

        List<GameInShop> results = storeService.checkGameInStoreForChange(List.of(gameInShop));

        assertFalse(results.isEmpty());
        assertEquals(1, results.size());
        assertEquals(BigDecimal.valueOf(12), results.get(0).getPrice());
    }
}
