package ro.semanticwebsearch.responsegenerator.parser;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.Logger;
import ro.semanticwebsearch.responsegenerator.model.Conflict;
import ro.semanticwebsearch.responsegenerator.parser.helper.DBPediaPropertyExtractor;
import ro.semanticwebsearch.responsegenerator.parser.helper.FreebasePropertyExtractor;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

/**
 * Created by Spac on 4/26/2015.
 */
class ConflictParser extends AbstractParserType {

    private static Logger log = Logger.getLogger(ConflictParser.class.getCanonicalName());

    @Override
    public Object parseFreebaseResponse(String freebaseResponse) {
        if (log.isInfoEnabled()) {
            log.info("ConflictThatTookPlaceInCountry" + " : " + freebaseResponse);
        }

        if (freebaseResponse == null || freebaseResponse.trim().isEmpty()) {
            return null;
        }

        ArrayList<String> conflictUris = new ArrayList<>();
        ArrayList<Conflict> conflicts = new ArrayList<>();
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode response = mapper.readTree(freebaseResponse).findValue("result");

            if (response.isArray()) {
                for (JsonNode item : response) {
                    conflictUris.add(FreebasePropertyExtractor.getFreebaseLink(FreebasePropertyExtractor.extractFreebaseId(item)));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (String uri : conflictUris) {
            try {
                conflicts.add(freebaseConflict(new URI(uri)));
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }

        return conflicts;
    }


    @Override
    public Object parseDBPediaResponse(String dbpediaResponse) {
        if (log.isInfoEnabled()) {
            log.info("ConflictThatTookPlaceInCountry" + " : " + dbpediaResponse);
        }

        if (dbpediaResponse == null || dbpediaResponse.trim().isEmpty()) {
            return null;
        }

        ArrayList<String> conflictUris = new ArrayList<>();
        ArrayList<Conflict> conflicts = new ArrayList<>();

        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode response = mapper.readTree(dbpediaResponse).findValue("results").findValue("bindings");
            if (response.isArray()) {
                JsonNode aux;
                //x0 -> conflict uri, x1 -> [country/place]
                for (JsonNode responseItem : response) {
                    aux = responseItem.findValue("x0");

                    if (aux != null && aux.findValue("type").toString().equals("\"uri\"")) {
                        conflictUris.add(DBPediaPropertyExtractor.extractValue(aux.findValue("value")));
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (String uri : conflictUris) {
            try {
                conflicts.add(dbpediaConflict(new URI(uri)));
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }

        return conflicts;
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
            JsonNode conflictInfo = mapper.readTree(personInfoResponse).findValue("property");

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
            conflictInfo = mapper.readTree(conflictInfoResponse).findValue(dbpediaUri.toString());

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
}
