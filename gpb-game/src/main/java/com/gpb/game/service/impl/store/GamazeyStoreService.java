package com.gpb.game.service.impl.store;

import com.gpb.game.bean.game.ClientActivationType;
import com.gpb.game.bean.game.Game;
import com.gpb.game.bean.game.GameInShop;
import com.gpb.game.bean.game.Genre;
import com.gpb.game.bean.game.ProductType;
import com.gpb.game.configuration.ResourceConfiguration;
import com.gpb.game.parser.StorePageParser;
import com.gpb.game.service.StoreService;
import lombok.extern.log4j.Log4j2;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.gpb.game.util.Constants.JPG_IMG_FILE_EXTENSION;
import static com.gpb.game.util.Constants.SEARCH_REQUEST_WAITING_TIME;

@Service(value = "gamazey.com.ua")
@Log4j2
public class GamazeyStoreService implements StoreService {

    private static final String GAME_PAGE_NAME_FIELD = "rm-product-title order-1 order-md-0";
    private static final String GAME_PAGE_OLD_PRICE_FIELD = "rm-product-center-price-old";
    private static final String GAME_PAGE_DISCOUNT_PRICE_FIELD = "rm-product-center-price";
    private static final String GAME_PAGE_DISCOUNT_FIELD = "main-product-you-save";
    private static final String GAME_PAGE_IS_AVAILABLE = "rm-module-stock rm-out-of-stock";
    private static final String GAME_PAGE_CHARACTERISTICS = "rm-product-attr-list-item d-flex d-sm-block";
    private static final String GAME_IMG_CLASS = "img-fluid";
    private static final String GAMEZEY_SEARCH_URL = "https://gamazey.com.ua/search?search=";
    private static final String GAME_NAME_PRODUCT_TYPE_PART = "(Гра|Ігрова валюта|Доповнення) ";
    private static final String GAME_NAME_SPECIFICATION_PART = " для .+ \\(Ключ активації .+\\)";
    private static final String GAME_GENRES_FIELD = "Жанр";

    private static final int GAME_IMAGE_CROP_WIDTH_START = 20;
    private static final int GAME_IMAGE_CROP_WIDTH_LONG = 320;
    private static final int GAME_IMAGE_HEIGHT = 400;

    private final StorePageParser parser;

    private final Map<String, Genre> genreMap;

    private final Map<String, ProductType> productTypeMap;

    private final Map<String, ClientActivationType> clientActivationTypeMap;

    private final ResourceConfiguration resourceConfiguration;

    @Value("${GAMEZEY_LOGIN}")
    private String login;
    @Value("${GAMEZEY_PASSWORD}")
    private String password;

    public GamazeyStoreService(StorePageParser parser, Map<String, Genre> genreMap, Map<String, ProductType> productTypeMap,
                               Map<String, ClientActivationType> clientActivationTypeMap, ResourceConfiguration resourceConfiguration) {
        this.parser = parser;
        this.genreMap = genreMap;
        this.productTypeMap = productTypeMap;
        this.resourceConfiguration = resourceConfiguration;
        this.clientActivationTypeMap = clientActivationTypeMap;
    }

