package src.main.java.Pageclasses;


import java.lang.Exception;
import java.lang.Override;
import java.lang.String;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedCondition;

import src.main.java.baseClasses.BasePage;

public class SauceLab extends BasePage
{
	//public static WebDriver driver;

    /**
     * Page class.
     * @param driver
     */
	
	public SauceLab(WebDriver driver) {
        super(driver); 
        PageFactory.initElements( driver, this);
        }
	
	public void openBrowser(String URL) 
	{
       this.driver.navigate().to(URL);
       // clickWhenVisible(usernameLocator);
        //return (SF_LoginPage) openPage(SF_LoginPage.class);
    }
	
 public static void execute() throws Exception {
    // set timeout for driver actions (similar to step timeout)
   
    By by;
    boolean booleanResult;

    // 1. Navigate to '{{ApplicationURL}}'
    //    Navigates the specified URL (Auto-generated)
    GeneratedUtils.sleep(500);
   

    // 2. Click 'user-name'
    GeneratedUtils.sleep(500);
    by = By.cssSelector("#user-name");
    driver.findElement(by).click();

    // 3. Type 'standard_user' in 'user-name'
    GeneratedUtils.sleep(500);
    by = By.cssSelector("#user-name");
    driver.findElement(by).sendKeys("standard_user");

    // 4. Click 'password'
    GeneratedUtils.sleep(500);
    by = By.cssSelector("#password");
    driver.findElement(by).click();

    // 5. Type 'secret_sauce' in 'password'
    GeneratedUtils.sleep(500);
    by = By.cssSelector("#password");
    driver.findElement(by).sendKeys("secret_sauce");

    // 6. Click 'login-button'
    GeneratedUtils.sleep(500);
    by = By.cssSelector("#login-button");
    driver.findElement(by).click();

  }

@Override
protected ExpectedCondition getPageLoadCondition() {
	// TODO Auto-generated method stub
	return null;
}

  

  
}
