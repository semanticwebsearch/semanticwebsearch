package ro.semanticwebsearch.responsegenerator.parser;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import ro.semanticwebsearch.responsegenerator.model.Album;
import ro.semanticwebsearch.responsegenerator.parser.helper.DBPediaPropertyExtractor;
import ro.semanticwebsearch.responsegenerator.parser.helper.FreebasePropertyExtractor;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

/**
 * Created by Spac on 5/3/2015.
 */
class AlbumParser extends AbstractParserType {

    @Override
    public ArrayList<Album> parseFreebaseResponse(String freebaseResponse) {
        String extractedUri = "";
        ArrayList<String> albumUris = new ArrayList<>();

        //region extract uri from freebase
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode results = mapper.readTree(freebaseResponse).get("result");
            if (results.isArray()) {
                for (JsonNode item : results) {
                    extractedUri = FreebasePropertyExtractor.getFreebaseLink(FreebasePropertyExtractor.extractFreebaseId(item));
                    if (extractedUri != null && !extractedUri.trim().isEmpty()) {
                        albumUris.add(extractedUri);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        //endregion

        ArrayList<Album> albums = new ArrayList<>();
        for(String uri : albumUris) {
            try {
                albums.add(freebaseAlbum(new URI(uri)));
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }

        return albums;
    }

    @Override
    public ArrayList<Album> parseDBPediaResponse(String dbpediaResponse) {
        String extractedUri = "";
        ArrayList<String> albumUris = new ArrayList<>();

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
                        albumUris.add(extractedUri);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        //endregion

        ArrayList<Album> albums = new ArrayList<>();
        for(String uri : albumUris) {
            try {
                albums.add(dbpediaAlbum(new URI(uri)));
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }

        return albums;
    }

    private Album dbpediaAlbum(URI dbpediaUri) {
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

            Album album = new Album();
            aux = DBPediaPropertyExtractor.getName(locationInfo);
            album.setName(aux);

            return album;

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private Album freebaseAlbum(URI freebaseURI) {
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

            Album album = new Album();
            aux = FreebasePropertyExtractor.getPersonName(personInfo);
            album.setName(aux);

            return album;

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;

    }
}
