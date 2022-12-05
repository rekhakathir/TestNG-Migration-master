package src.main.java.baseClasses;


import org.openqa.selenium.*;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.WebDriverWait;

import src.main.java.logger.Log;
import src.main.java.util.ApplicationProperties;
import src.main.java.util.SeleniumWait;

import org.openqa.selenium.JavascriptExecutor;
import javax.swing.*;

import java.time.Duration;
import java.util.List;
import src.main.java.baseClasses.BaseTestPage;


public abstract class BasePage<T extends BasePage<?>> extends BaseTestPage {
    protected static WebDriver driver;
    //private Duration LOAD_TIMEOUT = ApplicationProperties.DEFAULT_TIMEOUT.getIntVal();
    protected SeleniumWait wait;

    public BasePage(WebDriver driver) {
        super(driver);
        this.driver = this.getDriver();
        PageFactory.initElements( driver, this);
    }

    public T openPage(Class<T> Clazz) {
        T page = null;
        try {
            driver = this.getDriver();
            page = PageFactory.initElements(driver, Clazz);
            ExpectedCondition<?> pageLoadCondition = ((BasePage<?>) page).getPageLoadCondition();
            waitForPageToLoad(pageLoadCondition);
        } catch (NoSuchElementException e) {
            throw new IllegalStateException(String.format("This is not the %s page", Clazz.getSimpleName()));
        }
        return page;
    }
    
   

  //  protected void waitForPageToLoad(ExpectedCondition<?> pageLoadCondition) {
    protected void waitForPageToLoad(ExpectedCondition<?> pageLoadCondition) {
        WebDriverWait wait = new WebDriverWait(driver,60);
        wait.until(pageLoadCondition);
    }

    protected void waitForElementEnabled(WebElement element) {
        WebDriverWait wait = new WebDriverWait(driver,60);
        wait.until(ExpectedConditions.elementToBeClickable(element));
    }

    protected abstract ExpectedCondition<?> getPageLoadCondition();

    /*public void typeInputField(WebElement element, String value) throws InterruptedException {
        String val = value;
        this.wait.untilElementIsEnabled(element,5000,3000);
        element.clear();
        this.wait.untilElementIsEnabled(element,5000,3000);
        element.sendKeys(val);
        this.wait.waitForPageToLoad();
        this.wait.waitForPageToLoad();
        element.sendKeys(Keys.ENTER);
        Thread.sleep(3000);
        Log.LOGGER.info("Entered value: " + this.getAttributeValue(element));
        this.wait.waitForPageToLoad();
    }
*/
    public void typeInputField(WebElement element, String value) throws InterruptedException {
        String val = value;
        this.wait.untilElementIsEnabled(element,5000,3000);
        element.clear();
        this.wait.untilElementIsEnabled(element,5000,3000);
        element.sendKeys(val);
        this.wait.waitForPageToLoad();
        element.sendKeys(Keys.ENTER);
        this.wait.waitForPageToLoad();
        this.wait.waitForPageToLoad();
        Log.LOGGER.info("Entered value: " + this.getAttributeValue(element));
        this.wait.waitForPageToLoad();
        this.wait.waitForPageToLoad();
    }
    public void typeTab(WebElement element, String value) throws InterruptedException {
        String val = value;
        element.sendKeys(val);
        this.wait.waitForPageToLoad();
        element.sendKeys(Keys.TAB);
        this.wait.waitForPageToLoad();
        Log.LOGGER.info("Entered value: " + this.getAttributeValue(element));
        this.wait.waitForPageToLoad();
    }

    public void typeESCAPE(WebElement element, String value) throws InterruptedException {
        String val = value;
        element.sendKeys(val);
        this.wait.waitForPageToLoad();
        element.sendKeys(Keys.ESCAPE);
        Log.LOGGER.info("Entered value: " + this.getAttributeValue(element));
        this.wait.waitForPageToLoad();
    }

    public void type(WebElement element, String value){
        String val = value;
        this.wait.waitForElementToBeVisible(element);
        element.clear();
        element.sendKeys(val);
        this.wait.waitForPageToLoad();
        element.sendKeys(Keys.ESCAPE);
        this.wait.waitForPageToLoad();
    }

    public void SetElementText( WebElement textInput, String textValue) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript(
                "document.getElementById(textInput).setAttribute('value', textValue);");

    }

    public void clickElement(WebElement element) {
        highlightElement(element);
        element.click();
     // this.wait.waitForPageToLoad();
    }

    public WebElement IframeName(By locator) {
        List<WebElement> iframes = this.getDriver().findElements(By.tagName("iframe"));
        for (WebElement iframe : iframes) {
            this.getDriver().switchTo().frame(iframe);
            if (this.getDriver().findElements(locator).size() > 0) {
                this.getDriver().switchTo().defaultContent();
                return iframe;
            }
        }
        this.getDriver().switchTo().defaultContent();
        return null;
    }

    public void clickElement(By locator) {
        this.clickElement(this.driver.findElement(locator));
	
    }
	 public void typeInputFieldBy(By locator, String value) throws InterruptedException {
        String val = value;
        this.wait.untilElementIsEnabled(locator);
      //  this.driver.findElement(locator).click();
        //this.wait.untilElementIsEnabled(locator);
        this.driver.findElement(locator).sendKeys(val);
       // this.wait.waitForPageToLoad();
        //this.driver.findElement(locator).sendKeys(Keys.ENTER);
        this.wait.waitForPageToLoad();
    }


    public WebElement highlightElement(WebElement element) {
        if (driver instanceof JavascriptExecutor) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].style.border='3px solid purple'", element);
        }
        return element;
    }
    public void select(By locator, By locator1)  {

        try {
            this.wait.waitForElementToBeClickable(locator).click();
            // this.wait.waitForPageToLoad();
            this.wait.waitForElementToBeClickable(locator1).click();
            this.wait.waitForPageToLoad();

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

	
	
	 //public void waitForLoad(WebDriver driver) { new
	// WebDriverWait(driver,30).until((ExpectedCondition<Boolean>) wd ->
	// ((JavascriptExecutor)wd).executeScript("return document.readyState").equals(
	//"complete")); }
	 
}