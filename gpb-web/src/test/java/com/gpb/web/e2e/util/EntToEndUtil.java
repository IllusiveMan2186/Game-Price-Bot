package com.gpb.web.e2e.util;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public class EntToEndUtil {

    public static final String GPB_URL = "http://localhost:3000/";

    public static WebDriver getGpbWebDriver() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--remote-allow-origins=*");
        WebDriver driver = new ChromeDriver(options);
        driver.get(GPB_URL);
        return driver;
    }

    public static void loginInToGpb(WebDriver driver, String adminEmail, String adminPassword) {
        driver.findElement(By.id("login-button")).click();
        driver.findElement(By.id("login-email")).sendKeys(adminEmail);
        driver.findElement(By.id("login-password")).sendKeys(adminPassword);
        driver.findElement(By.id("login-form-button")).click();
    }
}
