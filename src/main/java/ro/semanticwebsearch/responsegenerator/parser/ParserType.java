package ro.semanticwebsearch.responsegenerator.parser;

/**
 * Created by Spac on 4/8/2015.
 */
public interface ParserType {
   /* Map<String, Object> doSomethingUseful(ServiceResponse response)
            throws UnsupportedEncodingException, URISyntaxException, InstantiationException, IllegalAccessException;
*/
    Object parseFreebaseResponse(String freebaseResponse, String questionId);

    Object parseDBPediaResponse(String dbpediaResponse, String questionId);

}
