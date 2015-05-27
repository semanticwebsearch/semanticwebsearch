package ro.semanticwebsearch.responsegenerator.parser;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.Logger;
import ro.semanticwebsearch.responsegenerator.model.Person;
import ro.semanticwebsearch.responsegenerator.parser.helper.DBPediaPropertyExtractor;
import ro.semanticwebsearch.responsegenerator.parser.helper.FreebasePropertyExtractor;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Spac on 4/18/2015.
 */
class PersonParser extends AbstractParserType {

    private static Logger log = Logger.getLogger(PersonParser.class.getCanonicalName());

    public PersonParser() {
        System.out.println("constructor WhoIs");
    }

    @Override
    public List<Person> parseDBPediaResponse(String dbpediaResponse) {
        String extractedUri = "";
        List<String> uris = new ArrayList<>();
        List<Person> persons = new ArrayList<>();
        //region extract uri from dbpedia response
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode responseNode = mapper.readTree(dbpediaResponse);

            if (responseNode.has("results")) {
                responseNode = responseNode.get("results").get("bindings");
            }

            if (responseNode.isArray()) {
                JsonNode aux;

                //iterates through object in bindings array
                for (JsonNode node : responseNode) {
                    //elements from every object (x0,x1..) these are properties
                    aux = node.get("x0");
                    if (aux != null && aux.get("type").toString().equals("\"uri\"")) {
                        extractedUri = DBPediaPropertyExtractor.extractValue(aux.get("value"));
                        uris.add(extractedUri);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        //endregion

        for(String uri : uris) {
            try {
                persons.add(dbpediaWhoIs(new URI(uri)));
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }

        return persons;
    }

    @Override
    public List<Person> parseFreebaseResponse(String freebaseResponse) {
        String extractedUri = "";
        List<String> uris = new ArrayList<>();
        List<Person> persons = new ArrayList<>();
        //region extract uri from freebase
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode results = mapper.readTree(freebaseResponse).get("result");
            if (results.isArray()) {
                for (JsonNode item : results) {
                    extractedUri = FreebasePropertyExtractor.getFreebaseLink(FreebasePropertyExtractor.extractFreebaseId(item));
                    if (extractedUri != null && !extractedUri.trim().isEmpty()) {
                        uris.add(extractedUri);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        //endregion

        for(String uri : uris){
            try {
                persons.add(freebaseWhoIs(new URI(uri)));
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }

        return persons;
    }

    public Person freebaseWhoIs(URI freebaseURI) {
        if (freebaseURI == null || freebaseURI.toString().trim().isEmpty()) {
            return null;
        }

        try {
            WebTarget client;
            String personInfoResponse, aux;
            ObjectMapper mapper = new ObjectMapper();

            client = ClientBuilder.newClient().target(freebaseURI);
            personInfoResponse = client.request().get(String.class);
            JsonNode personInfo = mapper.readTree(personInfoResponse).get("property");

            Person person = new Person();
            aux = FreebasePropertyExtractor.getPersonName(personInfo);
            person.setName(aux);

            aux = FreebasePropertyExtractor.getBirthdate(personInfo);
            person.setBirthdate(aux);

            aux = FreebasePropertyExtractor.getDeathdate(personInfo);
            person.setDeathdate(aux);

            person.setBirthplace(FreebasePropertyExtractor.getBirthplace(personInfo));

            aux = FreebasePropertyExtractor.getAbstractDescription(personInfo);
            person.setDescription(aux);

            aux = FreebasePropertyExtractor.getShortDescription(personInfo);
            person.setShortDescription(aux);

            person.setThumbnails(FreebasePropertyExtractor.getThumbnail(personInfo));

            aux = FreebasePropertyExtractor.getPrimaryTopicOf(personInfo);
            person.setWikiPageExternal(aux);

            person.setEducation(FreebasePropertyExtractor.getEducation(personInfo));
            person.setNationality(FreebasePropertyExtractor.getNationality(personInfo));

            person.setParents(FreebasePropertyExtractor.getParents(personInfo));

            person.setSpouse(FreebasePropertyExtractor.getSpouse(personInfo));
            person.setChildren(FreebasePropertyExtractor.getChildren(personInfo));

            person.setNotableFor(FreebasePropertyExtractor.getNotableFor(personInfo));

            return person;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public Person dbpediaWhoIs(URI dbpediaUri) {
        if (dbpediaUri == null || dbpediaUri.toString().trim().isEmpty()) {
            return null;
        }

        try {
            WebTarget client;
            String personInfoResponse, aux;
            JsonNode personInfo;
            ObjectMapper mapper = new ObjectMapper();

            client = ClientBuilder.newClient().target(DBPediaPropertyExtractor.convertDBPediaUrlToResourceUrl(dbpediaUri.toString()));
            personInfoResponse = client.request().get(String.class);
            personInfo = mapper.readTree(personInfoResponse).get(dbpediaUri.toString());

            ro.semanticwebsearch.responsegenerator.model.Person person = new ro.semanticwebsearch.responsegenerator.model.Person();
            aux = DBPediaPropertyExtractor.getName(personInfo);
            person.setName(aux);

            aux = DBPediaPropertyExtractor.getBirthdate(personInfo);
            person.setBirthdate(aux);

            aux = DBPediaPropertyExtractor.getDeathdate(personInfo);
            person.setDeathdate(aux);

            person.setBirthplace(DBPediaPropertyExtractor.getBirthplace(personInfo));

            aux = DBPediaPropertyExtractor.getAbstractDescription(personInfo);
            person.setDescription(aux);

            aux = DBPediaPropertyExtractor.getShortDescription(personInfo);
            person.setShortDescription(aux);

            aux = DBPediaPropertyExtractor.getPrimaryTopicOf(personInfo);
            person.setWikiPageExternal(aux);

            person.setEducation(DBPediaPropertyExtractor.getEducation(personInfo));
            person.setNationality(DBPediaPropertyExtractor.getNationality(personInfo));

            person.setParents(DBPediaPropertyExtractor.getParents(personInfo));
            person.setThumbnails(DBPediaPropertyExtractor.getThumbnail(personInfo));
            person.setSpouse(DBPediaPropertyExtractor.getSpouse(personInfo));
            person.setChildren(DBPediaPropertyExtractor.getChildren(personInfo));

            return person;

        } catch (IOException e) {
            e.printStackTrace();
        }


        return null;
    }


}
