package src.main.java.util;

import io.github.bonigarcia.wdm.WebDriverManager;
import src.main.java.logger.Log;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.ie.InternetExplorerOptions;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import src.main.java.Constants.Browsers;
import src.main.java.Constants.RemoteHosts;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class that manages the creation of web drivers.
 */
public class DriverManager {

    /**
     * The logger.
     */
    private static final Logger LOG = Logger.getLogger(DriverManager.class.getName());

    /**
     * The platform of the execution machine.
     */
    private static final String PLATFORM;

    /**
     * Headless execution switch.
     */
    private static final boolean HEADLESS;

    /**
     * Remote driver switch.
     */
    private static final boolean REMOTE;

    /**
     * The selenium host.
     */
    private static final String SELENIUM_HOST;

    /**
     * The selenium port.
     */
    private static final String SELENIUM_PORT;

    /**
     * The selenium remote url.
     */
    private static final String SELENIUM_REMOTE_URL;

    /**
     * The Sauce Labs username
     */
    private static final String SAUCE_USERNAME;

    /**
     * The Sauce Labs key
     */
    private static final String SAUCE_ACCESS_KEY;

    /**
     * The Sauce Labs remote url
     */
    private static final String SAUCE_REMOTE_URL;

    /**
     * The Sauce Labs tunnel id
     */
    private static final String TUNNEL_IDENTIFIER;

    /**
     * Tunnel identifier constant
     */
    private static final String TUNNEL_IDENTIFIER_CONST = "tunnel-identifier";

    /**
     * The selenium browser window dimension.
     */
    private static final Dimension BROWSER_SIZE;

    /**
     * The browser window width.
     */
    private static final Integer BROWSER_WIDTH;

    /**
     * The browser window height.
     */
    private static final Integer BROWSER_HEIGHT;

    /**
     * The path to the driver executables. Drivers are downloaded at run time if
     * they do not exists.
     */
    private static final String DRIVER_DOWNLOAD_PATH = ConfigurationManager.getBundle().getString("wdm.targetPath");

	private static final ThreadLocal<String> sessionBrowser = null;

    /**
     * Session Id.
     */
    private final ThreadLocal<String> sessionId = new ThreadLocal<>();

    /**
     * Session Browser.
     */
    //private final ThreadLocal<String> sessionBrowser = new ThreadLocal<>();

    /**
     * Session Platform.
     */
   private final ThreadLocal<String> sessionPlatform = new ThreadLocal<>();

    /**
     * Session Version.
     */
   private final ThreadLocal<String> sessionVersion = new ThreadLocal<>();

    static {
        REMOTE = ApplicationProperties.REMOTE.getBooleanVal(false);
        PLATFORM = ApplicationProperties.PLATFORM.getStringVal();
        HEADLESS = ApplicationProperties.HEADLESS.getBooleanVal(false);

        //CHECKSTYLE:OFF
        BROWSER_HEIGHT = ApplicationProperties.BROWSER_HEIGHT.getStringVal().isEmpty() ? 1024 : Integer.parseInt(ApplicationProperties.BROWSER_HEIGHT.getStringVal());
        BROWSER_WIDTH = ApplicationProperties.BROWSER_WIDTH.getStringVal().isEmpty() ? 1280 : Integer.parseInt(ApplicationProperties.BROWSER_WIDTH.getStringVal());
        BROWSER_SIZE = new Dimension(BROWSER_WIDTH, BROWSER_HEIGHT);
        //CHECKSTYLE:ON

        //Applicable For Selenium Grid
        SELENIUM_HOST = ApplicationProperties.SELENIUM_HOST.getStringVal();
        SELENIUM_PORT = ApplicationProperties.SELENIUM_PORT.getStringVal();
        SELENIUM_REMOTE_URL = "http://" + SELENIUM_HOST + ":" + SELENIUM_PORT + "/wd/hub";

        //Applicable For Sauce Labs
        SAUCE_USERNAME = ApplicationProperties.SAUCE_USERNAME.getStringVal();
        SAUCE_ACCESS_KEY = ApplicationProperties.SAUCE_ACCESS_KEY.getStringVal();
        TUNNEL_IDENTIFIER = ApplicationProperties.TUNNEL_IDENTIFIER.getStringVal();
        SAUCE_REMOTE_URL = "https://ondemand.saucelabs.com/wd/hub";
    }

