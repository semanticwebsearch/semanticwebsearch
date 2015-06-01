package ro.semanticwebsearch.responsegenerator.parser;

import ro.semanticwebsearch.responsegenerator.model.Answer;

import java.util.List;

/**
 * Created by Spac on 4/8/2015.
 */
public interface ParserType {
   /* Map<String, Object> doSomethingUseful(ServiceResponse response)
            throws UnsupportedEncodingException, URISyntaxException, InstantiationException, IllegalAccessException;
*/
    Object parseFreebaseResponse(String freebaseResponse, String questionId);

    List<Answer> parseDBPediaResponse(String dbpediaResponse, String questionId);

}
