package com.gpb.backend.e2e;

import com.gpb.backend.BasicEndToEndTest;
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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("e2e")
class UserAuthenticationEndToEndTest extends BasicEndToEndTest {

    private final String adminEmail = System.getProperty("e2e.email");
    private final String adminPassword = System.getProperty("e2e.password");

    @RepeatedTest(EntToEndUtil.ATTEMPTS_AMOUNT)
    void testUserLogin_whenSuccess_shouldLoginInProfile() {
        WebDriver driver = getGpbWebDriver();


        assertEquals(0, driver.findElements(By.id("profile-dropdown-button")).size());

        EntToEndUtil.loginInToGpb(driver, adminEmail, adminPassword);


        assertTrue(driver.findElement(By.id("profile-dropdown-button")).isDisplayed());

        driver.quit();
    }

    @RepeatedTest(EntToEndUtil.ATTEMPTS_AMOUNT)
    void testUserLogin_whenWrongCredential_shouldShowError() {
        WebDriver driver = getGpbWebDriver();

        assertEquals(0, driver.findElements(By.id("profile-dropdown-button")).size());

        EntToEndUtil.loginInToGpb(driver, "notExistingEmail@mail.com", "notExistingPassword");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofMillis(1000));
        WebElement errorElement = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.className("Error"))
        );

        assertFalse(errorElement.getText().isEmpty());

        driver.quit();
    }

    @RepeatedTest(EntToEndUtil.ATTEMPTS_AMOUNT)
    void testUserLogout_whenSuccess_shouldLogoutOfProfile() throws InterruptedException {
        WebDriver driver = getGpbWebDriver();


        EntToEndUtil.loginInToGpb(driver, adminEmail, adminPassword);
        EntToEndUtil.waitToBeClickable(driver, "profile-dropdown-button").click();
        driver.findElement(By.id("logout-button")).click();


        assertTrue(driver.findElement(By.id("login-button")).isDisplayed());
        assertEquals(0, driver.findElements(By.id("profile-dropdown-button")).size());

        driver.quit();
    }
}
