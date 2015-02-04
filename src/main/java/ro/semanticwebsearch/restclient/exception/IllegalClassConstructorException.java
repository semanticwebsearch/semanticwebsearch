package ro.semanticwebsearch.restclient.exception;

/**
 * Created by valentin.spac on 2/4/2015.
 */
public class IllegalClassConstructorException extends Exception {

    public IllegalClassConstructorException(String cause) {
        super(cause);
    }

    public IllegalClassConstructorException(){
        super();
    }
}
