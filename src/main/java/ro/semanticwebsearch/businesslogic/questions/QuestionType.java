package ro.semanticwebsearch.businesslogic.questions;

import com.fasterxml.jackson.databind.JsonNode;
import ro.semanticwebsearch.businesslogic.ServiceResponse;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.Map;

/**
 * Created by Spac on 4/8/2015.
 */
public interface QuestionType {
    Map<String, JsonNode> doSomethingUseful(ServiceResponse response)
            throws UnsupportedEncodingException, URISyntaxException, InstantiationException, IllegalAccessException;

}
