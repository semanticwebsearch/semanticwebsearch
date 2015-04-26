package ro.semanticwebsearch.responsegenerator;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.Logger;
import ro.semanticwebsearch.businesslogic.ServiceResponse;
import ro.semanticwebsearch.responsegenerator.model.Person;
import ro.semanticwebsearch.responsegenerator.parser.DBPediaParser;
import ro.semanticwebsearch.responsegenerator.parser.FreebaseParser;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Spac on 4/18/2015.
 */
class WhoIs implements QuestionType {

    private static final String QUESTION = "Who is";
    private static Logger log = Logger.getLogger(WhoIs.class.getCanonicalName());

    public WhoIs() {
        System.out.println("constructor WhoIs");
    }

    @Override
    public Map<String, Object> doSomethingUseful(ServiceResponse response)
            throws UnsupportedEncodingException, URISyntaxException, InstantiationException, IllegalAccessException {

        if (log.isInfoEnabled()) {
            log.info(QUESTION + " : " + response);
        }

        //dbpedia
        String dbpediaResponse = response.getDbpediaResponse();
        String extractedUri = "";

        //extract uri from dbpedia response
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode responseNode = mapper.readTree(dbpediaResponse);

            if(responseNode.has("results")) {
                responseNode = responseNode.findValue("results").findValue("bindings");
            }

            if(responseNode.isArray()) {
                JsonNode aux;

                //iterates through object in bindings array
                for (JsonNode node : responseNode) {
                    //elements from every object (x0,x1..) these are properties
                    aux = node.findValue("x0");
                    if(aux != null && aux.findValue("type").toString().equals("\"uri\"")) {
                        extractedUri = DBPediaParser.extractValue(aux.findValue("value"));
                        break;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Person dbpediaPerson = null;
        if(extractedUri != null && !extractedUri.trim().isEmpty()) {
            dbpediaPerson = dbpediaWhoIs(new URI(extractedUri));
        }

        //freebase
        extractedUri = null;
        String freebaseResponse = response.getFreebaseResponse();
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode results = mapper.readTree(freebaseResponse).findValue("result");
            if (results.isArray()) {
                for (JsonNode item : results) {
                    extractedUri = FreebaseParser.getFreebaseLink(FreebaseParser.extractFreebaseId(item));
                    if (extractedUri != null && !extractedUri.trim().isEmpty()) {
                        break;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Person freebasePerson = null;
        if(extractedUri != null && !extractedUri.trim().isEmpty()) {
            freebasePerson = freebaseWhoIs(new URI(extractedUri));
        }


        Map<String, Object> map = new HashMap<>();
        map.put("freebase", freebasePerson);
        map.put("dbpedia", dbpediaPerson);
        return map;
    }

    public Person freebaseWhoIs(URI freebaseURI) {
        if(freebaseURI == null || freebaseURI.toString().trim().isEmpty()) {
            return null;
        }

        try {
            WebTarget client;
            String personInfoResponse, aux;
            ObjectMapper mapper = new ObjectMapper();

            client = ClientBuilder.newClient().target(freebaseURI);
            personInfoResponse = client.request().get(String.class);
            JsonNode personInfo = mapper.readTree(personInfoResponse).findValue("property");

            Person person = new Person();
            aux = FreebaseParser.getPersonName(personInfo);
            person.setName(aux);

            aux = FreebaseParser.getBirthdate(personInfo);
            person.setBirthdate(aux);

            aux = FreebaseParser.getDeathdate(personInfo);
            person.setDeathdate(aux);

            person.setBirthplace(FreebaseParser.getBirthplace(personInfo));

            aux = FreebaseParser.getAbstractDescription(personInfo);
            person.setDescription(aux);

            aux = FreebaseParser.getShortDescription(personInfo);
            person.setShortDescription(aux);

            person.setThumbnails(FreebaseParser.getThumbnail(personInfo));

            aux = FreebaseParser.getPrimaryTopicOf(personInfo);
            person.setWikiPageExternal(aux);

            person.setEducation(FreebaseParser.getEducation(personInfo));
            person.setNationality(FreebaseParser.getNationality(personInfo));

            person.setParents(FreebaseParser.getParents(personInfo));

            person.setSpouse(FreebaseParser.getSpouse(personInfo));
            person.setChildren(FreebaseParser.getChildren(personInfo));

            return person;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public Person dbpediaWhoIs(URI dbpediaUri) {
        if(dbpediaUri == null || dbpediaUri.toString().trim().isEmpty()) {
            return null;
        }

        try {
            WebTarget client;
            String personInfoResponse, aux;
            JsonNode personInfo;
            ObjectMapper mapper = new ObjectMapper();

            client = ClientBuilder.newClient().target(DBPediaParser.convertDBPediaUrlToResourceUrl(dbpediaUri.toString()));
            personInfoResponse = client.request().get(String.class);
            personInfo = mapper.readTree(personInfoResponse).findValue(dbpediaUri.toString());

            Person person = new Person();
            aux = DBPediaParser.getName(personInfo);
            person.setName(aux);

            aux = DBPediaParser.getBirthdate(personInfo);
            person.setBirthdate(aux);

            aux = DBPediaParser.getDeathdate(personInfo);
            person.setDeathdate(aux);

            person.setBirthplace(DBPediaParser.getBirthplace(personInfo));

            aux = DBPediaParser.getAbstractDescription(personInfo);
            person.setDescription(aux);

            aux = DBPediaParser.getShortDescription(personInfo);
            person.setShortDescription(aux);

            aux = DBPediaParser.getPrimaryTopicOf(personInfo);
            person.setWikiPageExternal(aux);

            person.setEducation(DBPediaParser.getEducation(personInfo));
            person.setNationality(DBPediaParser.getNationality(personInfo));

            person.setParents(DBPediaParser.getParents(personInfo));
            person.setThumbnails(DBPediaParser.getThumbnail(personInfo));
            person.setSpouse(DBPediaParser.getSpouse(personInfo));
            person.setChildren(DBPediaParser.getChildren(personInfo));

            return person;

        }catch (IOException e) {
            e.printStackTrace();
        }


        return null;
    }

}
