package ro.semanticwebsearch.responsegenerator.parser;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import ro.semanticwebsearch.persistence.MongoDBManager;
import ro.semanticwebsearch.responsegenerator.model.Answer;
import ro.semanticwebsearch.responsegenerator.model.Location;
import ro.semanticwebsearch.responsegenerator.parser.helper.Constants;
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
 * Created by Spac on 5/2/2015.
 */
class LocationParser extends AbstractParserType  {

    public LocationParser() {
        TYPE = Constants.LOCATION;
    }

    @Override
    public List<Answer> parseFreebaseResponse(String freebaseResponse, String questionId) {
        String extractedUri = "";
        //region extract uri from freebase
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode results = mapper.readTree(freebaseResponse).get("result");
            if (results.isArray()) {
                for (JsonNode item : results) {
                    extractedUri = FreebasePropertyExtractor.getFreebaseLink(FreebasePropertyExtractor.extractFreebaseId(item));
                    if (extractedUri != null && !extractedUri.trim().isEmpty()) {
                        break;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        //endregion
        Location aux;
        List<Answer> locations = new ArrayList<>();
        if (extractedUri != null && !extractedUri.trim().isEmpty()) {
            try {
                aux = freebaseLocation(new URI(extractedUri));
                if(aux != null) {
                    Answer s = Answer.getBuilderForQuestion(questionId)
                            .setBody(aux)
                            .setOrigin(Constants.FREEBASE)
                            .setType(TYPE)
                            .build();
                    locations.add(s);

                    MongoDBManager.saveAnswerList(locations);
                }
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }

        return locations;
    }

    private Location freebaseLocation(URI freebaseURI) {
        if (freebaseURI == null || freebaseURI.toString().trim().isEmpty()) {
            return null;
        }

        try {
            WebTarget client;
            String locationInfoResponse, aux;
            ObjectMapper mapper = new ObjectMapper();

            client = ClientBuilder.newClient().target(freebaseURI);
            locationInfoResponse = client.request().get(String.class);
            JsonNode locationInfo = mapper.readTree(locationInfoResponse).get("property");

            Location location = new Location();
            aux = FreebasePropertyExtractor.getPersonName(locationInfo);
            location.setName(aux);
            location.setDescription(FreebasePropertyExtractor.getAbstractDescription(locationInfo));
            location.setThumbnails(FreebasePropertyExtractor.getThumbnail(locationInfo));
            location.setWikiPageExternal(FreebasePropertyExtractor.getPrimaryTopicOf(locationInfo));
            location.setCapital(FreebasePropertyExtractor.getCapital(locationInfo));
            location.setOfficialLanguage(FreebasePropertyExtractor.getOfficialLanguage(locationInfo));
            location.setCurrency(FreebasePropertyExtractor.getCurrency(locationInfo));
            location.setCallingCode(FreebasePropertyExtractor.getCallingCode(locationInfo));
            location.setGeolocation(FreebasePropertyExtractor.getGeolocation(locationInfo));
            location.setArea(FreebasePropertyExtractor.getArea(locationInfo));
            location.setReligions(FreebasePropertyExtractor.getReligions(locationInfo));
            location.setDateFounded(FreebasePropertyExtractor.getDateFounded(locationInfo));
            location.setPopulation(FreebasePropertyExtractor.getPopulation(locationInfo));

            return location;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Answer> parseDBPediaResponse(String dbpediaResponse, String questionId) {
        String extractedUri = "";
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
                        break;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        //endregion

        Location aux;
        List<Answer> locations = new ArrayList<>();
        if (extractedUri != null && !extractedUri.trim().isEmpty()) {
            try {
                aux = dbpediaLocation(new URI(extractedUri));
                if(aux != null) {
                    Answer s = Answer.getBuilderForQuestion(questionId)
                            .setBody(aux)
                            .setOrigin(Constants.DBPEDIA)
                            .setType(TYPE)
                            .build();
                    locations.add(s);

                    MongoDBManager.saveAnswerList(locations);
                }
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }

        return locations;
    }

    private Location dbpediaLocation(URI dbpediaUri) {

        if (dbpediaUri == null || dbpediaUri.toString().trim().isEmpty()) {
            return null;
        }

        try {
            WebTarget client;
            String locationInfoResponse, aux;
            JsonNode locationInfo;
            ObjectMapper mapper = new ObjectMapper();

            client = ClientBuilder.newClient().target(DBPediaPropertyExtractor.convertDBPediaUrlToResourceUrl(dbpediaUri.toString()));
            locationInfoResponse = client.request().get(String.class);
            locationInfo = mapper.readTree(locationInfoResponse).get(dbpediaUri.toString());

            Location location = new Location();
            aux = DBPediaPropertyExtractor.getName(locationInfo);
            location.setName(aux);

            location.setDescription(DBPediaPropertyExtractor.getAbstractDescription(locationInfo));
            location.setThumbnails(DBPediaPropertyExtractor.getThumbnail(locationInfo));
            location.setWikiPageExternal(DBPediaPropertyExtractor.getPrimaryTopicOf(locationInfo));
            location.setGeolocation(DBPediaPropertyExtractor.getGeolocation(locationInfo));
            location.setOfficialLanguage(DBPediaPropertyExtractor.getLanguage(locationInfo));
            location.setCapital(DBPediaPropertyExtractor.getCapital(locationInfo));
            location.setCurrency(DBPediaPropertyExtractor.getCurrency(locationInfo));
            location.setCallingCode(DBPediaPropertyExtractor.getCallingCode(locationInfo));
            location.setArea(DBPediaPropertyExtractor.getArea(locationInfo));
            location.setDateFounded(DBPediaPropertyExtractor.getFoundingDate(locationInfo));
            location.setPopulation(DBPediaPropertyExtractor.getPopulation(locationInfo));

            return location;

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
