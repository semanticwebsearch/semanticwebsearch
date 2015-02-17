package ro.semanticwebsearch.services.exception;

/**
 * Created by Spac on 17 Feb 2015.
 */
public class InvalidConfigurationFileException extends RuntimeException {

    public InvalidConfigurationFileException(String message) {
        super(message);
    }

    public InvalidConfigurationFileException(String message, Exception e) {
        super(message, e);
    }
}
