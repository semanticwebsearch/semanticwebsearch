package ro.semanticwebsearch.persistence.exception;

/**
 * Created by valentin.spac on 2/13/2015.
 */
public class HibernateInitializeException extends RuntimeException {

    public HibernateInitializeException(String message, Exception e) {
        super(message, e);
    }

}
