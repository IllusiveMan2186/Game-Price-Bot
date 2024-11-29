package com.gpb.web.e2e;

import com.gpb.web.e2e.util.EntToEndUtil;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("e2e")
class UserAuthenticationEndToEndTest {

    private final String adminEmail = System.getProperty("e2e.email");
    private final String adminPassword = System.getProperty("e2e.password");

    @Test
    void testUserLogin_Successfully_LoginInProfile() {
        WebDriver driver = EntToEndUtil.getGpbWebDriver();


        assertEquals(0, driver.findElements(By.id("profile-dropdown-button")).size());

        EntToEndUtil.loginInToGpb(driver, adminEmail, adminPassword);


        assertTrue(driver.findElement(By.id("profile-dropdown-button")).isDisplayed());

        driver.quit();
    }

    @Test
    void testUserLogin_WrongCredential_ShowError() {
        WebDriver driver = EntToEndUtil.getGpbWebDriver();


        assertEquals(0, driver.findElements(By.id("profile-dropdown-button")).size());

        EntToEndUtil.loginInToGpb(driver, "notExistingEmail@mail.com", "notExistingPassword");

        String errorMessage = driver.findElement(By.id("pills-login"))
                .findElement(By.className("Error")).getText();
        assertFalse(errorMessage.isEmpty());

        driver.quit();
    }

    @Test
    void testUserLogout_Successfully_LogoutOfProfile() {
        WebDriver driver = EntToEndUtil.getGpbWebDriver();


        EntToEndUtil.loginInToGpb(driver, adminEmail, adminPassword);
        driver.findElement(By.id("profile-dropdown-button")).click();
        driver.findElement(By.id("logout-button")).click();


        assertTrue(driver.findElement(By.id("login-button")).isDisplayed());
        assertEquals(0, driver.findElements(By.id("profile-dropdown-button")).size());

        driver.quit();
    }
}
