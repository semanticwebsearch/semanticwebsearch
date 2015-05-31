package ro.semanticwebsearch.responsegenerator.parser;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.apache.log4j.Logger;
import ro.semanticwebsearch.responsegenerator.model.Person;
import ro.semanticwebsearch.responsegenerator.parser.helper.DBPediaPropertyExtractor;
import ro.semanticwebsearch.responsegenerator.parser.helper.FreebasePropertyExtractor;
import ro.semanticwebsearch.responsegenerator.parser.helper.MetadataProperties;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Spac on 4/12/2015.
 */
class ChildrenOfParser implements ParserType {
    private static Logger log = Logger.getLogger(ChildrenOfParser.class.getCanonicalName());

    public ChildrenOfParser() {
        System.out.println("constructor who are children of");
    }

    @Override
    public ArrayList<Person> parseFreebaseResponse(String freebaseResponse, String questionId) {
        Map<String, String> freebaseChildrenInfo = new HashMap<>();
        parseFreebaseForUriAndName(freebaseResponse, freebaseChildrenInfo);

        ArrayList<Person> freebasePersons = new ArrayList<>();
        PersonParser personParser = new PersonParser();

        for (Map.Entry<String, String> child : freebaseChildrenInfo.entrySet()) {
            try {
                freebasePersons.add(personParser.freebaseWhoIs(new URI(child.getValue())));
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }

        return freebasePersons;
    }

    @Override
    public ArrayList<Person> parseDBPediaResponse(String dbpediaResponse, String questionId) {
        Map<String, String> dbpediaChildrenInfo = new HashMap<>();
        parseDBPediaForUriAndName(dbpediaResponse, dbpediaChildrenInfo);

        ArrayList<Person> dbpediaPersons = new ArrayList<>();
        PersonParser personParser = new PersonParser();
        for (Map.Entry<String, String> child : dbpediaChildrenInfo.entrySet()) {
            try {
                dbpediaPersons.add(personParser.dbpediaWhoIs(new URI(child.getValue())));
            } catch (URISyntaxException e) {
                e.printStackTrace();
                ro.semanticwebsearch.responsegenerator.model.Person person = new Person();
                person.setName(child.getValue());
                dbpediaPersons.add(person);
            }
        }

        return dbpediaPersons;
    }

    /**
     * Iterates through freebase response, searches for /people/person/children tag and extracts the valued from there.
     * The data is used to populate the @param freebaseChildUri, (key, value) = (childName, freebaseUri)
     */
    private void parseFreebaseForUriAndName(String freebase, Map<String, String> freebaseChildUri) {
        JsonNode freebaseResponse;
        try {
            ObjectMapper mapper = new ObjectMapper();
            freebaseResponse = mapper.readTree(freebase);
            JsonNode bindings = freebaseResponse.get("result");

            if (bindings.isArray()) {
                ArrayNode results = (ArrayNode) bindings;

                //iterates through object in bindings array
                for (JsonNode node : results) {
                    System.out.println(node.toString());
                    JsonNode children = node.get(MetadataProperties.CHILDREN.getFreebase());
                    if (children.isArray()) {
                        for (JsonNode child : children) {
                            freebaseChildUri.put(DBPediaPropertyExtractor.extractValue(child.get("name")),
                                    FreebasePropertyExtractor.getFreebaseLink(DBPediaPropertyExtractor.extractValue(child.get("id"))));
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
     *
     * @param dbpedia      string to pe parsed (dbpedia response)
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
            JsonNode bindings = dbpediaResponse.get("results").get("bindings");

            if (bindings.isArray()) {
                JsonNode aux;
                String uri, personName;
                String[] auxArray;

                //iterates through object in bindings array
                for (JsonNode node : bindings) {
                    //elements from every object (x0,x1..) these are properties
                    aux = node.get("x1");
                    if (aux != null) {
                        //TODO another approach is to GET link, get foaf:name property and add it here
                        if (aux.get("type").toString().equals("\"uri\"")) {
                            uri = DBPediaPropertyExtractor.extractValue(aux.get("value"));
                            auxArray = uri.split("/");
                            personName = auxArray[auxArray.length - 1].replace("_", " ");
                            childrenName.put(personName, DBPediaPropertyExtractor.extractValue(aux.get("value")));
                        }

                        if (aux.get("type").toString().equals("\"literal\"")) {
                            childrenName.put(DBPediaPropertyExtractor.extractValue(aux.get("value")),
                                    DBPediaPropertyExtractor.extractValue(aux.get("value")));
                            // System.out.println(extractValue(aux.get("value")));
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
