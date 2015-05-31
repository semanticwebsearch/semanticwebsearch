package ro.semanticwebsearch.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by Spac on 5/30/2015.
 */
public class Config {

    private static Properties properties;

    static {
        properties = new Properties();
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream stream2 = classLoader.getResourceAsStream("services.properties");
        try {
            properties.load(stream2);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getProperty(String property) {
        if(properties != null) {
            return properties.getProperty(property);
        }

        return null;
    }
}
