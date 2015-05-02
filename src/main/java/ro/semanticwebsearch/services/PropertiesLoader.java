package ro.semanticwebsearch.services;

import org.apache.log4j.Logger;
import ro.semanticwebsearch.services.exception.ConfigurationFileNotFoundException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by Spac on 17 Feb 2015.
 */
class PropertiesLoader {

    private static Logger log = Logger.getLogger(PropertiesLoader.class.getCanonicalName());
    private static PropertiesLoader instance;
    private Properties properties;

    private PropertiesLoader() throws IOException {
        load();
    }

    public static PropertiesLoader getInstance() throws IOException {
        if (instance == null) {
            instance = new PropertiesLoader();
        }

        return instance;
    }

    private void load() throws IOException {
        Properties properties = new Properties();
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream stream = classLoader.getResourceAsStream("services.properties");

        if (stream != null) {
            if (log.isInfoEnabled()) {
                log.info("Loading services properties");
            }

            properties.load(stream);
        } else {
            if (log.isInfoEnabled()) {
                log.info("File services.properties not found in classpath");
            }
            throw new ConfigurationFileNotFoundException("File [services.properties] not found in classpath");
        }

        this.properties = properties;
    }


    public Properties getProperties() {
        return properties;
    }

}
