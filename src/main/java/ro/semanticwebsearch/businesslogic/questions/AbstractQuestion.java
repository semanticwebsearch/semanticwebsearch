package ro.semanticwebsearch.businesslogic.questions;

import com.fasterxml.jackson.databind.JsonNode;
import ro.semanticwebsearch.businesslogic.ServiceResponse;

import java.util.Map;

/**
 * Created by Spac on 4/8/2015.
 */
public abstract class AbstractQuestion implements QuestionType {

    public abstract Map<String, JsonNode> doSomethingUseful(ServiceResponse response);
}