    /**
     * creates the browser driver specified in the system property "browser" or
     * the configuration parameter 'browser' specified in the
     * 'environment.properties' file. if no property is set then a chrome
     * browser driver is created. The allowed properties are CHROME, IE. e.g to
     * run with IE, pass in the option -Dbrowser=IE at runtime
     * 
     * TODO consider - what if this is running on the Linux server which has no IE support?
     *
     * @return WebDriver
     */
    public static WebDriver getBrowser() {
        Browsers browser;
        WebDriver dr;
        System.setProperty("wdm.targetPath", DRIVER_DOWNLOAD_PATH);
        String browserName = ApplicationProperties.BROWSER.getStringVal();
        browser = Browsers.browserForName(browserName);
        Log.LOGGER.info("Browser is set to" + browserName);

        switch (browser) {
            case IE:
            case INTERNETEXPLORER:
                dr = createIEDriver();
                break;
            case CHROME:
                dr = createChromeDriver();
                break;
            case FIREFOX:
                dr = createFireFoxDriver();
                break;
            default:
                dr = createChromeDriver();
                break;
        }
        return dr;
    }

    /**
     * Returns value of remote property
     *
     * @return A boolean value of remote property
     */
    public static boolean isRemote()
    {
        String isRemoteString = ApplicationProperties.REMOTE.getStringVal();

        return (isRemoteString != null && !isRemoteString.isEmpty() && isRemoteString.equalsIgnoreCase("true")) || REMOTE;
    }

    /**
     * Creates a chrome driver.
     * @return Returns a chrome driver instance.
     */
    public static WebDriver createChromeDriver() {
        //below code lets you switch between a local driver and the grid:

        if (isRemote()) {
            return createRemoteDriver(getChromeOptions());
        } else {
            WebDriverManager.getInstance(ChromeDriver.class).setup();
            return new ChromeDriver(getChromeOptions());
        }
    }

    /**
     * Creates an IE driver.
     * @return Returns an IE driver instance.
     */
    public static WebDriver createIEDriver() {
        //below code lets you switch between a local driver and the grid:
        WebDriverManager.iedriver().arch32().setup();
        WebDriverManager.getInstance(InternetExplorerDriver.class).setup();

        if (isRemote()) {
            return createRemoteDriver(getInternetExploreOptions());
        } else {
            return new InternetExplorerDriver(getInternetExploreOptions());
        }

    }

    /**
     * Creates a Firefox web driver.
     * @return Returns a firefox web driver instance.
     */
    public static WebDriver createFireFoxDriver() {
        //below code lets you switch between a local driver and the grid:
        WebDriverManager.firefoxdriver().arch32().setup();
        if (isRemote()) {
            return createRemoteDriver(getFirefoxOptions());
        } else {
            return new FirefoxDriver(getFirefoxOptions());
        }
    }

    /**
     * Creates a remote web driver.
     * @param capabilities the desired driver capabilities.
     * @return a remote web driver instance.
     */
	
	  public static WebDriver createRemoteDriver(Capabilities capabilities){
	  
	  WebDriver remoteWebDriver = null; 
	  String remoteHost =ApplicationProperties.REMOTE_HOST.getStringVal();
	  
	  if (remoteHost != null) { 
		  RemoteHosts remoteHostName = RemoteHosts.remoteHostForName(remoteHost);
	  
	 

	switch (remoteHostName) 
	  
	  {
	  case GRID: 
		  
		  try 
		  {
	  Log.LOGGER.info("Starting remote driver on Grid..."); 
	 remoteWebDriver = new RemoteWebDriver(new URL(SELENIUM_REMOTE_URL), capabilities);                                                                                             Log.LOGGER.info(
	  MessageFormat.format( "Running on remote Grid instance at {0}",SELENIUM_REMOTE_URL)); 
          }                                                                                                                                                                                                                                               
		  catch (MalformedURLException e)
		  {
	 Log.info(SELENIUM_REMOTE_URL + " Error: " + e.getMessage()); throw new
	 RuntimeException(e);
	 // just give up 
	 } 
		  break; 
	 
	  case SAUCE: 
		 try 
		 {
	  Log.LOGGER.info("Starting remote driver on Sauce Labs...");                   
	  remoteWebDriver =new RemoteWebDriver(new URL(SAUCE_REMOTE_URL), capabilities);
	  Log.LOGGER.info( MessageFormat.format(
	  "Running on remote Sauce Labs instance at {0}", SAUCE_REMOTE_URL)); 
	     }
		 catch
	  (MalformedURLException e) { LOG.info(SAUCE_REMOTE_URL + " Error: " +
	  e.getMessage()); throw new RuntimeException(e); 
	  // just give up 
	  } 
		 break;
	   default: Log.LOGGER.info( MessageFormat.format(
	  "Remote host {0} is not supported.", remoteHostName));
	   
	  } } 
	  else 
	  { 
	Log.LOGGER.info("Could not read remote.host value from environment.properties. "); }
	  
	  return remoteWebDriver;
	  }


