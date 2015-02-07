package ro.semanticwebsearch;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ro.semanticwebsearch.restclient.RestClient;
import ro.semanticwebsearch.restclient.RestClientFactory;

/**
 * Created by Spac on 07 Feb 2015.
 */
public class Main {
    public static void main(String[] args)
            throws IllegalAccessException, InstantiationException {
        RestClient freebase = RestClientFactory.getInstanceFor("DBPedia");
        String set = freebase.query("" +
                "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>" +
                "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" +
                "PREFIX foaf: <http://xmlns.com/foaf/0.1/>" +
                "SELECT DISTINCT ?x1  WHERE {\n" +
                "    ?x0 rdf:type foaf:Person.\n" +
                "    ?x0 rdfs:label \"Tom Cruise\"@en.\n" +
                "    ?x0 rdfs:comment ?x1.\n" +
                "}");

        ObjectMapper mapper = new ObjectMapper();
        try {
            System.out.println(mapper.writeValueAsString(set));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        RestClient db = RestClientFactory.getInstanceFor("Freebase");
        String set2 = db.query("[{\n" +
                "  \"type\": \"/type/property\",\n" +
                "  \"schema\": {\n" +
                "    \"id\": \"/music/artist\"\n" +
                "  },\n" +
                "  \"id\": null,\n" +
                "  \"name\": null\n" +
                "}]");
        ObjectMapper mapper2 = new ObjectMapper();
        try {
            System.out.println(mapper2.writeValueAsString(set2));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

    }
}
