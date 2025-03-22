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
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@AllArgsConstructor
public class CommonStoreServiceImpl implements StoreService {

    protected final StorePageParser pageFetcher;
    protected final StoreParser storeParser;


    @Override
    public Optional<Game> findUncreatedGameByUrl(String url) {
        log.info("Searching uncreated game with url: '{}'", url);

        return pageFetcher.getPage(url)
                .flatMap(page -> {
                    GameInShop gameInShop = storeParser.parseGameInShopFromPage(page);
                    if (gameInShop == null) {
                        return Optional.empty();
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
                    return Optional.of(game);
                });
    }

    @Override
    public Optional<GameInShop> findByUrl(String url) {
        log.info("Searching game with url: '{}'", url);
        return pageFetcher.getPage(url).flatMap(page -> {
            GameInShop game = storeParser.parseGameInShopFromPage(page);

            if (game == null) return Optional.empty();

            game.setUrl(url);
            return Optional.of(game);
        });
    }

    @Override
    public List<Game> findUncreatedGameByName(String name) {
        log.info("Searching game with name: '{}'", name);

        List<String> gameUrls = storeParser.parseSearchResults(name, pageFetcher);

        long startTime = System.currentTimeMillis();

        return gameUrls.stream()
                .takeWhile(url -> System.currentTimeMillis() - startTime <= Constants.SEARCH_REQUEST_WAITING_TIME)
                .map(this::findUncreatedGameByUrl)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();
    }

    @Override
    public Optional<GameInShop> findByName(String name) {
        log.info("Searching registered game with name: '{}'", name);

        return storeParser.parseSearchResults(name, pageFetcher).stream()
                .findFirst()
                .flatMap(url -> {
                    log.info("Found game URL: {}", url);
                    return pageFetcher.getPage(url)
                            .flatMap(page -> {
                                GameInShop game = storeParser.parseGameInShopFromPage(page);
                                game.setUrl(url);
                                return Optional.of(game);
                            });
                });
    }

    @Override
    public List<GameInShop> checkGameInStoreForChange(List<GameInShop> gameInShops) {
        log.info("Checking {} games from wishlist for changes in store", gameInShops.size());

        return gameInShops.stream()
                .map(this::processGameChange)
                .filter(Objects::nonNull)
                .toList();
    }

    private GameInShop processGameChange(GameInShop game) {
        Optional<Document> pageOpt = pageFetcher.getPage(game.getUrl());

        if (pageOpt.isEmpty()) {
            log.warn("Failed to fetch page for game: {}", game.getUrl());
            return null;
        }

        GameInShop gameOnPage = storeParser.parseGameInShopFromPage(pageOpt.get());

        return hasGameInfoChanged(game, gameOnPage)
                ? setChangedFields(game, gameOnPage)
                : null;
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