    public static ChromeOptions getChromeOptions()
    {
        ChromeOptions options = new ChromeOptions();
        ChromeOptions defaultOptions = getDefaultChromeOptions();
        ChromeOptions customOptions = getCustomChromeOptions();
        options.merge(defaultOptions);
        options.merge(customOptions);

        // Add arguments back, these are cleared during merge
        Map<String, Object> defaultMap = defaultOptions.asMap();
        Map<String, List<String>> nonDefaultCapabilities = (Map<String, List<String>> ) defaultMap.get("goog:chromeOptions");
        List<String> defaultArgs = nonDefaultCapabilities.get("args");
        options.addArguments(defaultArgs);

        Map<String, Object> customMap = customOptions.asMap();
        Map<String, List<String>> nonCustomCapabilities = (Map<String, List<String>> ) customMap.get("goog:chromeOptions");
        List<String> customArgs = nonCustomCapabilities.get("args");
        options.addArguments(customArgs);

        return options;
    }

    /**
     * Gets the custom chrome options from properties file
     * @return Chrome Options
     */
    public static ChromeOptions getCustomChromeOptions(){

        ChromeOptions options = new ChromeOptions();
        HashMap<String, String> customOptions = DriverManager.getCustomChromeOptionsFromPropertiesFile();

        for(Map.Entry<String, String> entry : customOptions.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();

            // Get all the args
            if(key.equalsIgnoreCase("args"))
            {
                String[] args = value.split(",");
                options.addArguments(args);
            }

            options.setCapability(key, value);
        }

        return options;
    }

    /**
     * Gets the default chrome options.
     *
     * @return Returns chrome options.
     */
    public static ChromeOptions getDefaultChromeOptions() {

        ChromeOptions options = new ChromeOptions();
        //To start chrome without security warning
        options.addArguments("test-type");
        //To start the chrome in Maximized mode
        options.addArguments("start-maximized");
        options.addArguments("--ignore-certificate-errors");
        options.addArguments("--disable-web-security");
        options.addArguments("--allow-running-insecure-content");
        options.addArguments("--disable-plugins", "--disable-extensions", "--disable-popup-blocking");
        options.setExperimentalOption("useAutomationExtension", false);

        //Support for Headless browser testing
        if ((ApplicationProperties.HEADLESS.getStringVal() != null && ApplicationProperties.HEADLESS.getStringVal().equals("true")) || HEADLESS) {

            Log.LOGGER.info("Running Chrome Headless");
            options.addArguments("--headless");
        }

        DesiredCapabilities capabilities = new DesiredCapabilities();
        options.setCapability(ChromeOptions.CAPABILITY, capabilities);
        options.addArguments("disable-infobars");
        options.setCapability(ChromeOptions.CAPABILITY, generatePerformanceLoggingCapability());
        options.setCapability("version", ApplicationProperties.VERSION.getStringVal());
        options.setCapability("platform", ApplicationProperties.PLATFORM.getStringVal());
        options.setCapability(TUNNEL_IDENTIFIER_CONST, TUNNEL_IDENTIFIER);
        options.setCapability("build", getBuildName());
        options.setCapability("username", SAUCE_USERNAME);
        options.setCapability("accessKey", SAUCE_ACCESS_KEY);
        options.setCapability("unitTestChromeCapability", "ThisValueShouldBeOverwritten");

        return options;
    }

    public static InternetExplorerOptions getInternetExploreOptions()
    {
        InternetExplorerOptions options = new InternetExplorerOptions();
        options.merge(getDefaultInternetExploreOptions());
        options.merge(getCustomIEOptions());
        return options;
    }

    /**
     * Gets IE options from properties file
     * @return IE Options
     */
    public static InternetExplorerOptions getCustomIEOptions(){

        InternetExplorerOptions options = new InternetExplorerOptions();
        HashMap<String, String> customOptions = DriverManager.getCustomIEOptionsFromPropertiesFile();

        for(Map.Entry<String, String> entry : customOptions.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            options.setCapability(key, value);
        }

        return options;
    }

