package ro.semanticwebsearch.responsegenerator.parser;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.apache.log4j.Logger;
import ro.semanticwebsearch.businesslogic.ServiceResponse;
import ro.semanticwebsearch.responsegenerator.model.Person;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Spac on 4/23/2015.
 */
class WhereIs implements ParserType {

    private static final String QUESTION = "Where is";
    private static Logger log = Logger.getLogger(WhereIs.class.getCanonicalName());


    @Override
    public Map<String, Object> doSomethingUseful(ServiceResponse response)
            throws UnsupportedEncodingException, URISyntaxException, InstantiationException, IllegalAccessException {
        if (log.isInfoEnabled()) {
            log.info(QUESTION + " : " + response);
        }

        String dbpediaResponse = response.getDbpediaResponse();
        Person dbpediaPerson = parseDBPediaResponse(dbpediaResponse);

        String freebaseResponse = response.getFreebaseResponse();
        Person freebasePerson = parseFreebaseResponse(freebaseResponse);
        Map<String, Object> map = new HashMap<>();
        map.put("freebase", freebasePerson);
        map.put("dbpedia", dbpediaPerson);
        return map;
    }

    private Person parseFreebaseResponse(String freebaseResponse) {

        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode response = mapper.readTree(freebaseResponse).findValue("result");
            if (response.isArray()) {
                ArrayNode bindingsArray = (ArrayNode) response;
                JsonNode aux;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private Person parseDBPediaResponse(String dbpediaResponse) {

        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode response = mapper.readTree(dbpediaResponse).findValue("results").findValue("bindings");
            if (response.isArray()) {
                ArrayNode bindingsArray = (ArrayNode) response;
                JsonNode aux;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
