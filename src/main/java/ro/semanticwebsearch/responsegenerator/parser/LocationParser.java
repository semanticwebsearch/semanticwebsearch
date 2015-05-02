package ro.semanticwebsearch.responsegenerator.parser;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import ro.semanticwebsearch.responsegenerator.parser.helper.DBPediaPropertyExtractor;
import ro.semanticwebsearch.responsegenerator.parser.helper.FreebasePropertyExtractor;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by Spac on 5/2/2015.
 */
class LocationParser extends AbstractParserType  {

    @Override
    public Object parseFreebaseResponse(String freebaseResponse) {
        String extractedUri = "";
        //region extract uri from freebase
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode results = mapper.readTree(freebaseResponse).findValue("result");
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
        return null;
    }

    @Override
    public Object parseDBPediaResponse(String dbpediaResponse) {
        String extractedUri = "";
        //region extract uri from dbpedia response
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode responseNode = mapper.readTree(dbpediaResponse);

            if (responseNode.has("results")) {
                responseNode = responseNode.findValue("results").findValue("bindings");
            }

            if (responseNode.isArray()) {
                JsonNode aux;

                //iterates through object in bindings array
                for (JsonNode node : responseNode) {
                    //elements from every object (x0,x1..) these are properties
                    aux = node.findValue("x0");
                    if (aux != null && aux.findValue("type").toString().equals("\"uri\"")) {
                        extractedUri = DBPediaPropertyExtractor.extractValue(aux.findValue("value"));
                        break;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        //endregion
        if (extractedUri != null && !extractedUri.trim().isEmpty()) {
            try {
                return dbpediaLocation(new URI(extractedUri));
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private Object dbpediaLocation(URI dbpediaUri) {

        if (dbpediaUri == null || dbpediaUri.toString().trim().isEmpty()) {
            return null;
        }

        try {
            WebTarget client;
            String locatoinInfoResponse;
            JsonNode locationInfo;
            ObjectMapper mapper = new ObjectMapper();

            client = ClientBuilder.newClient().target(DBPediaPropertyExtractor.convertDBPediaUrlToResourceUrl(dbpediaUri.toString()));
            locatoinInfoResponse = client.request().get(String.class);
            locationInfo = mapper.readTree(locatoinInfoResponse).findValue(dbpediaUri.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
