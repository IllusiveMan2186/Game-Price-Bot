package com.gpb.web.service.impl.store;

import com.gpb.web.bean.game.Game;
import com.gpb.web.bean.game.GameInShop;
import com.gpb.web.service.StoreService;
import com.gpb.web.parser.StorePageParser;
import lombok.extern.log4j.Log4j2;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service(value = "gamazey.com.ua")
@Log4j2
public class GamazeyStoreService implements StoreService {

    private static final String GAME_PAGE_NAME_FIELD = "rm-product-title order-1 order-md-0";
    private static final String GAME_PAGE_OLD_PRICE_FIELD = "rm-product-center-price-old";
    private static final String GAME_PAGE_DISCOUNT_FIELD = "main-product-you-save";
    private static final String GAME_PAGE_IS_AVAILABLE = "rm-module-stock rm-out-of-stock";

    private final StorePageParser parser;

    public GamazeyStoreService(StorePageParser parser) {
        this.parser = parser;
    }

    @Override
    public Game findUncreatedGameByUrl(String url) {
        log.info(String.format("Searching uncreated game with url : '%s' in gamazey store", url));
        Document page = parser.getPage(url);
        GameInShop gameInShop = getGameInShop(page);
        gameInShop.setUrl(url);

        Game game = Game.builder()
                .name(gameInShop.getNameInStore())
                .gamesInShop(Collections.singletonList(gameInShop)).build();
        gameInShop.setGame(game);
        return game;
    }

    @Override
    public GameInShop findByUrl(String url) {
        log.info(String.format("Searching game with url : '%s' in gamazey store", url));
        Document page = parser.getPage(url);
        GameInShop game = getGameInShop(page);
        game.setUrl(url);
        return game;
    }

    @Override
    public Game findUncreatedGameByName(String name) {
        return null;
    }

    @Override
    public GameInShop findByName(String name) {
        return null;
    }

    private GameInShop getGameInShop(Document page) {
        String nameField = page.getElementsByClass(GAME_PAGE_NAME_FIELD).text();
        String priceField = page.getElementsByClass(GAME_PAGE_OLD_PRICE_FIELD).text();
        String discountField = page.getElementById(GAME_PAGE_DISCOUNT_FIELD).text();
        boolean isAvailable = page.getElementsByClass(GAME_PAGE_IS_AVAILABLE).isEmpty();

        return GameInShop.builder()
                .nameInStore(nameField)
                .price(new BigDecimal(getIntFromString(priceField)))
                .discount(Integer.parseInt(getIntFromString(discountField)))
                .isAvailable(isAvailable)
                .build();
    }

    private String getIntFromString(String field) {
        Pattern intsOnly = Pattern.compile("\\d+");
        Matcher makeMatch = intsOnly.matcher(field);
        makeMatch.find();
        return makeMatch.group();
    }
}
