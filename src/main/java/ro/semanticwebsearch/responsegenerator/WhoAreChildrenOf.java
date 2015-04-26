package ro.semanticwebsearch.responsegenerator;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.apache.log4j.Logger;
import ro.semanticwebsearch.responsegenerator.model.Person;
import ro.semanticwebsearch.responsegenerator.parser.DBPediaParser;
import ro.semanticwebsearch.responsegenerator.parser.FreebaseParser;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Spac on 4/12/2015.
 */
class WhoAreChildrenOf extends AbstractQuestionType {
    private static final String QUESTION = "Who are the children of";
    private static final String FREEBASE_RESOURCE_LINK = "https://www.googleapis.com/freebase/v1/topic%s?filter=all";
    private static Logger log = Logger.getLogger(WhoAreChildrenOf.class.getCanonicalName());

    public WhoAreChildrenOf() {
        System.out.println("constructor who are children of");
    }

   /* @Override
    public Map<String, Object> doSomethingUseful(ServiceResponse response)
            throws UnsupportedEncodingException, URISyntaxException, InstantiationException, IllegalAccessException {

        if (log.isInfoEnabled()) {
            log.info(QUESTION + " : " + response);
        }

        //dbpedia response parsing
        String dbpedia = response.getDbpediaResponse();
        Map<String, String> dbpediaChildrenInfo = new HashMap<>();
        parseDBPediaForUriAndName(dbpedia, dbpediaChildrenInfo);
        //fac get pe uris, la dbpedia, fac serviceResponse

        //freebase response parsing
        String freebase = response.getFreebaseResponse();
        Map<String, String> freebaseChildrenInfo = new HashMap<>();
        parseFreebaseForUriAndName(freebase, freebaseChildrenInfo);

        return prepareResponse(dbpediaChildrenInfo, freebaseChildrenInfo);


    }
*/

    @Override
    public Object parseFreebaseResponse(String freebaseResponse) {
        Map<String, String> freebaseChildrenInfo = new HashMap<>();
        parseFreebaseForUriAndName(freebaseResponse, freebaseChildrenInfo);

        ArrayList<Person> freebasePersons = new ArrayList<>();
        WhoIs whoIs = new WhoIs();

        for(Map.Entry<String, String> child : freebaseChildrenInfo.entrySet()) {
            try {
                freebasePersons.add(whoIs.freebaseWhoIs(new URI(child.getValue())));
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }

        return freebasePersons;
    }

    @Override
    public Object parseDBPediaResponse(String dbpediaResponse) {
        Map<String, String> dbpediaChildrenInfo = new HashMap<>();
        parseDBPediaForUriAndName(dbpediaResponse, dbpediaChildrenInfo);

        ArrayList<Person> dbpediaPersons = new ArrayList<>();
        WhoIs whoIs = new WhoIs();

        for(Map.Entry<String, String> child : dbpediaChildrenInfo.entrySet()) {
            try {
                dbpediaPersons.add(whoIs.dbpediaWhoIs(new URI(child.getValue())));
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }

        return dbpediaPersons;
    }

  /*  *//**
     * Returns a map containing the @param dbpediaChildrenInfo and @param freebaseChildrenInfo in a format like :
     *  dbpedia/freebase -> {name : childrenName, additionalInfo : the value from map}
     *//*
    private Map<String, Object> prepareResponse(Map<String, String> dbpediaChildrenInfo, Map<String, String> freebaseChildrenInfo) {
        Map<String, Object> results = new HashMap<>();
        ArrayList<Person> dbpediaResponse = new ArrayList<>();
        WhoIs whoIs = new WhoIs();

        for(Map.Entry<String, String> child : dbpediaChildrenInfo.entrySet()) {
            try {
                dbpediaResponse.add(whoIs.dbpediaWhoIs(new URI(child.getValue())));
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }

        results.put("dbpedia", dbpediaResponse);

        for(Map.Entry<String, String> child : freebaseChildrenInfo.entrySet()) {
            try {
                freebaseResponse.add(whoIs.freebaseWhoIs(new URI(child.getValue())));
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }

        results.put("freebase", freebaseResponse);

        return results;
    }*/

    /**
     * Iterates through freebase response, searches for /people/person/children tag and extracts the valued from there.
     * The data is used to populate the @param freebaseChildUri, (key, value) = (childName, freebaseUri)
     */
    private void parseFreebaseForUriAndName(String freebase,  Map<String, String> freebaseChildUri) {
        JsonNode freebaseResponse;
        try {
            ObjectMapper mapper = new ObjectMapper();
            freebaseResponse = mapper.readTree(freebase);
            JsonNode bindings = freebaseResponse.findValue("result");

            if(bindings.isArray()) {
                ArrayNode results = (ArrayNode) bindings;

                //iterates through object in bindings array
                for (JsonNode node : results) {
                    System.out.println(node.toString());
                    JsonNode children = node.findValue("/people/person/children");
                    if(children.isArray()) {
                        for(JsonNode child : children) {
                            freebaseChildUri.put(DBPediaParser.extractValue(child.findValue("name")),
                                    FreebaseParser.getFreebaseLink(DBPediaParser.extractValue(child.findValue("id"))));
                        }
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * Parses the response from dbpedia. Iterates through properties of "bindings", extracts the value of uris and saves
     * them in @param uris; if uri does not exists, extracts the literal (child name) and saves it in @param childrenName
     * @param dbpedia string to pe parsed (dbpedia response)
     * @param childrenName
     */
    private void parseDBPediaForUriAndName(String dbpedia, Map<String, String> childrenName) {
        try {
            /*
            * "results": {
                "bindings": [
                   {
                    "x0": { "type": "uri" , "value": "http://dbpedia.org/resource/Steve_Jobs" } ,
                    "x1": { "type": "literal" , "xml:lang": "en" , "value": "Erin Jobs" }
                  }]
            * */
            ObjectMapper mapper = new ObjectMapper();
            JsonNode dbpediaResponse = mapper.readTree(dbpedia);
            JsonNode bindings = dbpediaResponse.findValue("results").findValue("bindings");

            if(bindings.isArray()) {
                JsonNode aux;
                String uri, personName;
                String[] auxArray;

                //iterates through object in bindings array
                for(JsonNode node : bindings) {
                    //elements from every object (x0,x1..) these are properties
                    aux = node.findValue("x1");
                    if(aux != null) {
                        //TODO another approach is to GET link, get foaf:name property and add it here
                        if(aux.findValue("type").toString().equals("\"uri\"")) {
                            uri = DBPediaParser.extractValue(aux.findValue("value"));
                            auxArray = uri.split("/");
                            personName = auxArray[auxArray.length - 1].replace("_", " ");
                            childrenName.put(personName, DBPediaParser.extractValue(aux.findValue("value")));
                        }

                        if(aux.findValue("type").toString().equals("\"literal\"")) {
                            childrenName.put(DBPediaParser.extractValue(aux.findValue("value")),
                                    DBPediaParser.extractValue(aux.findValue("value")));
                           // System.out.println(extractValue(aux.findValue("value")));
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