    /**
     * Gets the default IE browser options.
     *
     * @return the IE browser options
     */
    public static InternetExplorerOptions getDefaultInternetExploreOptions() {
        InternetExplorerOptions options = new InternetExplorerOptions();
        options.setCapability("version", ApplicationProperties.VERSION.getStringVal());
        options.setCapability("platform", ApplicationProperties.PLATFORM.getStringVal());
        options.setCapability(TUNNEL_IDENTIFIER_CONST, TUNNEL_IDENTIFIER);
        // Removed ignore zoom settings
        options.introduceFlakinessByIgnoringSecurityDomains().enablePersistentHovering().destructivelyEnsureCleanSession();
        options.setCapability("build", getBuildName());

        options.setCapability("username", SAUCE_USERNAME);
        options.setCapability("accessKey", SAUCE_ACCESS_KEY);
        options.setCapability("unitTestIECapability", "ThisValueShouldBeOverwritten");
        options.destructivelyEnsureCleanSession();

        return options;
    }

    /**
     * Gets the merged custom and default firefox options
     * @return
     */
    public static FirefoxOptions getFirefoxOptions()
    {
        FirefoxOptions options = new FirefoxOptions();
        FirefoxOptions defaultOptions = getDefaultFireFoxDesiredCapabilities();
        FirefoxOptions customOptions = getCustomFirefoxOptions();
        options.merge(defaultOptions);
        options.merge(customOptions);

        // Add arguments back, these are cleared during merge
        Map<String, Object> defaultMap = defaultOptions.asMap();
        Map<String, List<String>> nonDefaultCapabilities = (Map<String, List<String>> ) defaultMap.get("moz:firefoxOptions");
        List<String> defaultArgs = nonDefaultCapabilities.get("args");
        options.addArguments(defaultArgs);

        Map<String, Object> customMap = customOptions.asMap();
        Map<String, List<String>> nonCustomCapabilities = (Map<String, List<String>> ) customMap.get("moz:firefoxOptions");
        List<String> customArgs = nonCustomCapabilities.get("args");
        options.addArguments(customArgs);
        return options;
    }

    /**
     * Gets firefox options from properties file
     * @return firefox Options
     */
    public static FirefoxOptions getCustomFirefoxOptions(){

        FirefoxOptions options = new FirefoxOptions();
        HashMap<String, String> customOptions = DriverManager.getCustomFirefoxOptionsFromPropertiesFile();

        for(Map.Entry<String, String> entry : customOptions.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();

            if(key.equalsIgnoreCase("args"))
            {
                String[] args = value.split(",");
                options.addArguments(args);
            }

            options.setCapability(key, value);
        }

        return options;
    }

    /**
     * Gets the Firefox default desired capabilities.
     *
     * @return the desired capabilities for Firefox
     */
    public static FirefoxOptions getDefaultFireFoxDesiredCapabilities() {
        String domain = "deltads.ent";
        FirefoxProfile profile = new FirefoxProfile();
        profile.setPreference("network.negotiate-auth.trusted-uris", domain);
        profile.setPreference("network.negotiate-auth.delegation-uris", domain);
        profile.setPreference("network.automatic-ntlm-auth.trusted-uris", domain);
        FirefoxOptions options = new FirefoxOptions();
        options.setCapability("marionette", true);
        options.setHeadless(HEADLESS);
        options.setUnhandledPromptBehaviour(UnexpectedAlertBehaviour.ACCEPT).setAcceptInsecureCerts(true);
        options.setProfile(profile);
        options.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
        options.setCapability("version", ApplicationProperties.VERSION.getStringVal());
        options.setCapability("platform", Platform.fromString(ApplicationProperties.PLATFORM.getStringVal()));
        options.setCapability(TUNNEL_IDENTIFIER_CONST, TUNNEL_IDENTIFIER);
        options.setCapability("disable-restore-session-state", true);
        options.setCapability("build", getBuildName());
        options.setCapability("username", SAUCE_USERNAME);
        options.setCapability("accessKey", SAUCE_ACCESS_KEY);
        options.setCapability("unitTestFirefoxCapability", "ThisValueShouldBeOverwritten");
        options.addArguments("unitTestFirefoxArg");
        return options;
    }

