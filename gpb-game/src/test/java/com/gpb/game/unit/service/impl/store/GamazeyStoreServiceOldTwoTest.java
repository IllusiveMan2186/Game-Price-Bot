package com.gpb.game.unit.service.impl.store;

import com.gpb.common.entity.game.ClientActivationType;
import com.gpb.common.entity.game.Genre;
import com.gpb.common.entity.game.ProductType;
import com.gpb.common.exception.NotFoundException;
import com.gpb.game.configuration.mapper.GamazeyEnumMapper;
import com.gpb.game.entity.game.Game;
import com.gpb.game.entity.game.GameInShop;
import com.gpb.game.parser.StorePageParser;
import com.gpb.game.service.ResourceService;
import com.gpb.game.service.impl.sda.GamazeyStoreServiceOldTwo;
import com.gpb.game.util.Constants;
import com.gpb.game.util.store.GamazeyConstants;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.* ;

/**
class GamazeyStoreServiceOldTwoTest {

    private GamazeyEnumMapper gamazeyEnumMapper = new GamazeyEnumMapper();
    @Mock
    private StorePageParser pageFetcher;
    @Mock
    private ResourceService resourceService;

    @Mock
    private Map<String, Genre> genreMap;

    @Mock
    private Map<String, ProductType> productTypeMap;

    @InjectMocks
    private GamazeyStoreServiceOldTwo storeService;

    @BeforeEach
    void setUp() {
        storeService = new GamazeyStoreServiceOldTwo(
                resourceService,
                pageFetcher,
                gamazeyEnumMapper.gamazeyGenreMap(),
                gamazeyEnumMapper.gamazeyProductTypeMap(),
                gamazeyEnumMapper.gamazeyClientActivationMap());
    }

    @Test
    void testFindUncreatedGameByUrl_whenSuccess_shouldReturnGame() {
        String url = "https://example.com/game";
        Document document = mock(Document.class);
        when(pageFetcher.getPage(url)).thenReturn(document);

        setUpParseGamePage(
                document,
                "Test Game - Some DLC (Xbox One) - Xbox Live Key - UNITED STATES",
                "1 200 ₴",
                "КАТАЛОГ ПРОДУКЦІЇ Gaming DLCs Test Game - Some Dlc (Xbox One) - Xbox Live Key - UNITED STATES",
                true,
                false);


        Game result = storeService.findUncreatedGameByUrl(url);


        assertNotNull(result);
        GameInShop expectedGameInShop = GameInShop.builder()
                .nameInStore("Test Game - Some DLC")
                .price(new BigDecimal("1200"))
                .discountPrice(new BigDecimal("1200"))
                .discount(0)
                .clientType(ClientActivationType.MICROSOFT)
                .isAvailable(true)
                .url(url).build();
        Game expectedGame = Game.builder()
                .name(expectedGameInShop.getNameInStore())
                .type(ProductType.ADDITION)
                .genres(new ArrayList<>())
                .gamesInShop(Set.of(expectedGameInShop))
                .build();

        assertEquals(expectedGame, result);
        verify(resourceService).cropImage(
                "imgUrl",
                "Test Game - Some DLC",
                GamazeyConstants.GAME_IMAGE_CROP_WIDTH_START,
                0,
                GamazeyConstants.GAME_IMAGE_CROP_WIDTH_LONG,
                GamazeyConstants.GAME_IMAGE_HEIGHT
        );
    }

    @Test
    void testFindUncreatedGameByUrl_whenImageNotFound_shouldNotCallCropImage() {
        String url = "https://example.com/game";
        Document document = mock(Document.class);
        when(pageFetcher.getPage(url)).thenReturn(document);

        setUpParseGamePage(
                document,
                "Test Game - Some DLC (Xbox One) - Xbox Live Key - UNITED STATES",
                "1 200 ₴",
                "КАТАЛОГ ПРОДУКЦІЇ Gaming DLCs Test Game - Some Dlc (Xbox One) - Xbox Live Key - UNITED STATES",
                true,
                true);
        when(document.getElementsByClass(GamazeyConstants.GAME_IMG_CLASS)).thenReturn(new Elements());


        Game result = storeService.findUncreatedGameByUrl(url);


        assertNotNull(result);
        verify(resourceService, times(0)).cropImage(
                "imgUrl",
                "Test Game - Some DLC",
                GamazeyConstants.GAME_IMAGE_CROP_WIDTH_START,
                0,
                GamazeyConstants.GAME_IMAGE_CROP_WIDTH_LONG,
                GamazeyConstants.GAME_IMAGE_HEIGHT
        );
    }

    @Test
    void testFindUncreatedGameByUrl_whenPageNotHaveFiled_shouldThrowException() {
        String url = "https://example.com/game";
        Document document = mock(Document.class);
        when(pageFetcher.getPage(url)).thenReturn(document);
        when(document.getElementsByClass(GamazeyConstants.GAME_PAGE_NAME_FIELD)).thenReturn(new Elements());

        when(document.select(GamazeyConstants.GAME_PRICE_FIELD)).thenReturn(new Elements());


        assertThrows(
                NotFoundException.class,
                () -> storeService.findUncreatedGameByUrl(url),
                "app.game.error.url.not.found");

    }

    @Test
    void testFindByUrl_whenSuccess_shouldReturnGameInStore() {
        String url = "https://example.com/game";
        Document document = mock(Document.class);

        when(pageFetcher.getPage(url)).thenReturn(document);
        setUpParseGamePage(
                document,
                "Test Game (Steam)",
                "50 ₴",
                "",
                true,
                true);


        GameInShop result = storeService.findByUrl(url);


        assertNotNull(result);
        assertEquals("Test Game", result.getNameInStore());
    }

    @Test
    void testFindByUrl_whenPageNotHaveFiled_shouldThrowException() {
        String url = "https://example.com/game";
        Document document = mock(Document.class);
        when(pageFetcher.getPage(url)).thenReturn(document);
        when(document.getElementsByClass(GamazeyConstants.GAME_PAGE_NAME_FIELD)).thenReturn(new Elements());

        when(document.select(GamazeyConstants.GAME_PRICE_FIELD)).thenReturn(new Elements());


        assertThrows(NotFoundException.class, () -> storeService.findByUrl(url), "app.game.error.url.not.found");
    }

    @Test
    void testFindUncreatedGameByName_whenSuccess_shouldReturnGame() {
        String name = "Test Game";
        String gameUrl = "https/some.url.com";
        Document gameSearchUrl = mock(Document.class);
        Document gamePage = mock(Document.class);
        Element listElement = mock(Element.class);
        when(pageFetcher.getPage(GamazeyConstants.CARDMAQ_SEARCH_URL + name)).thenReturn(gameSearchUrl);
        when(listElement.attr(Constants.ATTRIBUTE_HREF)).thenReturn(gameUrl);
        when(gameSearchUrl.getElementsByClass(GamazeyConstants.GAME_IN_LIST)).thenReturn(new Elements(listElement));
        when(pageFetcher.getPage(gameUrl)).thenReturn(gamePage);
        setUpParseGamePage(
                gamePage,
                "Test Game (Steam)",
                "50 ₴",
                "",
                true,
                false);


        List<Game> result = storeService.findUncreatedGameByName(name);


        assertEquals(1, result.size());
        GameInShop expectedGameInShop = GameInShop.builder()
                .nameInStore("Test Game")
                .price(new BigDecimal("50"))
                .discountPrice(new BigDecimal("50"))
                .discount(0)
                .isAvailable(true)
                .url("https/some.url.com").build();
        Game expectedGame = Game.builder()
                .name(expectedGameInShop.getNameInStore())
                .type(ProductType.GAME)
                .genres(new ArrayList<>())
                .gamesInShop(Set.of(expectedGameInShop))
                .build();
        assertEquals(expectedGame, result.get(0));
    }

    @Test
    void testFindByName_whenSuccess_shouldReturnGameInShop() {
        String name = "Test Game";
        String gameUrl = "https/some.url.com";
        Document gameSearchUrl = mock(Document.class);
        Document gamePage = mock(Document.class);
        Element listElement = mock(Element.class);
        when(pageFetcher.getPage(GamazeyConstants.CARDMAQ_SEARCH_URL + name)).thenReturn(gameSearchUrl);
        when(listElement.attr(Constants.ATTRIBUTE_HREF)).thenReturn(gameUrl);
        when(gameSearchUrl.getElementsByClass(GamazeyConstants.GAME_IN_LIST)).thenReturn(new Elements(listElement));
        when(pageFetcher.getPage(gameUrl)).thenReturn(gamePage);
        setUpParseGamePage(
                gamePage,
                "Test Game (Steam)",
                "50 ₴",
                "",
                true,
                true);


        GameInShop result = storeService.findByName(name);


        assertNotNull(result);
        GameInShop expectedGameInShop = GameInShop.builder()
                .nameInStore("Test Game")
                .price(new BigDecimal("50"))
                .discountPrice(new BigDecimal("50"))
                .discount(0)
                .isAvailable(true)
                .url(gameUrl).build();
        assertEquals(expectedGameInShop, result);
    }

    @Test
    void testCheckGameInStoreForChange_whenInfoChanged_shouldReturnListWithChangedGame() {
        GameInShop existingGame = GameInShop.builder()
                .nameInStore("Test Game")
                .price(new BigDecimal("50"))
                .discountPrice(new BigDecimal("50"))
                .isAvailable(true)
                .url("url")
                .build();
        Document document = mock(Document.class);

        when(pageFetcher.getPage(existingGame.getUrl())).thenReturn(document);
        setUpParseGamePage(
                document,
                "Test Game",
                "55 ₴",
                "",
                true,
                true);


        List<GameInShop> result = storeService.checkGameInStoreForChange(List.of(existingGame));


        assertEquals(1, result.size());
        assertEquals(new BigDecimal("55"), result.get(0).getPrice());
        assertEquals(new BigDecimal("55"), result.get(0).getPrice());
        assertTrue(result.get(0).isAvailable());
    }

    @Test
    void testCheckGameInStoreForChange_whenInfoNotChanged_shouldReturnEmptyList() {
        GameInShop existingGame = GameInShop.builder()
                .nameInStore("Test Game")
                .price(new BigDecimal("50"))
                .discountPrice(new BigDecimal("50"))
                .isAvailable(true)
                .url("url")
                .build();
        Document document = mock(Document.class);

        when(pageFetcher.getPage(existingGame.getUrl())).thenReturn(document);
        setUpParseGamePage(
                document,
                "Test Game",
                "50 ₴",
                "",
                true,
                true);


        List<GameInShop> result = storeService.checkGameInStoreForChange(List.of(existingGame));


        assertTrue(result.isEmpty());
    }

    void setUpParseGamePage(Document document,
                            String name,
                            String price,
                            String discountPrice,
                            String discount,
                            String title,
                            boolean isAvailable,
                            boolean isRegistered) {
        Element nameElement = mock(Element.class);
        Element priceElement = mock(Element.class);
        Element discountPriceElement = mock(Element.class);
        Element discountElement = mock(Element.class);
        Element availableElement = mock(Element.class);
        Element titleElement = mock(Element.class);

        when(document.getElementsByClass(GamazeyConstants.GAME_PAGE_NAME_FIELD))
                .thenReturn(new Elements(nameElement));
        when(nameElement.text()).thenReturn(name);

        when(document.select(GamazeyConstants.GAME_PAGE_OLD_PRICE_FIELD))
                .thenReturn(new Elements(priceElement));
        when(priceElement.text()).thenReturn(price);

        when(document.select(GamazeyConstants.GAME_PAGE_DISCOUNT_PRICE_FIELD))
                .thenReturn(new Elements(discountPriceElement));
        when(discountPriceElement.text()).thenReturn(discountPrice);

        when(document.select(GamazeyConstants.GAME_PAGE_DISCOUNT_FIELD))
                .thenReturn(new Elements(discountElement));
        when(discountElement.text()).thenReturn(discount);

        if (isAvailable) {
            when(document.getElementsByClass(GamazeyConstants.GAME_PAGE_IS_AVAILABLE))
                    .thenReturn(new Elements(availableElement));
        }

        if (!isRegistered) {
            Element imageElement = mock(Element.class);
            when(document.getElementsByClass(GamazeyConstants.GAME_IMG_CLASS))
                    .thenReturn(new Elements(imageElement));
            when(imageElement.attr("src")).thenReturn("imgUrl");
        }
    }
}*/