package com.gpb.backend;

import com.gpb.backend.e2e.util.EntToEndUtil;
import org.openqa.selenium.WebDriver;

public class BasicEndToEndTest {

    private final String gpbUrl = System.getProperty("e2e.url");

    protected WebDriver getGpbWebDriver() {
        return EntToEndUtil.getGpbWebDriver(gpbUrl);
    }
}