    @Override
    public Game findUncreatedGameByUrl(String url) {
        log.info(String.format("Searching uncreated game with url : '%s' in gamazey store", url));
        Document page = parser.getPage(url);
        GameInShop gameInShop = getGameInShop(page);
        ProductType type = getProductType(page);
        List<Genre> genres = getGenresFromFiled(page);
        saveImage(page, gameInShop.getNameInStore());
        gameInShop.setUrl(url);

        Game game = Game.builder()
                .name(gameInShop.getNameInStore())
                .gamesInShop(Collections.singleton(gameInShop))
                .genres(genres)
                .type(type)
                .build();
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
    public List<Game> findUncreatedGameByName(String name) {
        log.info(String.format("Searching game with name : '%s' in gamazey store", name));

        Document page = parser.getPage(GAMEZEY_SEARCH_URL + name);
        Elements elements = page.getElementsByClass("rm-module-title");

        long startTime = System.currentTimeMillis();

        List<Game> games = new ArrayList<>();
        for (Element element : elements) {
            if (System.currentTimeMillis() - startTime > SEARCH_REQUEST_WAITING_TIME) {
                log.warn("Search process exceeded the maximum allowed duration. Stopping search.");
                break;
            }
            String url = element.child(0).attr("href");
            games.add(findUncreatedGameByUrl(url));
        }

        return games;
    }

    @Override
    public GameInShop findByName(String name) {
        Document page = parser.getPage(GAMEZEY_SEARCH_URL + name);
        Elements elements = page.getElementsByClass("rm-module-title");
        String url = elements.get(0).child(0).attr("href");
        return findByUrl(url);
    }

    @Override
    public List<GameInShop> checkGameInStoreForChange(List<GameInShop> gameInShops) {
        log.info("Check {} games from wishlist in stores for changes in gamazey store", gameInShops.size());

        List<GameInShop> changedGames = new ArrayList<>();

        for (GameInShop game : gameInShops) {
            if (isGameInfoChanged(game)) {
                changedGames.add(setChangedFields(game));
            }
        }
        return changedGames;
    }

    private boolean isGameInfoChanged(GameInShop gameInShop) {
        log.info(String.format("Check for game '%s' info changing in gamazey store", gameInShop.getNameInStore()));

        Document page = parser.getPage(gameInShop.getUrl());
        GameInShop gameOnPage = getGameInShop(page);

        return gameInShop.isAvailable() == gameOnPage.isAvailable()
                && gameInShop.getDiscountPrice().equals(gameOnPage.getDiscountPrice())
                && gameInShop.getPrice().equals(gameOnPage.getPrice());
    }

    private GameInShop setChangedFields(GameInShop gameInShop) {
        log.info(String.format("Set changes for game '%s' in gamazey store", gameInShop.getNameInStore()));

        Document page = parser.getPage(gameInShop.getUrl());
        GameInShop gameOnPage = getGameInShop(page);

        gameInShop.setDiscountPrice(gameOnPage.getDiscountPrice());
        gameInShop.setPrice(gameOnPage.getPrice());
        gameInShop.setAvailable(gameOnPage.isAvailable());
        gameInShop.setDiscount(gameOnPage.getDiscount());

        return gameInShop;
    }

    private GameInShop getGameInShop(Document page) {
        String nameField = page.getElementsByClass(GAME_PAGE_NAME_FIELD).text();
        String priceField = page.getElementsByClass(GAME_PAGE_OLD_PRICE_FIELD).text();
        String discountPriceField = page.getElementsByClass(GAME_PAGE_DISCOUNT_PRICE_FIELD).get(0)
                .child(1).text();
        String discountField = page.getElementById(GAME_PAGE_DISCOUNT_FIELD).text();
        boolean isAvailable = page.getElementsByClass(GAME_PAGE_IS_AVAILABLE).isEmpty();

        return GameInShop.builder()
                .nameInStore(removeUnnecessaryInfoFromGameName(nameField))
                .price(new BigDecimal(getIntFromString(priceField)))
                .discountPrice(new BigDecimal(getIntFromString(discountPriceField)))
                .discount(Integer.parseInt(getIntFromString(discountField)))
                .isAvailable(isAvailable)
                .clientType(getClientActivationTypeFromGameName(nameField))
                .build();
    }

    /**
     * Get genres by searching needed field on page if characteristics presented
     *
     * @param page page
     * @return list of genres if presented
     */
    private List<Genre> getGenresFromFiled(Document page) {
        Elements characteristic = page.getElementsByClass(GAME_PAGE_CHARACTERISTICS);

        if (!characteristic.isEmpty()) {
            Optional<Element> genreField = characteristic.stream()
                    .filter(ch -> ch.text().contains(GAME_GENRES_FIELD))
                    .findAny();
            if (genreField.isPresent()) {
                return getGenres(genreField.get());
            }

        }
        return new ArrayList<>();
    }

    private String getIntFromString(String field) {
        Pattern intsOnly = Pattern.compile("\\d+");
        Matcher makeMatch = intsOnly.matcher(field);

        StringBuilder result = new StringBuilder();
        while (makeMatch.find()) {
            result.append(makeMatch.group());
        }
        return result.toString();
    }

    private List<Genre> getGenres(Element genreElement) {
        List<Genre> genres = new ArrayList<>();
        for (Map.Entry<String, Genre> entry : genreMap.entrySet()) {
            if (genreElement.text().contains(entry.getKey())) {
                System.out.println(entry.getKey() + " " + entry.getValue());
                genres.add(entry.getValue());
            }
        }
        return genres;
    }

    private void saveImage(Document document, String gameName) {
        Element element = document.getElementsByClass(GAME_IMG_CLASS).get(1);
        String imgUrl = element.attr("src");
        String filePath = resourceConfiguration.getImageFolder() + "/" + sanitizeFilename(gameName) + JPG_IMG_FILE_EXTENSION;
        try {
            cropImage(imgUrl, filePath);
        } catch (IOException e) {
            log.error(String.format("Not loaded image for game '%s' by url '%s':'%s'", gameName, imgUrl, e.getMessage()));
        }
    }

    public static void downloadImage(String imageUrl, String filePath) throws IOException {
        URL url = new URL(imageUrl);
        Path path = Paths.get(filePath);

        try (InputStream in = url.openStream()) {
            Files.copy(in, path);
        }
    }

    private void cropImage(String imageUrl, String filePath) throws IOException {
        BufferedImage image = ImageIO.read(new URL(imageUrl));
        BufferedImage crop = image.getSubimage(GAME_IMAGE_CROP_WIDTH_START, 0, GAME_IMAGE_CROP_WIDTH_LONG,
                GAME_IMAGE_HEIGHT);
        File outputFile = new File(filePath);
        ImageIO.write(crop, "JPG", outputFile);
    }

    private String sanitizeFilename(String filename) {
        return filename.replaceAll("[:/]", "_");
    }


    private String removeUnnecessaryInfoFromGameName(String originalName) {
        return originalName
                .replaceAll(GAME_NAME_PRODUCT_TYPE_PART, "")
                .replaceAll(GAME_NAME_SPECIFICATION_PART, "");
    }

    private ProductType getProductType(Document page) {
        String originalName = page.getElementsByClass(GAME_PAGE_NAME_FIELD).text();
        for (String productType : productTypeMap.keySet()) {
            if (originalName.contains(productType)) {
                return productTypeMap.get(productType);
            }
        }
        return ProductType.GAME;
    }

    private ClientActivationType getClientActivationTypeFromGameName(String originalName) {
        for (String clientActivationType : clientActivationTypeMap.keySet()) {
            if (originalName.contains(clientActivationType)) {
                return clientActivationTypeMap.get(clientActivationType);
            }
        }
        return null;
    }
}
