package com.gpb.web.e2e.util;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

public class EntToEndUtil {

    public static final String GPB_URL = "http://localhost:3000/";
    public static final long WAIT_TIME = 20;

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
        driver.findElement(By.id("login-button")).click();
        driver.findElement(By.name("email")).sendKeys(adminEmail);
        driver.findElement(By.name("password")).sendKeys(adminPassword);
        driver.findElement(By.id("pills-login")).findElement(By.className("btn")).click();

        choseLocale(driver, "EN");
    }

    private static void choseLocale(WebDriver driver, String locale) {
        List<WebElement> elementList = driver.findElement(By.id("footer-language-choice"))
                .findElements(By.className("btn"));
        Optional<WebElement> englishLanguageChoice = elementList.stream()
                .filter(element -> element.getText().equals(locale))
                .findFirst();
        englishLanguageChoice.ifPresent(WebElement::click);
    }

    public static void gameSearch(WebDriver driver, String gameName) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(WAIT_TIME));
        driver.findElement(By.id("game-search-input-field")).sendKeys(gameName);
        driver.findElement(By.id("game-search-button")).click();
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.className("App-game-content-list-loading")));
    }
}
