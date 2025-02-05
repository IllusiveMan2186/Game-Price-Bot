package com.gpb.backend.e2e;

import com.gpb.backend.e2e.util.EntToEndUtil;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Tag;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Tag("e2e")
class UserGameListEndToEndTest {

    private static final String GAME_NAME = "Minecraft Java & Bedrock Edition";
    private final String adminEmail = System.getProperty("e2e.email");
    private final String adminPassword = System.getProperty("e2e.password");

    @RepeatedTest(EntToEndUtil.ATTEMPTS_AMOUNT)
    void testGameSubscription_whenSuccess_shouldSubscribeToGame() {
        WebDriver driver = getNameInfoPAge();


        clickSubscribeButtonIfNeededCondition(driver, "Unsubscribe");


        WebElement subscribeButton = waitToBeClickable(driver, "subscribe-button");
        assertEquals("Unsubscribe", subscribeButton.getText());

        driver.quit();
    }

    @RepeatedTest(EntToEndUtil.ATTEMPTS_AMOUNT)
    void testGameUnsubscribe_whenSuccess_shouldUnsubscribeToGame() {
        WebDriver driver = getNameInfoPAge();

        clickSubscribeButtonIfNeededCondition(driver, "Subscribe");


        WebElement subscribeButton = waitToBeClickable(driver, "subscribe-button");
        assertEquals("Subscribe", subscribeButton.getText());

        driver.quit();
    }

    @RepeatedTest(EntToEndUtil.ATTEMPTS_AMOUNT)
    void testUserGameList_whenSuccess_shouldGameInListAfterSubscription() {
        WebDriver driver = getNameInfoPAge();

        String gameName = driver.findElement(By.className("App-game-page-info-title")).getText();

        clickSubscribeButtonIfNeededCondition(driver, "Unsubscribe");

        waitToBeClickable(driver, "profile-dropdown-button").click();
        driver.findElement(By.id("user-gameList-button")).click();


        String gameNameInList = driver.findElements(By.className("App-game-content-list-game")).get(0)
                .findElement(By.className("App-game-content-list-game-info-title")).getText();
        assertEquals(gameName, gameNameInList);

        driver.quit();
    }

    private WebElement waitToBeClickable(WebDriver driver, String elementId) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(1));
        return wait.until(ExpectedConditions.elementToBeClickable(By.id(elementId)));
    }

    private void clickSubscribeButtonIfNeededCondition(WebDriver driver, String unwantedButtonState) {

        if (driver.findElement(By.id("subscribe-button")).getText().equals(unwantedButtonState)) {
            driver.findElement(By.id("subscribe-button")).click();
        }
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        wait.until(ExpectedConditions.elementToBeClickable(By.id("subscribe-button"))).click();

    }

    private WebDriver getNameInfoPAge() {
        WebDriver driver = EntToEndUtil.getGpbWebDriver();

        EntToEndUtil.loginInToGpb(driver, adminEmail, adminPassword);

        if (driver.findElements(By.className("App-game-content-list-game")).isEmpty()) {
            EntToEndUtil.gameSearch(driver, GAME_NAME);
        }


        driver.findElement(By.className("App-game-content-list-game")).click();
        return driver;
    }
}
