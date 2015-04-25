package ro.semanticwebsearch.responsegenerator;

import ro.semanticwebsearch.businesslogic.ServiceResponse;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.Map;

/**
 * Created by Spac on 4/8/2015.
 */
public interface QuestionType {
    Map<String, Object> doSomethingUseful(ServiceResponse response)
            throws UnsupportedEncodingException, URISyntaxException, InstantiationException, IllegalAccessException;

}
