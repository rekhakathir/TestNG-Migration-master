package org.example;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.kms.katalon.core.webui.driver.DriverFactory;
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords;

import io.github.bonigarcia.wdm.WebDriverManager;


public class SimpleTest {

    private static WebDriver driver;

    @BeforeClass
    public void setUp() {
        System.out.println("Set up");
        driver = DriverFactory.getWebDriver();
        
    }

    @Test(groups = { "fast" })
    public void aFastTest() {
        System.out.println("Fast test");
    }

    @Test(groups = { "slow" })
    public static void aSlowTest() {
    	System.setProperty("webdriver.chrome.driver", "D://Katalon_Studio_PE_Windows_64-8.5.2//configuration//resources//drivers//chromedriver_win32//chromedriver.exe");
        driver = new ChromeDriver();
        DriverFactory.changeWebDriver(driver);
        System.out.println("Slow test");
        driver.get("http://www.google.com");
        driver.manage().window().maximize();
        driver.quit();
//        WebUiBuiltInKeywords.openBrowser("http://www.google.com");
//        
//        WebUiBuiltInKeywords.closeBrowser();
    }

    @AfterClass
    public void teardown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
