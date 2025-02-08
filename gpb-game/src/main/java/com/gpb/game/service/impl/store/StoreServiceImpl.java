package com.gpb.game.service.impl.store;

import com.gpb.common.exception.NotFoundException;
import com.gpb.game.entity.game.Game;
import com.gpb.game.entity.game.GameInShop;
import com.gpb.game.parser.StorePageParser;
import com.gpb.game.parser.StoreParser;
import com.gpb.game.service.StoreService;
import com.gpb.game.util.Constants;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Slf4j
@AllArgsConstructor
public class StoreServiceImpl implements StoreService {

    protected final StorePageParser pageFetcher;
    protected final StoreParser storeParser;


    @Override
    public Game findUncreatedGameByUrl(String url) {
        log.info("Searching uncreated game with url: '{}'", url);

        Document page = pageFetcher.getPage(url);
        GameInShop gameInShop = storeParser.parseGameInShopFromPage(page);

        if (gameInShop == null) {
            throw new NotFoundException("app.game.error.url.not.found");
        }

        storeParser.saveImage(page);
        gameInShop.setUrl(url);

        Game game = Game.builder()
                .name(gameInShop.getNameInStore())
                .gamesInShop(new HashSet<>(Set.of(gameInShop)))
                .genres(storeParser.getGenres(page))
                .type(storeParser.getProductType(page))
                .build();
        gameInShop.setGame(game);
        return game;
    }

    @Override
    public GameInShop findByUrl(String url) {
        log.info("Searching game with url: '{}'", url);
        Document page = pageFetcher.getPage(url);
        GameInShop game = storeParser.parseGameInShopFromPage(page);

        if (game == null) {
            throw new NotFoundException("app.game.error.url.not.found");
        }

        game.setUrl(url);
        return game;
    }

    @Override
    public List<Game> findUncreatedGameByName(String name) {
        log.info("Searching game with name: '{}'", name);

        List<String> gameUrls = storeParser.parseSearchResults(name, pageFetcher);

        long startTime = System.currentTimeMillis();

        return gameUrls.stream()
                .takeWhile(url -> System.currentTimeMillis() - startTime <= Constants.SEARCH_REQUEST_WAITING_TIME)
                .map(this::findUncreatedGameByUrl)
                .toList();
    }

    @Override
    public GameInShop findByName(String name) {
        log.info("Searching registered game with name: '{}'", name);

        String url = storeParser
                .parseSearchResults(name, pageFetcher)
                .stream()
                .findFirst()
                .orElse(null);

        if (url == null) {
            return null;
        }

        Document page = pageFetcher.getPage(url);
        GameInShop game = storeParser.parseGameInShopFromPage(page);
        game.setUrl(url);

        return game;
    }


    @Override
    public List<GameInShop> checkGameInStoreForChange(List<GameInShop> gameInShops) {
        log.info("Checking {} games from wishlist for changes in store", gameInShops.size());

        return gameInShops.stream()
                .map(game -> {
                    GameInShop gameOnPage = storeParser.parseGameInShopFromPage(pageFetcher.getPage(game.getUrl()));
                    return hasGameInfoChanged(game, gameOnPage) ? setChangedFields(game, gameOnPage) : null;
                })
                .filter(Objects::nonNull)
                .toList();
    }

    private boolean hasGameInfoChanged(GameInShop gameInShop, GameInShop gameOnPage) {
        log.info("Checking changes for game '{}' in store", gameInShop.getNameInStore());

        return !(gameInShop.isAvailable() == gameOnPage.isAvailable()
                && gameInShop.getDiscountPrice().compareTo(gameOnPage.getDiscountPrice()) == 0
                && gameInShop.getPrice().compareTo(gameOnPage.getPrice()) == 0);
    }

    private GameInShop setChangedFields(GameInShop gameInShop, GameInShop gameOnPage) {
        log.info("Applying changes to game '{}' in store", gameInShop.getNameInStore());

        return GameInShop.builder()
                .nameInStore(gameInShop.getNameInStore())
                .price(gameOnPage.getPrice())
                .discountPrice(gameOnPage.getDiscountPrice())
                .discount(gameOnPage.getDiscount())
                .isAvailable(gameOnPage.isAvailable())
                .clientType(gameInShop.getClientType())
                .url(gameInShop.getUrl())
                .build();
    }
}

