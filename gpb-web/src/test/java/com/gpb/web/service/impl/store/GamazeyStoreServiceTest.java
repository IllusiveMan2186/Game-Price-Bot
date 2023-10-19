package com.gpb.web.service.impl.store;

import com.gpb.web.bean.game.Game;
import com.gpb.web.bean.game.GameInShop;
import com.gpb.web.parser.StorePageParser;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GamazeyStoreServiceTest {

    private static final String GAME_PAGE_NAME_FIELD = "rm-product-title order-1 order-md-0";
    private static final String GAME_PAGE_OLD_PRICE_FIELD = "rm-product-center-price-old";
    private static final String GAME_PAGE_DISCOUNT_FIELD = "main-product-you-save";
    private static final String GAME_PAGE_IS_AVAILABLE = "rm-module-stock rm-out-of-stock";

    StorePageParser parser = mock(StorePageParser.class);

    GamazeyStoreService storeService = new GamazeyStoreService(parser);

    @Test
    void getUncreatedGameByUrlSuccessfullyShouldReturnNewGame() {
        final GameInShop gameInShop = getGameInStore();
        final Game game = Game.builder()
                .name(gameInShop.getNameInStore())
                .gamesInShop(Collections.singletonList(gameInShop))
                .build();
        gameInShop.setGame(game);

        Document page = mock(Document.class);
        String url = "url";
        when(parser.getPage(url)).thenReturn(page);
        Elements nameFieldElement = mock(Elements.class);
        Elements priceFieldElement = mock(Elements.class);
        Element discountFieldElement = mock(Document.class);
        Elements isAvailableElement = mock(Elements.class);

        when(page.getElementsByClass(GAME_PAGE_NAME_FIELD)).thenReturn(nameFieldElement);
        when(page.getElementsByClass(GAME_PAGE_OLD_PRICE_FIELD)).thenReturn(priceFieldElement);
        when(page.getElementById(GAME_PAGE_DISCOUNT_FIELD)).thenReturn(discountFieldElement);
        when(page.getElementsByClass(GAME_PAGE_IS_AVAILABLE)).thenReturn(isAvailableElement);

        when(nameFieldElement.text()).thenReturn(gameInShop.getNameInStore());
        when(priceFieldElement.text()).thenReturn(gameInShop.getPrice().toString());
        when(discountFieldElement.text()).thenReturn(String.valueOf(gameInShop.getDiscount()));
        when(isAvailableElement.isEmpty()).thenReturn(gameInShop.isAvailable());

        Game result = storeService.findUncreatedGameByUrl(url);

        assertEquals(game.getName(), result.getName());
        assertEquals(game.getGamesInShop().size(), result.getGamesInShop().size());
        GameInShop resulGameInStore = game.getGamesInShop().get(0);
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

        when(page.getElementsByClass(GAME_PAGE_NAME_FIELD)).thenReturn(nameFieldElement);
        when(page.getElementsByClass(GAME_PAGE_OLD_PRICE_FIELD)).thenReturn(priceFieldElement);
        when(page.getElementById(GAME_PAGE_DISCOUNT_FIELD)).thenReturn(discountFieldElement);
        when(page.getElementsByClass(GAME_PAGE_IS_AVAILABLE)).thenReturn(isAvailableElement);

        when(nameFieldElement.text()).thenReturn(gameInShop.getNameInStore());
        when(priceFieldElement.text()).thenReturn(gameInShop.getPrice().toString());
        when(discountFieldElement.text()).thenReturn(String.valueOf(gameInShop.getDiscount()));
        when(isAvailableElement.isEmpty()).thenReturn(gameInShop.isAvailable());

        GameInShop result = storeService.findByUrl(url);

        assertEquals(gameInShop, result);
    }

    private GameInShop getGameInStore(){
        return  GameInShop.builder()
                .nameInStore("name")
                .price(new BigDecimal(1))
                .url("url")
                .discount(10)
                .isAvailable(true)
                .build();
    }
}