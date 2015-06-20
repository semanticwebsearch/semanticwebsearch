package ro.semanticwebsearch.responsegenerator.parser;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.Logger;
import ro.semanticwebsearch.persistence.MongoDBManager;
import ro.semanticwebsearch.responsegenerator.model.Answer;
import ro.semanticwebsearch.responsegenerator.model.Person;
import ro.semanticwebsearch.responsegenerator.parser.helper.Constants;
import ro.semanticwebsearch.responsegenerator.parser.helper.DBPediaPropertyExtractor;
import ro.semanticwebsearch.responsegenerator.parser.helper.FreebasePropertyExtractor;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

/**
 * Created by Spac on 4/18/2015.
 */
class PersonParser extends AbstractParserType {

    private static Logger log = Logger.getLogger(PersonParser.class.getCanonicalName());

    public PersonParser() {
        TYPE = Constants.PERSON;
    }

    @Override
    public List<Answer> parseDBPediaResponse(String dbpediaResponse, String questionId) {
        String extractedUri = "";
        Set<String> uris = new HashSet<>();
        List<Answer> answers = new ArrayList<>();

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

        List<String> listOfUris = new LinkedList<>(uris);
        extractDBPediaAnswers(questionId, listOfUris, answers, 0, Constants.MAX_CHUNK_SIZE);

        new Thread(() -> extractDBPediaAnswers(questionId, listOfUris, null, Constants.MAX_CHUNK_SIZE, 0)).start();

        return answers;
    }

    private void extractDBPediaAnswers(String questionId, List<String> uris, List<Answer> answers, int offset, int finish) {
        Person person;
        int start = 0, max = 0;

        if(offset == 0) {
            max = Math.min(finish, uris.size());
        } else {
            start = offset;
            max = uris.size();
        }

        if(answers == null) {
            answers = new ArrayList<>();
        }

        for(int idx = start; idx < max; idx++) {
            try {
                person = dbpediaWhoIs(new URI(uris.get(idx)));
                Answer s = Answer.getBuilderForQuestion(questionId)
                        .setBody(person)
                        .setOrigin(Constants.DBPEDIA)
                        .setType(TYPE)
                        .build();
                answers.add(s);
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }

        //save in db???
        MongoDBManager.saveAnswerList(answers);
    }

    @Override
    public List<Answer> parseFreebaseResponse(String freebaseResponse, String questionId) {
        String extractedUri;
        Set<String> uris = new HashSet<>();

        List<Answer> answers = new ArrayList<>();
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

        List<String> listOfUris = new LinkedList<>(uris);
        extractFreebaseAnswers(questionId, listOfUris, answers, 0, Constants.MAX_CHUNK_SIZE);
        new Thread(() -> extractFreebaseAnswers(questionId, listOfUris, null, Constants.MAX_CHUNK_SIZE, 0)).start();

        return answers;
    }

    private void extractFreebaseAnswers(String questionId, List<String> uris, List<Answer> answers, int offset, int max) {
        Person person;
        int start = 0, finish;

        if(offset == 0) {
            finish = Math.min(max, uris.size());
        } else {
            start = offset;
            finish = uris.size();
        }

        if(answers == null) {
            answers = new ArrayList<>();
        }

        for(int idx = start; idx < finish; idx++) {
            try {
                person = freebaseWhoIs(new URI(uris.get(idx)));
                Answer s = Answer.getBuilderForQuestion(questionId)
                        .setBody(person)
                        .setOrigin(Constants.FREEBASE)
                        .setType(TYPE)
                        .build();
                answers.add(s);
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }

        //save in db???
        MongoDBManager.saveAnswerList(answers);
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
            person.setSiblings(FreebasePropertyExtractor.getPersonSiblings(personInfo));

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
            JsonNode response = mapper.readTree(personInfoResponse);
            personInfo = response.get(dbpediaUri.toString());

            Person person = new Person();
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
            person.setSiblings(DBPediaPropertyExtractor.getPersonSiblings(response));

            return person;

        } catch (IOException e) {
            e.printStackTrace();
        }


        return null;
    }

}
