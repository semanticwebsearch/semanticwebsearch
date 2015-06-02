package ro.semanticwebsearch.responsegenerator.parser;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.apache.log4j.Logger;
import ro.semanticwebsearch.persistence.MongoDBManager;
import ro.semanticwebsearch.responsegenerator.model.Answer;
import ro.semanticwebsearch.responsegenerator.model.Person;
import ro.semanticwebsearch.responsegenerator.parser.helper.Constants;
import ro.semanticwebsearch.responsegenerator.parser.helper.DBPediaPropertyExtractor;
import ro.semanticwebsearch.responsegenerator.parser.helper.FreebasePropertyExtractor;
import ro.semanticwebsearch.responsegenerator.parser.helper.MetadataProperties;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Spac on 4/12/2015.
 */
class ChildrenOfParser extends AbstractParserType  {
    private static Logger log = Logger.getLogger(ChildrenOfParser.class.getCanonicalName());

    public ChildrenOfParser() {
        TYPE = Constants.PERSON;
    }

    @Override
    public List<Answer> parseFreebaseResponse(String freebaseResponse, String questionId) {
        Map<String, String> freebaseChildrenInfo = new HashMap<>();
        parseFreebaseForUriAndName(freebaseResponse, freebaseChildrenInfo);

        ArrayList<String> listOfUris = new ArrayList<>();
        listOfUris.addAll(freebaseChildrenInfo.entrySet().stream().map(Map.Entry::getValue).collect(Collectors.toList()));

        List<Answer> answers = new ArrayList<>();
        extractFreebaseAnswers(questionId, listOfUris, answers, 0, Constants.MAX_CHUNK_SIZE);

        new Thread(() -> extractFreebaseAnswers(questionId, listOfUris, null, Constants.MAX_CHUNK_SIZE, 0)).start();

        return answers;
    }

    @Override
    public List<Answer> parseDBPediaResponse(String dbpediaResponse, String questionId) {
        Map<String, String> dbpediaChildrenInfo = new HashMap<>();
        parseDBPediaForUriAndName(dbpediaResponse, dbpediaChildrenInfo);

        ArrayList<String> listOfUris = new ArrayList<>();
        listOfUris.addAll(dbpediaChildrenInfo.entrySet().stream().map(Map.Entry::getValue).collect(Collectors.toList()));

        List<Answer> answers = new ArrayList<>();
        extractDBPediaAnswers(questionId, listOfUris, answers, 0, Constants.MAX_CHUNK_SIZE);

        new Thread(() -> extractDBPediaAnswers(questionId, listOfUris, null, Constants.MAX_CHUNK_SIZE, 0)).start();

        return answers;
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

        PersonParser parser = new PersonParser();

        for(int idx = start; idx < finish; idx++) {
            try {
                person = parser.freebaseWhoIs(new URI(uris.get(idx)));
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

        PersonParser parser = new PersonParser();
        for(int idx = start; idx < max; idx++) {
            try {
                person = parser.dbpediaWhoIs(new URI(uris.get(idx)));
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

}
