package com.gpb.backend.e2e.util;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class EntToEndUtil {

    public static final String GPB_URL = "http://localhost:3000/";
    public static final long WAIT_TIME = 30;
    public static final int ATTEMPTS_AMOUNT = 10;

    public static WebDriver getGpbWebDriver() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--remote-allow-origins=*");
        WebDriver driver = new ChromeDriver(options);
        driver.get(GPB_URL);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(1));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(1));
        return driver;
    }

    public static void loginInToGpb(WebDriver driver, String adminEmail, String adminPassword) {
        EntToEndUtil.waitToBeClickable(driver, "login-button").click();
        driver.findElement(By.name("email")).sendKeys(adminEmail);
        driver.findElement(By.name("password")).sendKeys(adminPassword);
        EntToEndUtil.waitToBeClickable(driver, "pills-login")
                .findElement(By.className("btn")).click();

        choseLocale(driver);
    }

    private static void choseLocale(WebDriver driver) {
        if (!driver.findElements(By.id("profile-dropdown-button")).isEmpty() &&
                !driver.findElement(By.id("profile-dropdown-button")).getText().equals("Profile")) {

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            ((JavascriptExecutor) driver).executeScript("arguments[0].click();",
                    EntToEndUtil.waitToBeClickable(driver, "EN".toLowerCase() + "-locale"));
        }
    }


    public static void gameSearch(WebDriver driver, String gameName) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(WAIT_TIME));
        driver.findElement(By.id("game-search-input-field")).sendKeys(gameName);
        driver.findElement(By.id("game-search-button")).click();
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.className("app-content-loading")));
    }

    public static WebElement waitToBeClickable(WebDriver driver, String elementId) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(1));
        return wait.until(ExpectedConditions.elementToBeClickable(By.id(elementId)));
    }
}
