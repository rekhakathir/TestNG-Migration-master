package src.main.java.baseClasses;


import org.testng.ITestContext;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeSuite;

import src.main.java.logger.Log;
import src.main.java.util.EnvironmentSetup;

public interface BaseTestCase {

    /**
     * Sets the environment properties.
     */
    @BeforeSuite
    default void beforeSuite() {
        EnvironmentSetup.environmentSetup();
    }

    /**
     * Before class method to log the class name.
     *
     * @param context The test context
     */
    @BeforeClass
    default void beforeClass(ITestContext context) {
        Log.LOGGER.info("Running the test class: "
                + this.getClass().getCanonicalName());
    }

    /**
     * Method executes after each test. Removes the driver.
     */
    @AfterTest()
    default void afterTest() {
        Log.LOGGER.info("Finished Test Method");
    }
}
