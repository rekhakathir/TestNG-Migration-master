package src.main.java.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class LoadEnvironmentProperties {

    Properties prop;
    FileInputStream input = null;

    public LoadEnvironmentProperties() {
        try {
            File source = new File("resources/environment.properties");

            FileInputStream input = new FileInputStream(source);

            prop = new Properties();

            prop.load(input);

        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }
    public String getProperty(String key)
    {
        return prop.getProperty(key);
    }
}
