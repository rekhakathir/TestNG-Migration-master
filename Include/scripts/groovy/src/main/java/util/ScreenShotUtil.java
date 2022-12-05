package src.main.java.util;

import com.assertthat.selenium_shutterbug.core.Shutterbug;

import src.main.java.logger.Log;

import org.apache.commons.io.FileUtils;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.Augmenter;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.time.LocalDateTime;

/**
 * Utility class for screenshots.
 */
public final class ScreenShotUtil {

    /**
     * Private constructor.
     */
    private ScreenShotUtil() { }

    /**
     * Default folder path for screen shots.
     */
    static final String FOLDER_PATH = ApplicationProperties.SCREENSHOT_DIR.getStringVal();

    /**
     * Creates a screenshot of the viewport.
     * @param driver the web driver
     * @param screenShotName the name for the screenshot
     * @return the path to the captured screenshot
     */
    public static String captureScreenShot(WebDriver driver, String screenShotName) {

        File source = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        String dest = FOLDER_PATH + "/" + screenShotName + ".png";
        File sshot = new File(dest);

        try {
            dest = sshot.getCanonicalPath();
            FileUtils.copyFile(source, sshot);
            Log.LOGGER.info("Saved fullpage screenshot at: " + dest);
        } catch (IOException e) {
            Log.LOGGER.error("Unable to create screenshot Exception = " + e.getMessage() + e.getStackTrace());
        }

        return dest;
    }

    /**
     * Captures a base64 screenshot of the displayed page viewport.
     * @param driver The web driver
     * @param screenShotName the screenshot name
     * @return the path to the captured screenshot
     */
    public static String captureBase64ScreenShot(WebDriver driver, String screenShotName) {
        WebDriver d;
        try {
            if (driver instanceof org.openqa.selenium.remote.RemoteWebDriver) {
                d = new Augmenter().augment(driver);
            } else {
                d = driver;
            }
            String base64Image = ((TakesScreenshot) d).getScreenshotAs(OutputType.BASE64);

            return base64ImageToFile(base64Image, screenShotName);

        } catch (Exception e) {
            Log.LOGGER.error(MessageFormat.format("Could not capture screenshot for screenShotName. Exception {0}", e.getMessage()));
        }
        return null;
    }

    /**
     * Saves the web element image into a byte array.
     * @param driver the web driver
     * @param element the web element
     * @return an bytearray of an image of the element
     */
    public static byte[] saveWebElement(WebDriver driver, WebElement element) {
        try {
            BufferedImage image = Shutterbug
                    .shootElement(driver, element)
                    .withName(element.getText())
                    .withTitle(element.getText())
                    .getImage();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "png", baos);
            baos.flush();
            byte[] imageInByte = baos.toByteArray();
            baos.close();
            return imageInByte;
        } catch (IOException e) {
            Log.LOGGER.info("IOException creating screenshot. " + e.getMessage() + e.getStackTrace());
        }
        return "Unable to Get WebElement.".getBytes();
    }

    /**
     * Generates a unique screenshot name based on the local date.
     * @param screenShotName The screenshot name to append to the dateTime
     * @return Returns a unique screenshot name
     */
    public static String generateScreenshotFileName(String screenShotName) {
        screenShotName = screenShotName.replaceAll(" ", "_");
        screenShotName = screenShotName.replaceAll("[\\\\/:*?\"<>|]", "");
        String localDateTime = LocalDateTime.now().toString().replaceAll("[^0-9a-zA-Z]", "");
        StringBuilder name = new StringBuilder()
                .append(screenShotName)
                .append("_")
                .append(localDateTime);
        return name.toString();
    }

    /**
     * Saves the base64 image to a file.
     * @param base64Image The base64 image string
     * @param screenShotName the screenshot name
     * @return the path to the screenshot
     */
    private static String base64ImageToFile(String base64Image, String screenShotName) {
        String filePath = "";
        try {
            FileUtil.checkCreateDir(FOLDER_PATH);
            filePath = FileUtil.saveImageFile(base64Image, screenShotName, FOLDER_PATH);

            Log.LOGGER.debug("Capturing screen shot: " + filePath);

        } catch (Exception e) {
            Log.LOGGER.error("Error in capturing screenshot\n" + e.getMessage());
        }
        return filePath;
    }
}
