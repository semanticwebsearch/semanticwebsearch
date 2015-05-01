package ro.semanticwebsearch.responsegenerator;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.Logger;
import ro.semanticwebsearch.responsegenerator.model.Conflict;
import ro.semanticwebsearch.responsegenerator.parser.DBPediaParser;
import ro.semanticwebsearch.responsegenerator.parser.FreebaseParser;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

/**
 * Created by Spac on 4/26/2015.
 */
public class ConflictThatTookPlaceInCountry extends AbstractQuestionType {

    private static Logger log = Logger.getLogger(ConflictThatTookPlaceInCountry.class.getCanonicalName());

    @Override
    public Object parseFreebaseResponse(String freebaseResponse) {
        if (log.isInfoEnabled()) {
            log.info("ConflictThatTookPlaceInCountry" + " : " + freebaseResponse);
        }

        if(freebaseResponse == null || freebaseResponse.trim().isEmpty()) {
            return null;
        }

        ArrayList<String> conflictUris = new ArrayList<>();
        ArrayList<Conflict> conflicts = new ArrayList<>();
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode response = mapper.readTree(freebaseResponse).findValue("result");

            if (response.isArray()) {
                for(JsonNode item : response) {
                    conflictUris.add(FreebaseParser.getFreebaseLink(FreebaseParser.extractFreebaseId(item)));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        for(String uri : conflictUris) {
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

        if(dbpediaResponse == null || dbpediaResponse.trim().isEmpty()) {
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
                for(JsonNode responseItem : response) {
                    aux = responseItem.findValue("x0");

                    if(aux != null && aux.findValue("type").toString().equals("\"uri\"")) {
                        conflictUris.add(DBPediaParser.extractValue(aux.findValue("value")));
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        for(String uri : conflictUris) {
            try {
                conflicts.add(dbpediaConflict(new URI(uri)));
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }

        return conflicts;
    }

    private Conflict freebaseConflict(URI freebaseURI) {
        if(freebaseURI == null || freebaseURI.toString().trim().isEmpty()) {
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
            aux = FreebaseParser.getPersonName(conflictInfo);
            conflict.setName(aux);

            //conflict.setResult(FreebaseParser.getResult(conflictInfo));
            conflict.setDate(FreebaseParser.getEventDate(conflictInfo));
            conflict.setWikiPageExternal(FreebaseParser.getPrimaryTopicOf(conflictInfo));
            conflict.setDescription(FreebaseParser.getAbstractDescription(conflictInfo));
            conflict.setPartOf(FreebaseParser.getPartOf(conflictInfo));
            conflict.setThumbnails(FreebaseParser.getThumbnail(conflictInfo));

            conflict.setPlace(FreebaseParser.getEventLocations(conflictInfo));
            conflict.setCommanders(FreebaseParser.getCommanders(conflictInfo));
            conflict.setCombatants(FreebaseParser.getCombatants(conflictInfo));
            conflict.setCasualties(FreebaseParser.getCasualties(conflictInfo));

            return conflict;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public Conflict dbpediaConflict(URI dbpediaUri) {

        if(dbpediaUri == null || dbpediaUri.toString().trim().isEmpty()) {
            return null;
        }

        try {
            WebTarget client;
            String conflictInfoResponse, aux;
            JsonNode conflictInfo;
            ObjectMapper mapper = new ObjectMapper();

            client = ClientBuilder.newClient().target(DBPediaParser.convertDBPediaUrlToResourceUrl(dbpediaUri.toString()));
            conflictInfoResponse = client.request().get(String.class);
            conflictInfo = mapper.readTree(conflictInfoResponse).findValue(dbpediaUri.toString());

            Conflict conflict = new Conflict();
            aux = DBPediaParser.getName(conflictInfo);
            conflict.setName(aux);

            conflict.setResult(DBPediaParser.getResult(conflictInfo));
            conflict.setDate(DBPediaParser.getDate(conflictInfo));
            conflict.setWikiPageExternal(DBPediaParser.getPrimaryTopicOf(conflictInfo));
            conflict.setDescription(DBPediaParser.getAbstractDescription(conflictInfo));
            conflict.setPlace(DBPediaParser.getPlaces(conflictInfo));
            conflict.setCommanders(DBPediaParser.getCommanders(conflictInfo));
            conflict.setThumbnails(DBPediaParser.getThumbnail(conflictInfo));


            return conflict;

        }catch (IOException e) {
            e.printStackTrace();
        }
        return null;

    }
}
