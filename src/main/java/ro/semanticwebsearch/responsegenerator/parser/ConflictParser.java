package ro.semanticwebsearch.responsegenerator.parser;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.Logger;
import ro.semanticwebsearch.persistence.MongoDBManager;
import ro.semanticwebsearch.responsegenerator.model.Answer;
import ro.semanticwebsearch.responsegenerator.model.Conflict;
import ro.semanticwebsearch.responsegenerator.parser.helper.Constants;
import ro.semanticwebsearch.responsegenerator.parser.helper.DBPediaPropertyExtractor;
import ro.semanticwebsearch.responsegenerator.parser.helper.FreebasePropertyExtractor;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Spac on 4/26/2015.
 */
class ConflictParser extends AbstractParserType {

    public ConflictParser() {
        TYPE = Constants.CONFLICT;
    }

    private static Logger log = Logger.getLogger(ConflictParser.class.getCanonicalName());

    @Override
    public List<Answer> parseFreebaseResponse(String freebaseResponse, String questionId) {
        if (log.isInfoEnabled()) {
            log.info("ConflictThatTookPlaceInCountry" + " : " + freebaseResponse);
        }

        if (freebaseResponse == null || freebaseResponse.trim().isEmpty()) {
            return null;
        }

        ArrayList<String> conflictUris = new ArrayList<>();
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode response = mapper.readTree(freebaseResponse).get("result");

            if (response.isArray()) {
                for (JsonNode item : response) {
                    conflictUris.add(FreebasePropertyExtractor.getFreebaseLink(FreebasePropertyExtractor.extractFreebaseId(item)));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


        List<Answer> answers = new ArrayList<>();
        List<String> listOfUris = new LinkedList<>(conflictUris);

        extractFreebaseAnswers(questionId, listOfUris, answers, 0, Constants.MAX_CHUNK_SIZE);
        new Thread(() -> extractFreebaseAnswers(questionId, listOfUris, null, Constants.MAX_CHUNK_SIZE, 0)).start();

        return answers;
    }


    @Override
    public List<Answer> parseDBPediaResponse(String dbpediaResponse, String questionId) {
        if (log.isInfoEnabled()) {
            log.info("ConflictThatTookPlaceInCountry" + " : " + dbpediaResponse);
        }

        if (dbpediaResponse == null || dbpediaResponse.trim().isEmpty()) {
            return null;
        }

        ArrayList<String> conflictUris = new ArrayList<>();

        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode response = mapper.readTree(dbpediaResponse).get("results").get("bindings");
            if (response.isArray()) {
                JsonNode aux;
                //x0 -> conflict uri, x1 -> [country/place]
                for (JsonNode responseItem : response) {
                    aux = responseItem.get("x0");

                    if (aux != null && aux.get("type").toString().equals("\"uri\"")) {
                        conflictUris.add(DBPediaPropertyExtractor.extractValue(aux.get("value")));
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


        List<Answer> answers = new ArrayList<>();
        List<String> listOfUris = new LinkedList<>(conflictUris);

        extractDBPediaAnswers(questionId, listOfUris, answers, 0, Constants.MAX_CHUNK_SIZE);
        new Thread(() -> extractDBPediaAnswers(questionId, listOfUris, null, Constants.MAX_CHUNK_SIZE, 0)).start();

        return answers;
    }

    private Conflict freebaseConflict(URI freebaseURI) {
        if (freebaseURI == null || freebaseURI.toString().trim().isEmpty()) {
            return null;
        }

        try {
            WebTarget client;
            String personInfoResponse, aux;
            ObjectMapper mapper = new ObjectMapper();

            client = ClientBuilder.newClient().target(freebaseURI);
            personInfoResponse = client.request().get(String.class);
            JsonNode conflictInfo = mapper.readTree(personInfoResponse).get("property");

            Conflict conflict = new Conflict();
            aux = FreebasePropertyExtractor.getPersonName(conflictInfo);
            conflict.setName(aux);

            //conflict.setResult(FreebaseParser.getResult(conflictInfo));
            conflict.setDate(FreebasePropertyExtractor.getEventDate(conflictInfo));
            conflict.setWikiPageExternal(FreebasePropertyExtractor.getPrimaryTopicOf(conflictInfo));
            conflict.setDescription(FreebasePropertyExtractor.getAbstractDescription(conflictInfo));
            conflict.setPartOf(FreebasePropertyExtractor.getPartOf(conflictInfo));
            conflict.setThumbnails(FreebasePropertyExtractor.getThumbnail(conflictInfo));

            conflict.setPlace(FreebasePropertyExtractor.getEventLocations(conflictInfo));
            conflict.setCommanders(FreebasePropertyExtractor.getCommanders(conflictInfo));
            conflict.setCombatants(FreebasePropertyExtractor.getCombatants(conflictInfo));
            conflict.setCasualties(FreebasePropertyExtractor.getCasualties(conflictInfo));

            return conflict;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public Conflict dbpediaConflict(URI dbpediaUri) {

        if (dbpediaUri == null || dbpediaUri.toString().trim().isEmpty()) {
            return null;
        }

        try {
            WebTarget client;
            String conflictInfoResponse, aux;
            JsonNode conflictInfo;
            ObjectMapper mapper = new ObjectMapper();

            client = ClientBuilder.newClient().target(DBPediaPropertyExtractor.convertDBPediaUrlToResourceUrl(dbpediaUri.toString()));
            conflictInfoResponse = client.request().get(String.class);
            conflictInfo = mapper.readTree(conflictInfoResponse).get(dbpediaUri.toString());

            Conflict conflict = new Conflict();
            aux = DBPediaPropertyExtractor.getName(conflictInfo);
            conflict.setName(aux);

            conflict.setResult(DBPediaPropertyExtractor.getResult(conflictInfo));
            conflict.setDate(DBPediaPropertyExtractor.getDate(conflictInfo));
            conflict.setWikiPageExternal(DBPediaPropertyExtractor.getPrimaryTopicOf(conflictInfo));
            conflict.setDescription(DBPediaPropertyExtractor.getAbstractDescription(conflictInfo));
            conflict.setPlace(DBPediaPropertyExtractor.getPlaces(conflictInfo));
            conflict.setCommanders(DBPediaPropertyExtractor.getCommanders(conflictInfo));
            conflict.setThumbnails(DBPediaPropertyExtractor.getThumbnail(conflictInfo));
            conflict.setCombatants(DBPediaPropertyExtractor.getCombatants(conflictInfo));
            conflict.setPartOf(DBPediaPropertyExtractor.getPartOf(conflictInfo));

            return conflict;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;

    }

    private void extractDBPediaAnswers(String questionId, List<String> uris, List<Answer> answers, int offset, int finish) {
        Conflict conflict;
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
                conflict = dbpediaConflict(new URI(uris.get(idx)));
                Answer s = Answer.getBuilderForQuestion(questionId)
                        .setBody(conflict)
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

    private void extractFreebaseAnswers(String questionId, List<String> uris, List<Answer> answers, int offset, int max) {
        Conflict conflict;
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
                conflict = freebaseConflict(new URI(uris.get(idx)));
                Answer s = Answer.getBuilderForQuestion(questionId)
                        .setBody(conflict)
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
}
