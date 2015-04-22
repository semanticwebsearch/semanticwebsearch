package ro.semanticwebsearch.businesslogic.questions;

import ro.semanticwebsearch.businesslogic.ServiceResponse;

import java.util.Map;

/**
 * Created by Spac on 4/8/2015.
 */
public abstract class AbstractQuestion implements QuestionType {

    public abstract Map<String, Object> doSomethingUseful(ServiceResponse response);
}
