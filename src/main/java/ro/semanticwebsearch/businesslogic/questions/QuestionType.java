package ro.semanticwebsearch.businesslogic.questions;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;

/**
 * Created by Spac on 4/8/2015.
 */
public interface QuestionType {
    void doSomethingUseful(String query) throws UnsupportedEncodingException, URISyntaxException, InstantiationException, IllegalAccessException;

}
