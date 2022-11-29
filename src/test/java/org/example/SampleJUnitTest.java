package org.example;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import io.github.bonigarcia.wdm.WebDriverManager;

public class SampleJUnitTest {
    private WebDriver driver;

    @BeforeClass
    public void setUp() {
        System.out.println("Set up");
        WebDriverManager.chromedriver().setup();
    }

    @Before
    public void setupTest() {
        driver = new ChromeDriver();
    }

    @After
    public void teardown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    public void aFastTest() {
        System.out.println("Fast test");
    }

    @Test
    public void aSlowTest() {
        System.out.println("Slow test");
        driver.get("http://www.google.com");
    }
}
