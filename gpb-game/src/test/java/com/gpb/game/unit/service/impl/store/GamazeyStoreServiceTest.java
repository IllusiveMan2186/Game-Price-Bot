package com.gpb.game.unit.service.impl.store;

import com.gpb.common.entity.game.ClientActivationType;
import com.gpb.common.entity.game.Genre;
import com.gpb.common.entity.game.ProductType;
import com.gpb.game.configuration.ResourceConfiguration;
import com.gpb.game.entity.game.Game;
import com.gpb.game.entity.game.GameInShop;
import com.gpb.game.parser.StorePageParser;
import com.gpb.game.service.impl.store.GamazeyStoreService;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GamazeyStoreServiceTest {

    private static final String GAME_PAGE_NAME_FIELD = "rm-product-title order-1 order-md-0";
    private static final String GAME_PAGE_OLD_PRICE_FIELD = "rm-product-center-price-old";
    private static final String GAME_PAGE_DISCOUNT_FIELD = "main-product-you-save";
    private static final String GAME_PAGE_IS_AVAILABLE = "rm-module-stock rm-out-of-stock";
    private static final String GAME_PAGE_CHARACTERISTICS = "rm-product-attr-list-item d-flex d-sm-block";
    private static final String GAME_PAGE_DISCOUNT_PRICE_FIELD = "rm-product-center-price";
    private static final String GAMEZEY_SEARCH_URL = "https://gamazey.com.ua/search?search=";
    private static final String GAME_IMG_CLASS = "img-fluid";

    private static final String GENRE_ELEMENT = """
            <div class="rm-product-attr-list-item d-flex d-sm-block">
            <div>Жанр</div>
            <div>Симуляторы, Стратегии</div>
            </div>""";

    StorePageParser parser = mock(StorePageParser.class);

    Map<String, Genre> genereMap = new HashMap<>();

    Map<String, ProductType> productTypeMap = Collections.singletonMap("Гра", ProductType.GAME);

    Map<String, ClientActivationType> clientActivationTypeMap
            = Collections.singletonMap("steam", ClientActivationType.STEAM);

    ResourceConfiguration resourceConfiguration = new ResourceConfiguration();

    GamazeyStoreService storeService = new GamazeyStoreService(parser, genereMap, productTypeMap,
            clientActivationTypeMap, resourceConfiguration);

    @Test
    void testGetUncreatedGameByUrl_whenSuccess_shouldReturnNewGame() {
        final GameInShop gameInShop = getGameInStore();
        final Game game = Game.builder()
                .name(gameInShop.getNameInStore())
                .type(ProductType.GAME)
                .gamesInShop(Collections.singleton(gameInShop))
                .build();
        gameInShop.setGame(game);
        String url = "url";

        getDocumentForUncreatedGameByUrl(gameInShop, url);

        Game result = storeService.findUncreatedGameByUrl(url);

        assertEquals("Game", result.getName());
        assertEquals(game.getGamesInShop().size(), result.getGamesInShop().size());
        assertEquals(game.getType(), result.getType());
        GameInShop resulGameInStore = result.getGamesInShop().stream().toList().get(0);
        assertEquals(gameInShop.getNameInStore(), resulGameInStore.getNameInStore());
        assertEquals(gameInShop.getDiscount(), resulGameInStore.getDiscount());
        assertEquals(gameInShop.getUrl(), resulGameInStore.getUrl());
        assertEquals(gameInShop.getGame().getName(), resulGameInStore.getGame().getName());
    }

    @Test
    void testGetGameInStoreByUrl_whenSuccess_shouldReturnNewGameInStore() {
        final GameInShop gameInShop = getGameInStore();
        String url = "url";

        getDocumentForGameByUrl(gameInShop, url);

        GameInShop result = storeService.findByUrl(url);

        assertEquals(gameInShop, result);
    }

    @Test
    void testFindUncreatedGameByName_whenSuccess_shouldReturnGameList() {
        final GameInShop gameInShop = getGameInStore();
        final Game game = Game.builder()
                .name(gameInShop.getNameInStore())
                .gamesInShop(Collections.singleton(gameInShop))
                .build();
        gameInShop.setGame(game);
        String url = "url";

        Document page = getDocumentForUncreatedGameByUrl(gameInShop, url);

        String name = "name";
        when(parser.getPage(GAMEZEY_SEARCH_URL + name)).thenReturn(page);
        Elements titleElements = mock(Elements.class);
        Element titleElement = mock(Element.class);
        Element hrefTitleElements = mock(Element.class);

        when(page.getElementsByClass("rm-module-title")).thenReturn(titleElements);
        when(titleElements.iterator()).thenReturn(Collections.singletonList(titleElement).iterator());
        when(titleElement.child(0)).thenReturn(hrefTitleElements);
        when(hrefTitleElements.attr("href")).thenReturn(url);


        List<Game> resultList = storeService.findUncreatedGameByName(name);
        Game result = resultList.get(0);

        assertEquals("Game", result.getName());
        assertEquals(game.getGamesInShop().size(), result.getGamesInShop().size());
        GameInShop resulGameInStore = result.getGamesInShop().stream().toList().get(0);
        assertEquals(gameInShop.getNameInStore(), resulGameInStore.getNameInStore());
        assertEquals(gameInShop.getDiscount(), resulGameInStore.getDiscount());
        assertEquals(gameInShop.getUrl(), resulGameInStore.getUrl());
        assertEquals(gameInShop.getGame().getName(), resulGameInStore.getGame().getName());
    }

    @Test
    void testFindGameByName_whenSuccess_shouldReturnGame() {
        final GameInShop gameInShop = getGameInStore();
        String url = "url";

        Document page = getDocumentForUncreatedGameByUrl(gameInShop, url);

        String name = "name";
        when(parser.getPage(GAMEZEY_SEARCH_URL + name)).thenReturn(page);
        Elements titleElements = mock(Elements.class);
        Element titleElement = mock(Element.class);
        Element hrefTitleElements = mock(Element.class);

        when(page.getElementsByClass("rm-module-title")).thenReturn(titleElements);
        when(titleElements.get(0)).thenReturn(titleElement);
        when(titleElement.child(0)).thenReturn(hrefTitleElements);
        when(hrefTitleElements.attr("href")).thenReturn(url);


        GameInShop result = storeService.findByName(name);


        assertEquals(gameInShop.getNameInStore(), result.getNameInStore());
        assertEquals(gameInShop.getDiscount(), result.getDiscount());
        assertEquals(gameInShop.getUrl(), result.getUrl());
    }

    @Test
    void testCheckGameInStoreForChange_whenSuccess_shouldReturnListOfGameInShop() {
        List<GameInShop> gameInShops = new ArrayList<>();
        GameInShop gameInShop = GameInShop.builder()
                .nameInStore("Test Game")
                .url("https://gamazey.com.ua/game-url")
                .price(new BigDecimal("500"))
                .discountPrice(new BigDecimal("450"))
                .isAvailable(true)
                .build();
        gameInShops.add(gameInShop);

        getDocumentForUncreatedGameByUrl(gameInShop, gameInShop.getUrl());


        List<GameInShop> changedGames = storeService.checkGameInStoreForChange(gameInShops);


        assertNotNull(changedGames);
        assertEquals(1L, changedGames.size());
        assertEquals(gameInShop, changedGames.get(0));
    }


    private Document getDocumentForUncreatedGameByUrl(GameInShop gameInShop, String url) {
        Document page = getDocumentForGameByUrl(gameInShop, url);
        when(parser.getPage(url)).thenReturn(page);

        Elements characteristicsElement = mock(Elements.class);
        Element genreElement = mock(Element.class);
        Elements imgElements = mock(Elements.class);
        Element imgElement = mock(Element.class);

        when(page.getElementsByClass(GAME_PAGE_CHARACTERISTICS)).thenReturn(characteristicsElement);
        when(characteristicsElement.get(3)).thenReturn(genreElement);
        when(page.getElementsByClass(GAME_IMG_CLASS)).thenReturn(imgElements);
        when(imgElements.get(1)).thenReturn(imgElement);

        when(genreElement.text()).thenReturn(GENRE_ELEMENT);
        when(imgElement.attr("src")).thenReturn("url");

        return page;
    }

    private Document getDocumentForGameByUrl(GameInShop gameInShop, String url) {
        String nameOnPage = String.format("Гра %s: для ПК (Ключ активації Steam)", gameInShop.getNameInStore());
        Document page = mock(Document.class);
        when(parser.getPage(url)).thenReturn(page);
        Elements nameFieldElement = mock(Elements.class);
        Elements priceFieldElement = mock(Elements.class);
        Element discountFieldElement = mock(Document.class);
        Elements isAvailableElement = mock(Elements.class);
        Elements discountPriceFieldElements = mock(Elements.class);
        Element discountPriceFieldElement = mock(Element.class);
        Element discountPriceFieldElementChild = mock(Element.class);

        when(page.getElementsByClass(GAME_PAGE_NAME_FIELD)).thenReturn(nameFieldElement);
        when(page.getElementsByClass(GAME_PAGE_OLD_PRICE_FIELD)).thenReturn(priceFieldElement);
        when(page.getElementById(GAME_PAGE_DISCOUNT_FIELD)).thenReturn(discountFieldElement);
        when(page.getElementsByClass(GAME_PAGE_IS_AVAILABLE)).thenReturn(isAvailableElement);
        when(page.getElementsByClass(GAME_PAGE_DISCOUNT_PRICE_FIELD)).thenReturn(discountPriceFieldElements);

        when(nameFieldElement.text()).thenReturn(nameOnPage);
        when(priceFieldElement.text()).thenReturn(gameInShop.getPrice().toString());
        when(discountFieldElement.text()).thenReturn(String.valueOf(gameInShop.getDiscount()));
        when(isAvailableElement.isEmpty()).thenReturn(gameInShop.isAvailable());
        when(discountPriceFieldElements.get(0)).thenReturn(discountPriceFieldElement);
        when(discountPriceFieldElement.child(1)).thenReturn(discountPriceFieldElementChild);
        when(discountPriceFieldElementChild.text()).thenReturn(gameInShop.getDiscountPrice().toString());

        return page;
    }

    private GameInShop getGameInStore() {
        return GameInShop.builder()
                .nameInStore("Game")
                .price(new BigDecimal(10))
                .discountPrice(new BigDecimal(9))
                .url("url")
                .discount(10)
                .isAvailable(true)
                .build();
    }
}