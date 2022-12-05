package src.main.java.baseClasses;

import java.io.File;
import java.lang.reflect.Method;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.google.common.base.Preconditions;
import com.vimalselvam.testng.listener.ExtentTestNgFormatter;

import src.main.java.logger.Log;
import src.main.java.util.ApplicationProperties;
import src.main.java.util.DriverManager;
import src.main.java.util.ScreenShotUtil;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Driver;
import java.text.MessageFormat;

import org.apache.log4j.helpers.ThreadLocalMap;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.TestNG;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Parameters;
import org.testng.annotations.Optional;
import org.testng.annotations.AfterMethod;

/**
 * Selenium BaseSeleniumTest class.
 */
public class BaseSeleniumTest implements BaseTestCase {

    /**
     * The web driver for this class.
     */
    private ThreadLocal<WebDriver> driver = new ThreadLocal<WebDriver>();

    /**
     * Gets the thread safe web driver.
     * Call this method to get the driver object and launch the browser
     * @return the web driver
     */
    public WebDriver getDriver() {
        if (driver.get() == null) {
            driver.set(createDriver());
        }
            return driver.get();
    }

    /**
     * Quit and remove the web driver
     */
    public void quitAndRemoveDriver() {
        if (driver.get() != null) {
            driver.get().quit();
            driver.remove();
        }
    }

    /**
     * Gets the browser driver. Override this to implement custom driver initialization.
     * @return
     */
    protected WebDriver createDriver()
    {
        return DriverManager.getBrowser();
    }

    /**
     * Runs before all tests.
     *
     * @param browser sets the browser property
     * @param platform sets the platform property
     * @param application sets the application property
     * @param includePattern sets the include pattern property
     * @param excludePattern sets the exclude pattern property
     * @param environment sets the environment property
     */
    @Parameters({
        "browser",
        "platform",
        "application",
        "includePattern",
        "excludePattern",
        "environment"})
    @BeforeTest(alwaysRun = true, enabled = true)
    protected void testSetup(@Optional String browser,
            @Optional String platform,
            @Optional String application,
            @Optional String includePattern,
            @Optional String excludePattern,
            @Optional String environment) {

        // data provider filters
        String incPattern = "";
        if (includePattern != null) {
            incPattern = includePattern;
        } else if (System.getProperty("includePattern") == null) {
            incPattern = "NONE";
        }

        System.setProperty("includePattern", incPattern);

        if (excludePattern != null) {
            System.setProperty("excludePattern", excludePattern);
        }

        String webBroswer = "";
        // global variables
        if (browser != null) {
            webBroswer = browser;
        } else {
            webBroswer = ApplicationProperties.BROWSER.getStringVal("Chrome");
        }

        System.setProperty("browser", webBroswer);

        if (platform != null) {
            System.setProperty("platform", platform);
        }

        if (application != null) {
            System.setProperty("application", application);
        }

        if (environment != null) {
            System.setProperty("environment", environment);
        }
    }

    /**
     * Method executed before methods.
     *
     * @param method The method executed
     */
    @BeforeMethod
    public void beforeMethod(Method method) {
        Log.LOGGER.info("Running the test method: " + method.getName());
        if (method.getName().contains("WebServiceTest")) {
            Log.LOGGER.info("Testing Web Service: "
                    + method.getName().replace("WebServiceTest", ""));
        }
    }

    /**
     * Executes after every method. Takes a screen shot if enabled in property
     * files.
     *
     * @param result The test result
     */
    @AfterMethod()
    public void afterMethod(ITestResult result) {
        if (driver.get() != null) {
            String methodName = result.getMethod().getMethodName();

            if (DriverManager.isRemote()) {
                try {
                    ((JavascriptExecutor)getDriver()).executeScript("sauce:job-name=" + methodName);
                } catch (Exception e) {
                    Log.LOGGER.info("Failed to set Sauce job name: " + e.getMessage());
                }

                try {
                    String jobResult = result.isSuccess() ? "passed" : "failed";
                    ((JavascriptExecutor)getDriver()).executeScript("sauce:job-result=" + jobResult);
                } catch (Exception e) {
                    Log.LOGGER.info("Failed to set Sauce job result: " + e.getMessage());
                }
            }

            if (result.getStatus() == ITestResult.FAILURE) {
                try{
                    String browser = ApplicationProperties.BROWSER.getStringVal();
                    String fullScreenShotName = ScreenShotUtil.generateScreenshotFileName(methodName);
                    String fullScreenShotPath  = (browser.equalsIgnoreCase("IE") || browser.equalsIgnoreCase("INTERNETEXPLORER")) ?
                            ScreenShotUtil.captureScreenShot(getDriver(), fullScreenShotName) :
                            ScreenShotUtil.captureBase64ScreenShot(getDriver(), fullScreenShotName);

                    Reporter.log("Saved screentshot at: " + fullScreenShotPath , true);
                    addScreenShotToExtenReport(fullScreenShotPath , result);
                } catch (Exception e)
                {
                    Log.LOGGER.error(MessageFormat.format("Could not capture screenshot. Exception {0}", e.getMessage()));
                }

            }
        }

        quitAndRemoveDriver();
        Log.LOGGER.info("Quit Driver");
    }

