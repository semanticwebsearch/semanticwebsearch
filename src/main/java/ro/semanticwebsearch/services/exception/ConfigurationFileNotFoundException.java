package ro.semanticwebsearch.services.exception;

import java.io.IOException;

/**
 * Created by Spac on 17 Feb 2015.
 */
public class ConfigurationFileNotFoundException extends RuntimeException {

    public ConfigurationFileNotFoundException(String message) {
        super(message);
    }

    public ConfigurationFileNotFoundException(String message, IOException e) {
        super(message, e);
    }
}
