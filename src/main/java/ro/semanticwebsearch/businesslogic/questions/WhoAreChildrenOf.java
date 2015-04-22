package ro.semanticwebsearch.businesslogic.questions;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.log4j.Logger;
import ro.semanticwebsearch.businesslogic.ServiceResponse;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.*;

/**
 * Created by Spac on 4/12/2015.
 */
public class WhoAreChildrenOf implements QuestionType {
    private static final String QUESTION = "Who are the children of";
    private static final String FREEBASE_RESOURCE_LINK = "https://www.googleapis.com/freebase/v1/topic%s?filter=all";
    private static Logger log = Logger.getLogger(WhoAreChildrenOf.class.getCanonicalName());

    public WhoAreChildrenOf() {
        System.out.println("constructor who are children of");
    }

    @Override
    public Map<String, Object> doSomethingUseful(ServiceResponse response)
            throws UnsupportedEncodingException, URISyntaxException, InstantiationException, IllegalAccessException {

        if (log.isInfoEnabled()) {
            log.info(QUESTION + " : " + response);
        }

        //dbpedia response parsing
        String dbpedia = response.getDbpediaResponse();
        HashSet<String> dbpediaUris = new HashSet<>();
        Map<String, String> dbpediaChildrenInfo = new HashMap<>();
        parseDBPediaForUriAndName(dbpedia, dbpediaUris, dbpediaChildrenInfo);

        //freebase response parsing
        String freebase = response.getFreebaseResponse();
        Map<String, String> freebaseChildUri = new HashMap<>();
        Map<String, JsonNode> freebaseChildrenInfo = new HashMap<>();
        parseFreebaseForUriAndName(freebase, freebaseChildUri);

        //region WHOIS calls ( not used anymore)

       /* String infoQ;
        QuepyResponse qr;
        String additionalInfos;
        ArrayList<ServiceResponse> additionalResponses = new ArrayList<>();
        ServiceResponse auxResponse;

        for(String child : dbpediaChildrenName) {
            infoQ = String.format(AdditionalQuestions.WHOIS.toString(), child);
            qr = Dispatcher.queryQuepy(QueryType.SPARQL, infoQ);

            if(qr.getRule() != null) {
                auxResponse = new ServiceResponse();
                additionalInfos = Dispatcher.queryService(Dispatcher.DBPEDIA, qr.getQuery());
                auxResponse.setDbpediaResponse(additionalInfos);
                qr = Dispatcher.queryQuepy(QueryType.MQL, infoQ);

                additionalInfos = Dispatcher.queryService(Dispatcher.FREEBASE, qr.getQuery());
                auxResponse.setFreebaseResponse(additionalInfos);

            //add ?filter=all
                additionalResponses.add(auxResponse);
            }
        }
        JsonNode whoIsResponse;
        for(ServiceResponse resp : additionalResponses) {
            System.out.println(resp.getDbpediaResponse());
            try {
                whoIsResponse = mapper.readTree(resp.getDbpediaResponse());
                dbpediaUris.addAll(extractDBpediaUris(whoIsResponse));
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println(resp.getFreebaseResponse());
        }

        System.out.println(dbpediaUris);*/

        //endregion

        //get for uris
        //region DBPEDIA get for children info
        WebTarget client;
        String additionalInfo, childName = null;
        JsonNode aux, mainInfos;
        ObjectNode properties;
        ObjectMapper mapper = new ObjectMapper();
        Iterator<JsonNode> it;


        for(String dbpediaUri : dbpediaUris) {
            client = ClientBuilder.newClient().target(dbpediaUri);
            additionalInfo = client.request().get(String.class);

            try {
                aux = mapper.readTree(additionalInfo);
                additionalInfo = convertResourceUrlToDBPediaUrl(dbpediaUri);
                mainInfos = aux.findValue(additionalInfo);
                //TODO check if it is okay to get label as name
                for(JsonNode node : mainInfos.findValue("http://www.w3.org/2000/01/rdf-schema#label")) {
                    if(node.findValue("lang") != null
                            && ( "\"en\"".equals(node.findValue("lang").toString())
                                || "\"it\"".equals(node.findValue("lang").toString()))) {
                        childName = node.findValue("value").toString().replace("\"", "");
                    }
                }

                ((ObjectNode) aux).remove(additionalInfo);
                ((ObjectNode) aux).set("mainInfo", mainInfos);

                it = aux.iterator();
                while(it.hasNext()) {
                    properties = (ObjectNode)it.next();
                    properties.remove(uselessProperties);
                    if(properties.size() == 0) {
                        it.remove();
                    }

                }

                if(childName != null) {
                    dbpediaChildrenInfo.put(childName, aux.toString());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //endregion

        for(Map.Entry<String, String> child : freebaseChildUri.entrySet()) {
            client = ClientBuilder.newClient().target(child.getValue());
            additionalInfo = client.request().get(String.class);
            try {
                aux = mapper.readTree(additionalInfo);
                freebaseChildrenInfo.put(child.getKey(), aux);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return prepareResponse(dbpediaChildrenInfo, freebaseChildrenInfo);


    }

    /**
     * Returns a map containing the @param dbpediaChildrenInfo and @param freebaseChildrenInfo in a format like :
     *  dbpedia/freebase -> {name : childrenName, additionalInfo : the value from map}
     */
    private Map<String, Object> prepareResponse(Map<String, String> dbpediaChildrenInfo, Map<String, JsonNode> freebaseChildrenInfo) {
        Map<String, Object> results = new HashMap<>();
        ObjectNode aux;
        ObjectMapper mapper = new ObjectMapper();
        ArrayNode auxArray = new ArrayNode(JsonNodeFactory.instance);

        for(Map.Entry<String, String> child : dbpediaChildrenInfo.entrySet()) {
            aux = JsonNodeFactory.instance.objectNode();
            aux.put("name", child.getKey());
            try {
                aux.put("additionalInfo" , mapper.readTree(child.getValue()));
            } catch (IOException e) {
                e.printStackTrace();
            }
            auxArray.add(aux);
        }

        results.put("dbpedia", auxArray);
        auxArray = new ArrayNode(JsonNodeFactory.instance);
        for(Map.Entry<String, JsonNode> child : freebaseChildrenInfo.entrySet()) {
            aux = JsonNodeFactory.instance.objectNode();
            aux.put("name", child.getKey());
            aux.put("additionalInfo" , child.getValue());
            auxArray.add(aux);
        }

        results.put("freebase", auxArray);

        return results;
    }

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
                            freebaseChildUri.put(extractValue(child.findValue("name")),
                                    getFreebaseLink(extractValue(child.findValue("id"))));
                        }
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

   /* private void parseFreebaseForUriAndName(String freebase, ObjectMapper mapper,
                                            HashSet<String> freebaseUris, HashSet<String> freebaseChildrenName) {
        System.out.println(freebase);
        JsonNode freebaseResponse = null;
        try {
            freebaseResponse = mapper.readTree(freebase);
            JsonNode bindings = freebaseResponse.findValue("result");

            if(bindings.isArray()) {
                ArrayNode results = (ArrayNode) bindings;
                Iterator<JsonNode> it;
                JsonNode aux;

                //iterates through object in bindings array
                for (JsonNode node : results) {
                    System.out.println(node.toString());
                    JsonNode children = node.findValue("/people/person/children");
                    if(children.isArray()) {
                        for(JsonNode child : children) {
                            freebaseUris.add(getFreebaseLink(extractValue(child.findValue("id"))));
                            freebaseChildrenName.add(extractValue(child.findValue("name")));
                        }
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }


    }*/

    /**
     * Parses the response from dbpedia. Iterates through properties of "bindings", extracts the value of uris and saves
     * them in @param uris; if uri does not exists, extracts the literal (child name) and saves it in @param childrenName
     * @param dbpedia string to pe parsed (dbpedia response)
     * @param uris
     * @param childrenName
     */
    private void parseDBPediaForUriAndName(String dbpedia, HashSet<String> uris,
                                           Map<String, String> childrenName) {
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
                ArrayNode bindingsArray = (ArrayNode)bindings;
                Iterator<JsonNode> it;
                JsonNode aux;

                //iterates through object in bindings array
                for(JsonNode node : bindingsArray) {
                    //elements from every object (x0,x1..) these are properties
                    it = node.elements();
                    while(it.hasNext()) {
                        aux = it.next();
                        if(aux.findValue("type").toString().equals("\"uri\"")) {
                            uris.add(convertDBPediaUrlToResourceUrl(extractValue(aux.findValue("value"))));
                           // System.out.println(extractValue(aux.findValue("value")));
                        }

                        if(aux.findValue("type").toString().equals("\"literal\"")) {
                            childrenName.put(extractValue(aux.findValue("value")), extractValue(aux.findValue("value")));
                           // System.out.println(extractValue(aux.findValue("value")));
                        }

                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Given the json node, extracts the value and removes the "
     */
    private String extractValue(JsonNode node) {
        return node.toString().replace("\"", "");
    }


    private String getFreebaseLink(String resource) {
        return String.format(FREEBASE_RESOURCE_LINK, resource);
    }

    /**
     * Given the resource url found in dbpedia response, converts it in a resource url by replaces /page/ or /resource/ with /data/
     */
    private String convertDBPediaUrlToResourceUrl(String dbpediaURL) {
        StringBuilder builder = null;
        if(dbpediaURL.contains("/page/")){
            builder = new StringBuilder(dbpediaURL.replace("/page/",
                    "/data/"));
        }else{
            builder = new StringBuilder(dbpediaURL.replace("/resource/",
                    "/data/"));
        }

        builder.append(".json");
        return builder.toString();
    }

    /**
     * Dual of {@code}convertDBPediaUrlToResourceUrl
     */
    private String convertResourceUrlToDBPediaUrl(String dbpediaURL) {
        return dbpediaURL.replace("/data/", "/resource/").replace(".json", "");
    }

    //properties with no interest to us
    private static final ArrayList<String> uselessProperties = new ArrayList<>();

    static {
        uselessProperties.add("http://dbpedia.org/ontology/wikiPageRedirects");
        uselessProperties.add("http://www.w3.org/2002/07/owl#sameAs");
    }
}