    public void addStepLog(String stepName) {
        try{
            ITestResult result = Reporter.getCurrentTestResult();
            Preconditions.checkState(result != null);
            ExtentTest test = (ExtentTest) result.getAttribute("test");
            Preconditions.checkState(test != null);
            test.log(Status.PASS, stepName);
        } catch (IllegalStateException e)
        {
            Log.LOGGER.error(MessageFormat.format("Failed to add step log. Make sure you are running from a testng file", e.getMessage()));
        }

    }

    /**
     * Adds screen shot to extent report.
     *
     * @param stepName the name of the step
     */
    public void addScreenShotStep(String stepName) {
        try {
            String fullScreenShotName = ScreenShotUtil.generateScreenshotFileName(stepName);
            String fullScreenShotPath = ScreenShotUtil.captureBase64ScreenShot(getDriver(), fullScreenShotName);
            fullScreenShotPath = getScreenshotRelativePath(fullScreenShotPath);
            Log.LOGGER.info(MessageFormat.format("Screenshot Step Path = {0}", fullScreenShotPath));
            ITestResult result = Reporter.getCurrentTestResult();
            Preconditions.checkState(result != null);
            ExtentTest test = (ExtentTest) result.getAttribute("test");
            Preconditions.checkState(test != null);

           //test.log(Status.PASS, stepName, MediaEntityBuilder.createScreenCaptureFromPath(fullScreenShotPath).build());
            Log.LOGGER.info(MessageFormat.format("Screenshot attached from Path = {0}", fullScreenShotPath));

        } catch (Exception e) {
            Log.LOGGER.info(MessageFormat.format("Unable to attach the screenshot step to the report. Please verify test execution using ExtentTestNgFormatter lister. Exception {0}", e.getMessage()));
        }
    }

    /**
     * Adds screen shot to extent report.
     *
     * @param screenShotPath The screen shot path.
     * @param result The test result.
     */
    public void addScreenShotToExtenReport(String screenShotPath, ITestResult result) {
        try {
            if (screenShotPath != null) {
                File sc = new File(screenShotPath);
                if(!sc.exists()) {
                    throw new IOException();
                }
                screenShotPath = sc.getCanonicalPath();
                ExtentTestNgFormatter formatter = ExtentTestNgFormatter.getInstance();

                screenShotPath = getScreenshotRelativePath(screenShotPath);

                if (formatter != null) {
                    if(result != null && result.getAttribute("test") != null) {
                        formatter.addScreenCaptureFromPath(result, screenShotPath);
                    } else {
                        formatter.addScreenCaptureFromPath(screenShotPath);
                    }
                }
            }
        } catch (IOException e) {
            Log.LOGGER.info(MessageFormat.format("Unable to attach the screenshot at path {0} to the report. Please verify test execution using Extent lister. Exception {1}", screenShotPath, e.getMessage()));
        }
    }

    /**
     * Returns the screenshot path relative to the extent reporting path
     * @param screenshotAbsolutepath
     * @return
     */
    private String getScreenshotRelativePath(String screenshotAbsolutepath) {
        String reportPathStr = System.getProperty("reportPath");
        File reportPath;

        try {
            reportPath = new File(reportPathStr);
        } catch (NullPointerException e) {
            reportPath = new File(TestNG.DEFAULT_OUTPUTDIR);
        }

        Path extentReportPath = Paths.get(reportPath.getAbsolutePath());
        Path ssPath = Paths.get(screenshotAbsolutepath);
        return extentReportPath.relativize(ssPath).toString();
    }
}
