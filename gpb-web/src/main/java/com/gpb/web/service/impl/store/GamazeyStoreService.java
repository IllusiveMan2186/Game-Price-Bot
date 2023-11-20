package com.gpb.web.service.impl.store;

import com.gpb.web.bean.game.Game;
import com.gpb.web.bean.game.GameInShop;
import com.gpb.web.bean.game.Genre;
import com.gpb.web.service.StoreService;
import com.gpb.web.parser.StorePageParser;
import lombok.extern.log4j.Log4j2;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

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
    private static final String IMG_FOLDER = "E:\\Work\\Pet\\GPB\\Game-Price-Bot\\gpb-front\\src\\img\\games\\";
    private static final String IMG_FILE_EXTENSION = ".jpg";
    private static final String GAMEZEY_SEARCH_URL = "https://gamazey.com.ua/search?search=";
    private static final String GAMEZEY_WISHLIST = "https://gamazey.com.ua/wishlist";

    private final StorePageParser parser;

    private final Map<String, Genre> genreMap;

    @Value("${GAMEZEY_LOGIN}")
    private String login;
    @Value("${GAMEZEY_PASSWORD}")
    private String password;

    public GamazeyStoreService(StorePageParser parser, Map<String, Genre> genreMap) {
        this.parser = parser;
        this.genreMap = genreMap;
    }

    @Override
    public Game findUncreatedGameByUrl(String url) {
        log.info(String.format("Searching uncreated game with url : '%s' in gamazey store", url));
        Document page = parser.getPage(url);
        GameInShop gameInShop = getGameInShop(page);
        List<Genre> genres = getGenres(page.getElementsByClass(GAME_PAGE_CHARACTERISTICS).get(3));
        saveImage(page, gameInShop.getNameInStore());
        gameInShop.setUrl(url);

        Game game = Game.builder()
                .name(gameInShop.getNameInStore())
                .gamesInShop(Collections.singletonList(gameInShop))
                .genres(genres)
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

        List<Game> games = new ArrayList<>();
        for (Element element : elements) {
            String url = element.child(0).attr("href");
            games.add(findUncreatedGameByUrl(url));
        }

        return games;
    }

    @Override
    public GameInShop findByName(String name) {
        return null;
    }

    @Override
    public void subscribeToGame(GameInShop gameInShop) {
        String url = gameInShop.getUrl();
        log.info(String.format("Add to wish list of gamazey store game by url :'%s'", url));
        WebDriver driver = login(url);

        driver.findElement(By.cssSelector(".rm-product-top-button-wishlist")).click();
        driver.quit();
    }

    @Override
    public void unsubscribeFromGame(GameInShop gameInShop) {
        String name = gameInShop.getNameInStore();
        log.info(String.format("Remove from wish list of gamazey store game by name :'%s'", name));
        WebDriver driver = login(GAMEZEY_WISHLIST);

        driver.navigate().refresh();
        WebElement element = driver.findElement(By.xpath(String.format("//*[text()='%s']", name)));
        WebElement parent = element.findElement(By.xpath("./..")).findElement(By.xpath("./.."));
        parent.findElement(By.className("rm-btn-icon")).click();
        driver.quit();
    }

    @Override
    public List<GameInShop> checkGameInStoreForChange(List<GameInShop> gameInShops) {
        log.info(String.format("Check %s games from wishlist in stores for changes in gamazey store", gameInShops.size()));
        WebDriver driver = login(GAMEZEY_WISHLIST);
        List<GameInShop> changedGames = new ArrayList<>();
        driver.navigate().refresh();
        List<WebElement> elements = driver.findElements(By.className("rm-account-wishlist-item-info"));

        for (WebElement element : elements) {
            String name = element.findElement(By.className("rm-content-title")).getText();
            Optional<GameInShop> game = gameInShops.stream().filter(s -> s.getNameInStore().equals(name)).findFirst();

            if (game.isPresent() && isGameInfoChanged(game.get(), element)) {
                changedGames.add(setChangedFields(game.get(), element));
            }
        }
        driver.quit();
        return changedGames;
    }

    private boolean isGameInfoChanged(GameInShop gameInShop, WebElement element) {
        log.info(String.format("Check for game '%s' info changing in gamazey store", gameInShop.getNameInStore()));

        String discountPrice = element.findElement(By.className("rm-module-price-new")).getText();
        String price = element.findElement(By.className("rm-module-price-old")).getText();
        boolean isAvailable = element.findElements(By.className("rm-account-text")).get(0)
                .findElement(By.tagName("span")).getText().equals("В наличии");

        return gameInShop.isAvailable() == isAvailable
                && gameInShop.getDiscountPrice().toString().equals(discountPrice)
                && gameInShop.getPrice().toString().equals(price);
    }

    private GameInShop setChangedFields(GameInShop gameInShop, WebElement element) {
        log.info(String.format("Set changes for game '%s' in gamazey store", gameInShop.getNameInStore()));

        String discountPrice = element.findElement(By.className("rm-module-price-new")).getText();
        String price = element.findElement(By.className("rm-module-price-old")).getText();
        boolean isAvailable = element.findElements(By.className("rm-account-text")).get(1)
                .findElement(By.tagName("span")).getText().equals("В наличии");
        int discountPriceInt = Integer.parseInt(getIntFromString(discountPrice));
        int priceInt = Integer.parseInt(getIntFromString(price));
        int discount = 100 - (int) ((discountPriceInt * 100.0f) / priceInt);
        ;

        gameInShop.setDiscountPrice(new BigDecimal(discountPriceInt));
        gameInShop.setPrice(new BigDecimal(priceInt));
        gameInShop.setAvailable(isAvailable);
        gameInShop.setDiscount(discount);

        return gameInShop;
    }

    private WebDriver login(String url) {
        log.info("Login in gamazey store");

        System.setProperty("webdriver.chrome.driver", "E:\\Programs\\chromedriver-win64\\chromedriver.exe");
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--remote-allow-origins=*");
        WebDriver driver = new ChromeDriver(options);
        driver.get(url);

        driver.manage().timeouts().implicitlyWait(4, TimeUnit.SECONDS);

        driver.findElements(By.cssSelector(".d-flex.align-items-center")).get(3).click();
        driver.findElement(By.id("emailLoginInput")).sendKeys(login);
        driver.findElement(By.id("passwordLoginInput")).sendKeys(password);
        WebElement element = driver.findElement(By.id("popup-login-button"));
        JavascriptExecutor executor = (JavascriptExecutor) driver;
        executor.executeScript("arguments[0].click();", element);
        driver.navigate().refresh();
        return driver;
    }

    private GameInShop getGameInShop(Document page) {
        String nameField = page.getElementsByClass(GAME_PAGE_NAME_FIELD).text();
        String priceField = page.getElementsByClass(GAME_PAGE_OLD_PRICE_FIELD).text();
        String discountPriceField = page.getElementsByClass(GAME_PAGE_DISCOUNT_PRICE_FIELD).get(0)
                .child(1).text();
        String discountField = page.getElementById(GAME_PAGE_DISCOUNT_FIELD).text();
        boolean isAvailable = page.getElementsByClass(GAME_PAGE_IS_AVAILABLE).isEmpty();

        return GameInShop.builder()
                .nameInStore(nameField)
                .price(new BigDecimal(getIntFromString(priceField)))
                .discountPrice(new BigDecimal(getIntFromString(discountPriceField)))
                .discount(Integer.parseInt(getIntFromString(discountField)))
                .isAvailable(isAvailable)
                .build();
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
                genres.add(entry.getValue());
            }
        }
        return genres;
    }

    private void saveImage(Document document, String gameName) {
        Element element = document.getElementsByClass(GAME_IMG_CLASS).get(1);
        String imgUrl = element.attr("src");
        String filePath = IMG_FOLDER + gameName + IMG_FILE_EXTENSION;
        try {
            URL url = new URL(imgUrl);

            try (InputStream is = url.openStream();
                 OutputStream os = new FileOutputStream(filePath)) {

                byte[] b = new byte[2048];
                int length;

                while ((length = is.read(b)) != -1) {
                    os.write(b, 0, length);
                }
            }
            cropImage(filePath);
        } catch (IOException e) {
            log.error(String.format("Not loaded image for game '%s' by url '%s':'%s'", gameName, imgUrl, e.getMessage()));
        }
    }

    private void cropImage(String filePath) throws IOException {
        BufferedImage image = ImageIO.read(new File(filePath));
        BufferedImage crop = image.getSubimage(68, 0, 560, 700);
        ImageIO.write(crop, "JPG", new File(filePath));
    }
}
