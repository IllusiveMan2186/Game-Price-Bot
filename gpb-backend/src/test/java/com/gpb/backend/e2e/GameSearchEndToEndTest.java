package com.gpb.backend.e2e;

import com.gpb.backend.e2e.util.EntToEndUtil;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Tag;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import static com.gpb.backend.e2e.util.EntToEndUtil.gameSearch;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("e2e")
class GameSearchEndToEndTest {

    private static final String GAME_NAME = "Minecraft Java & Bedrock Edition";
    private final String adminEmail = System.getProperty("e2e.email");
    private final String adminPassword = System.getProperty("e2e.password");

    @RepeatedTest(EntToEndUtil.ATTEMPTS_AMOUNT)
    void testGameSearch_whenSuccess_shouldFindNeededGame() {
        WebDriver driver = EntToEndUtil.getGpbWebDriver();


        gameSearch(driver, GAME_NAME);


        WebElement firstGameInList = driver.findElement(By.className("App-game-content-list-game"));
        String gameName = firstGameInList.findElement(By.className("App-game-content-list-game-info-title")).getText();
        assertTrue(gameName.contains(GAME_NAME));

        driver.quit();
    }

    @RepeatedTest(EntToEndUtil.ATTEMPTS_AMOUNT)
    void testGameInfoContent_whenSuccess_shouldVerifyGameInfo() {
        WebDriver driver = EntToEndUtil.getGpbWebDriver();

        if (driver.findElements(By.className("App-game-content-list-game")).isEmpty()) {
            EntToEndUtil.gameSearch(driver, GAME_NAME);
        }


        driver.findElement(By.className("App-game-content-list-game")).click();


        assertTrue(driver.findElement(By.className("App-game-page")).isDisplayed());

        assertTrue(driver.findElement(By.className("App-game-content-list-game-info-img")).isDisplayed());

        assertTrue(driver.findElement(By.className("App-game-page-info")).isDisplayed());
        assertTrue(isTextNotEmpty(driver, "App-game-page-info-title"));

        assertTrue(driver.findElement(By.className("App-game-page-info-common")).isDisplayed());
        assertTrue(isTextNotEmpty(driver, "App-game-page-info-common-price"));
        assertTrue(isTextNotEmpty(driver, "App-game-content-list-game-info-type"));
        assertTrue(isTextNotEmpty(driver, "App-game-content-list-game-info-available"));
        assertTrue(isTextNotEmpty(driver, "App-game-content-list-game-info-price"));

        assertTrue(driver.findElement(By.className("App-game-page-info-subscribe")).isDisplayed());
        assertTrue(driver.findElement(By.className("App-game-page-info-storeList")).isDisplayed());

        driver.quit();
    }

    @RepeatedTest(EntToEndUtil.ATTEMPTS_AMOUNT)
    void testGameStoreLink_whenSuccess_shouldRedirectToStore() {
        WebDriver driver = EntToEndUtil.getGpbWebDriver();

        if (driver.findElements(By.className("App-game-content-list-game")).isEmpty()) {
            EntToEndUtil.gameSearch(driver, GAME_NAME);
        }


        driver.findElement(By.className("App-game-content-list-game")).click();


        assertTrue(driver.findElement(By.className("App-game-page-info-storeList")).isDisplayed());
        WebElement storeElement = driver.findElement(By.className("App-game-page-info-storeList-store"));
        assertTrue(storeElement.isDisplayed());

        assertTrue(isTextNotEmpty(storeElement, "App-game-content-list-game-info-available"));

        WebElement priceElement = driver.findElement(By.className("App-game-page-info-storeList-store-price-section"));
        assertTrue(storeElement.findElement(By.className("App-game-page-info-storeList-store-price-section")).isDisplayed());
        assertTrue(isTextNotEmpty(priceElement, "App-game-page-info-storeList-store-price"));
        assertTrue(isTextNotEmpty(priceElement, "App-game-page-info-storeList-store-discount"));
        assertTrue(isTextNotEmpty(priceElement, "App-game-page-info-storeList-store-discountPrice"));

        driver.quit();
    }

    private boolean isTextNotEmpty(WebElement element, String className) {
        return isTextNotEmpty(element.findElement(By.className(className)));
    }

    private boolean isTextNotEmpty(WebDriver driver, String className) {
        return isTextNotEmpty(driver.findElement(By.className(className)));
    }

    private boolean isTextNotEmpty(WebElement element) {
        return element.isDisplayed()
                && !element.getText().isEmpty();
    }
}
