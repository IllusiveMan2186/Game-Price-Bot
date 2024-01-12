package com.gpb.web.service.impl.store;

import com.gpb.web.bean.game.ClientActivationType;
import com.gpb.web.bean.game.Game;
import com.gpb.web.bean.game.GameInShop;
import com.gpb.web.bean.game.Genre;
import com.gpb.web.bean.game.ProductType;
import com.gpb.web.configuration.ResourceConfiguration;
import com.gpb.web.parser.StorePageParser;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
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
    void getUncreatedGameByUrlSuccessfullyShouldReturnNewGame() {
        final GameInShop gameInShop = getGameInStore();
        final Game game = Game.builder()
                .name(gameInShop.getNameInStore())
                .type(ProductType.GAME)
                .gamesInShop(Collections.singleton(gameInShop))
                .build();
        gameInShop.setGame(game);
        String url = "url";

        getDocumentForUncreatedGameByUrl(gameInShop, url, game);

        Game result = storeService.findUncreatedGameByUrl(url);

        assertEquals("Game", result.getName());
        assertEquals(game.getGamesInShop().size(), result.getGamesInShop().size());
        assertEquals(game.getType(), result.getType());
        GameInShop resulGameInStore = game.getGamesInShop().stream().toList().get(0);
        assertEquals(gameInShop.getNameInStore(), resulGameInStore.getNameInStore());
        assertEquals(gameInShop.getDiscount(), resulGameInStore.getDiscount());
        assertEquals(gameInShop.getUrl(), resulGameInStore.getUrl());
        assertEquals(gameInShop.getGame().getName(), resulGameInStore.getGame().getName());
    }

    @Test
    void getGameInStoreByUrlSuccessfullyShouldReturnNewGameInStore() {
        final GameInShop gameInShop = getGameInStore();

        Document page = mock(Document.class);
        String url = "url";
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

        when(nameFieldElement.text()).thenReturn(gameInShop.getNameInStore());
        when(priceFieldElement.text()).thenReturn(gameInShop.getPrice().toString());
        when(discountFieldElement.text()).thenReturn(String.valueOf(gameInShop.getDiscount()));
        when(isAvailableElement.isEmpty()).thenReturn(gameInShop.isAvailable());
        when(discountPriceFieldElements.get(0)).thenReturn(discountPriceFieldElement);
        when(discountPriceFieldElement.child(1)).thenReturn(discountPriceFieldElementChild);
        when(discountPriceFieldElementChild.text()).thenReturn(gameInShop.getDiscountPrice().toString());
        gameInShop.setNameInStore("Game");

        GameInShop result = storeService.findByUrl(url);

        assertEquals(gameInShop, result);
    }

    @Test
    void findUncreatedGameByNameSuccessfullyShouldReturnGameList() {
        final GameInShop gameInShop = getGameInStore();
        final Game game = Game.builder()
                .name(gameInShop.getNameInStore())
                .gamesInShop(Collections.singleton(gameInShop))
                .build();
        gameInShop.setGame(game);
        String url = "url";

        Document page = getDocumentForUncreatedGameByUrl(gameInShop, url, game);

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
        GameInShop resulGameInStore = game.getGamesInShop().stream().toList().get(0);
        assertEquals(gameInShop.getNameInStore(), resulGameInStore.getNameInStore());
        assertEquals(gameInShop.getDiscount(), resulGameInStore.getDiscount());
        assertEquals(gameInShop.getUrl(), resulGameInStore.getUrl());
        assertEquals(gameInShop.getGame().getName(), resulGameInStore.getGame().getName());
    }

    private Document getDocumentForUncreatedGameByUrl(GameInShop gameInShop, String url, Game game) {
        Document page = mock(Document.class);
        when(parser.getPage(url)).thenReturn(page);
        Elements nameFieldElement = mock(Elements.class);
        Elements priceFieldElement = mock(Elements.class);
        Element discountFieldElement = mock(Document.class);
        Elements isAvailableElement = mock(Elements.class);
        Elements characteristicsElement = mock(Elements.class);
        Element genreElement = mock(Element.class);
        Elements imgElements = mock(Elements.class);
        Element imgElement = mock(Element.class);
        Elements discountPriceFieldElements = mock(Elements.class);
        Element discountPriceFieldElement = mock(Element.class);
        Element discountPriceFieldElementChild = mock(Element.class);

        when(page.getElementsByClass(GAME_PAGE_NAME_FIELD)).thenReturn(nameFieldElement);
        when(page.getElementsByClass(GAME_PAGE_OLD_PRICE_FIELD)).thenReturn(priceFieldElement);
        when(page.getElementById(GAME_PAGE_DISCOUNT_FIELD)).thenReturn(discountFieldElement);
        when(page.getElementsByClass(GAME_PAGE_IS_AVAILABLE)).thenReturn(isAvailableElement);
        when(page.getElementsByClass(GAME_PAGE_CHARACTERISTICS)).thenReturn(characteristicsElement);
        when(characteristicsElement.get(3)).thenReturn(genreElement);
        when(page.getElementsByClass(GAME_IMG_CLASS)).thenReturn(imgElements);
        when(imgElements.get(1)).thenReturn(imgElement);
        when(page.getElementsByClass(GAME_PAGE_DISCOUNT_PRICE_FIELD)).thenReturn(discountPriceFieldElements);

        when(nameFieldElement.text()).thenReturn(gameInShop.getNameInStore());
        when(priceFieldElement.text()).thenReturn(gameInShop.getPrice().toString());
        when(discountFieldElement.text()).thenReturn(String.valueOf(gameInShop.getDiscount()));
        when(isAvailableElement.isEmpty()).thenReturn(gameInShop.isAvailable());
        when(genreElement.text()).thenReturn(GENRE_ELEMENT);
        when(imgElement.attr("src")).thenReturn("url");
        when(discountPriceFieldElements.get(0)).thenReturn(discountPriceFieldElement);
        when(discountPriceFieldElement.child(1)).thenReturn(discountPriceFieldElementChild);
        when(discountPriceFieldElementChild.text()).thenReturn(gameInShop.getDiscountPrice().toString());

        return page;
    }

    private GameInShop getGameInStore() {
        return GameInShop.builder()
                .nameInStore("Гра Game для ПК (Ключ активації Steam)")
                .price(new BigDecimal(10))
                .discountPrice(new BigDecimal(9))
                .url("url")
                .discount(10)
                .isAvailable(true)
                .build();
    }
}