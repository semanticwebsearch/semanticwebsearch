package ro.semanticwebsearch.responsegenerator;

import org.apache.log4j.Logger;
import ro.semanticwebsearch.businesslogic.ServiceResponse;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Spac on 4/26/2015.
 */
public abstract class AbstractQuestionType implements QuestionType {

    private static Logger log = Logger.getLogger(AbstractQuestionType.class.getCanonicalName());

    @Override
    public Map<String, Object> doSomethingUseful(ServiceResponse response)
            throws UnsupportedEncodingException, URISyntaxException, InstantiationException, IllegalAccessException {
        if (log.isInfoEnabled()) {
            log.info(response);
        }

        String dbpediaResponse = response.getDbpediaResponse();
        Object dbpediaPerson = parseDBPediaResponse(dbpediaResponse);

        String freebaseResponse = response.getFreebaseResponse();
        Object freebasePerson = parseFreebaseResponse(freebaseResponse);

        Map<String, Object> map = new HashMap<>();
        map.put("freebase", freebasePerson);
        map.put("dbpedia", dbpediaPerson);
        return map;
    }

    public abstract Object parseFreebaseResponse(String freebaseResponse);

    public abstract Object parseDBPediaResponse(String dbpediaResponse);
}