    /**
     * Generates download folder capability.
     *
     * @return Map of download capabilities.
     */
    public static HashMap<String, Object> generateDownloadFolderCapability() {
        HashMap<String, Object> chromeAdditionalOptions
                = new HashMap<>();
        chromeAdditionalOptions.put("download.default_directory", "./templates/");
        chromeAdditionalOptions.put("download.prompt_for_download", false);
        chromeAdditionalOptions.put("download.directory_upgrade", true);
        return chromeAdditionalOptions;
    }

    /**
     * Generates Performance Desired Capabilities.
     *
     * @return Desired capabilities.
     */
    public static DesiredCapabilities generatePerformanceLoggingCapability() {
        DesiredCapabilities cap = new DesiredCapabilities();
        LoggingPreferences logPrefs = new LoggingPreferences();
        logPrefs.enable(LogType.BROWSER, Level.ALL);
        logPrefs.enable(LogType.PERFORMANCE, Level.ALL);
        cap.setCapability(CapabilityType.LOGGING_PREFS, logPrefs);
        return cap;
    }

    /**
     * Sets the session info.
     *
     * @param webDriver The web driver instance.
     * @param caps The Capabilities.
     */
    private void setSessionInfo(RemoteWebDriver webDriver, DesiredCapabilities caps) {
        sessionId.set(webDriver.getSessionId().toString());
        //sessionBrowser.set(caps.getBrowserName());
       // sessionVersion.set(caps.getBrowserVersion());
       // sessionPlatform.set(caps.getPlatformName().toString());

        Log.LOGGER.info("\n*** TEST ENVIRONMENT = "
                + getSessionBrowser().toUpperCase()
                + "/" + getSessionPlatform().toUpperCase()
                + "/Session ID=" + getSessionId() + "\n");
    }

    /**
     * getSessionId method to retrieve active id.
     *
     * @return the session id
     */
    private String getSessionId() {
        return sessionId.get();
    }

    /**
     * getSessionBrowser method to retrieve active browser.
     *
     * @return the session browser
     */
    private String getSessionBrowser() {
        return sessionBrowser.get();
    }

    /**
     * getSessionVersion method to retrieve active version.
     *
     * @return the session version
     */
    private String getSessionVersion() {
      return sessionVersion.get();
    }

    /**
     * getSessionPlatform method to retrieve active platform.
     *
     * @return the session platform
     */
    private String getSessionPlatform() {
        return sessionPlatform.get();
    }

    /**
     * gets the build number
     *
     * @return the session platform
     */
    private static String getBuildName()
    {
        String buildName = ApplicationProperties.JENKINS_BUILD_NUMBER.getStringVal();
        String buildNumber = System.getenv("BUILD_NUMBER");
        if(buildNumber != null)
        {
            buildName = buildName + "_" + buildNumber;
        }

        return buildName;
    }

    /**
     * Gets the custom chrome options
     * @return Hashmap of the custom firefox properties
     */
    private static HashMap<String, String> getCustomChromeOptionsFromPropertiesFile()
    {
        return DriverManager.getCustomOptionsFromPropertiesFile(System.getProperty("chromeCapabilities.properties.file","resources/chromeCapabilities.properties"));
    }

    /**
     *
     * @return Hashmap of the custom firefox properties
     */
    private static HashMap<String, String> getCustomFirefoxOptionsFromPropertiesFile()
    {
        return DriverManager.getCustomOptionsFromPropertiesFile(System.getProperty("firefoxCapabilities.properties.file","resources/firefoxCapabilities.properties"));
    }

    /**
     *
     * @return Hashmap of the custom ie properties
     */
    private static HashMap<String, String> getCustomIEOptionsFromPropertiesFile()
    {
        return DriverManager.getCustomOptionsFromPropertiesFile(System.getProperty("ieCapabilities.properties.file","resources/ieCapabilities.properties"));
    }

    /**
     * Gets the custom options from the file path and returns a hashmap
     * @param filePath the file path to the properties file
     * @return Hashmap of the properties
     */
    private static HashMap<String, String> getCustomOptionsFromPropertiesFile(String filePath)
    {
        HashMap<String, String> map = new HashMap();
        PropertyUtil optionsProperties = new PropertyUtil();
        optionsProperties.clear();
        optionsProperties.load(new String[]{filePath});
        Iterator<String> keys = optionsProperties.getKeys();

        while(keys.hasNext())
        {
            String key = keys.next();
            String value = optionsProperties.getPropertyValue(key);

            if(!value.isEmpty())
            {
                map.put(key,value);
            }
        }
        return map;
    }
}
