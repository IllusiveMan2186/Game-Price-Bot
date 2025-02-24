package com.gpb.backend.e2e;

import com.gpb.backend.e2e.util.EntToEndUtil;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Tag("e2e")
class UserGameListEndToEndTest {

    private static final String GAME_NAME = "Minecraft";
    private final String adminEmail = System.getProperty("e2e.email");
    private final String adminPassword = System.getProperty("e2e.password");

    @Test
    void testGameSubscription_whenSuccess_shouldSubscribeToGame() {
        WebDriver driver = getNameInfoPage();


        clickSubscribeButtonIfNeededCondition(driver, "Unsubscribe");


        WebElement subscribeButton = EntToEndUtil.waitToBeClickable(driver, "subscribe-button");
        assertEquals("Unsubscribe", subscribeButton.getText());

        driver.quit();
    }

    @Test
    void testGameUnsubscribe_whenSuccess_shouldUnsubscribeToGame() {
        WebDriver driver = getNameInfoPage();

        clickSubscribeButtonIfNeededCondition(driver, "Subscribe");


        WebElement subscribeButton = EntToEndUtil.waitToBeClickable(driver, "subscribe-button");
        assertEquals("Subscribe", subscribeButton.getText());

        driver.quit();
    }

    @RepeatedTest(EntToEndUtil.ATTEMPTS_AMOUNT)
    void testUserGameList_whenSuccess_shouldGameInListAfterSubscription() {
        WebDriver driver = getNameInfoPage();

        String gameName = driver.findElement(By.className("app-game__title")).getText();

        clickSubscribeButtonIfNeededCondition(driver, "Unsubscribe");

        EntToEndUtil.waitToBeClickable(driver, "profile-dropdown-button").click();
        driver.findElement(By.id("user-gameList-button")).click();


        String gameNameInList = driver.findElements(By.className("app-list__game")).get(0)
                .findElement(By.className("app-list__game-title")).getText();
        assertEquals(gameName, gameNameInList);

        driver.quit();
    }

    private void clickSubscribeButtonIfNeededCondition(WebDriver driver, String unwantedButtonState) {

        if (driver.findElement(By.id("subscribe-button")).getText().equals(unwantedButtonState)) {
            driver.findElement(By.id("subscribe-button")).click();
        }
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        wait.until(ExpectedConditions.elementToBeClickable(By.id("subscribe-button"))).click();

    }

    private WebDriver getNameInfoPage() {
        WebDriver driver = EntToEndUtil.getGpbWebDriver();

        EntToEndUtil.loginInToGpb(driver, adminEmail, adminPassword);

        if (driver.findElements(By.className("app-list__game")).isEmpty()) {
            EntToEndUtil.gameSearch(driver, GAME_NAME);
        }


        driver.findElement(By.className("app-list__game")).click();
        return driver;
    }
}
