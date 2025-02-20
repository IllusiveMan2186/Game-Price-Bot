package com.gpb.backend.e2e;

import com.gpb.backend.e2e.util.EntToEndUtil;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import static com.gpb.backend.e2e.util.EntToEndUtil.gameSearch;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("e2e")
class GameSearchEndToEndTest {

    private static final String GAME_NAME = "Minecraft";
    private final String adminEmail = System.getProperty("e2e.email");
    private final String adminPassword = System.getProperty("e2e.password");

    @Test
    void testGameSearch_whenSuccess_shouldFindNeededGame() {
        WebDriver driver = EntToEndUtil.getGpbWebDriver();


        gameSearch(driver, GAME_NAME);


        WebElement firstGameInList = driver.findElement(By.className("app-list__game"));
        String gameName = firstGameInList.findElement(By.className("app-list__game-title")).getText();
        assertTrue(gameName.contains(GAME_NAME));

        driver.quit();
    }

    @Test
    void testGameInfoContent_whenSuccess_shouldVerifyGameInfo() {
        WebDriver driver = EntToEndUtil.getGpbWebDriver();

        if (driver.findElements(By.className("app-list__game")).isEmpty()) {
            EntToEndUtil.gameSearch(driver, GAME_NAME);
        }


        driver.findElement(By.className("app-list__game")).click();


        assertTrue(driver.findElement(By.className("app-game-page")).isDisplayed());

        assertTrue(driver.findElement(By.className("app-game-page-image")).isDisplayed());

        assertTrue(driver.findElement(By.className("app-game__info")).isDisplayed());
        assertTrue(isTextNotEmpty(driver, "app-game__info"));

        assertTrue(driver.findElement(By.className("app-game__details")).isDisplayed());
        assertTrue(isTextNotEmpty(driver, "app-game__details-price"));
        assertTrue(isTextNotEmpty(driver, "app-game-type"));
        assertTrue(isTextNotEmpty(driver, "app-game-available"));
        assertTrue(isTextNotEmpty(driver, "app-game-price"));

        assertTrue(driver.findElement(By.className("app-game__subscribe")).isDisplayed());
        assertTrue(driver.findElement(By.className("app-game__store-list")).isDisplayed());

        driver.quit();
    }

    @Test
    void testGameStoreLink_whenClickOnGameInStore_shouldRedirectToStore() {
        WebDriver driver = EntToEndUtil.getGpbWebDriver();

        if (driver.findElements(By.className("app-list__game")).isEmpty()) {
            EntToEndUtil.gameSearch(driver, GAME_NAME);
        }

        driver.findElement(By.className("app-list__game")).click();

        assertTrue(driver.findElement(By.className("app-game__store-list")).isDisplayed());
        WebElement storeElement = driver.findElement(By.className("app-store__item"));
        storeElement.click();



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
