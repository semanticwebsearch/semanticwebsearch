package ro.semanticwebsearch.responsegenerator.parser;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import ro.semanticwebsearch.persistence.MongoDBManager;
import ro.semanticwebsearch.responsegenerator.model.Album;
import ro.semanticwebsearch.responsegenerator.model.Answer;
import ro.semanticwebsearch.responsegenerator.model.StringPair;
import ro.semanticwebsearch.responsegenerator.parser.helper.Constants;
import ro.semanticwebsearch.responsegenerator.parser.helper.DBPediaPropertyExtractor;
import ro.semanticwebsearch.responsegenerator.parser.helper.FreebasePropertyExtractor;
import ro.semanticwebsearch.responsegenerator.parser.helper.MetadataProperties;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Spac on 5/3/2015.
 */
class AlbumParser extends AbstractParserType {

    public AlbumParser() {
        TYPE = Constants.ALBUM;
    }

    @Override
    public List<Answer> parseFreebaseResponse(String freebaseResponse, String questionId) {
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

        List<Answer> answers = new ArrayList<>();
        List<String> listOfUris = new LinkedList<>(albumUris);

        extractFreebaseAnswers(questionId, listOfUris, answers, 0, Constants.MAX_CHUNK_SIZE);
        new Thread(() -> extractFreebaseAnswers(questionId, listOfUris, null, Constants.MAX_CHUNK_SIZE, 0)).start();

        return answers;
    }

    @Override
    public List<Answer> parseDBPediaResponse(String dbpediaResponse, String questionId) {
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

        List<Answer> answers = new ArrayList<>();
        List<String> listOfUris = new LinkedList<>(albumUris);

        extractDBPediaAnswers(questionId, listOfUris, answers, 0, Constants.MAX_CHUNK_SIZE);
        new Thread(() -> extractDBPediaAnswers(questionId, listOfUris, null, Constants.MAX_CHUNK_SIZE, 0)).start();

        return answers;
    }

    public Album dbpediaAlbum(URI dbpediaUri) {
        if (dbpediaUri == null || dbpediaUri.toString().trim().isEmpty()) {
            return null;
        }

        try {
            WebTarget client;
            String albumInfoResponse, aux;
            JsonNode albumInfo;
            ObjectMapper mapper = new ObjectMapper();

            client = ClientBuilder.newClient().target(DBPediaPropertyExtractor.convertDBPediaUrlToResourceUrl(dbpediaUri.toString()));
            albumInfoResponse = client.request().get(String.class);
            albumInfo = mapper.readTree(albumInfoResponse).get(dbpediaUri.toString());

            Album album = new Album();
            aux = DBPediaPropertyExtractor.getName(albumInfo);
            album.setName(aux);
            album.setThumbnails(DBPediaPropertyExtractor.getThumbnail(albumInfo));
            album.setWikiPageExternalLink(DBPediaPropertyExtractor.getPrimaryTopicOf(albumInfo));
            album.setThumbnails(DBPediaPropertyExtractor.getThumbnail(albumInfo));
            album.setReleaseDate(DBPediaPropertyExtractor.getReleaseDate(albumInfo));
            album.setGenre(DBPediaPropertyExtractor.getGenre(albumInfo));



            return album;

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public Album freebaseAlbum(URI freebaseURI) {
        if (freebaseURI == null || freebaseURI.toString().trim().isEmpty()) {
            return null;
        }

        try {
            WebTarget client;
            String albumInfoResponse, aux;
            ObjectMapper mapper = new ObjectMapper();

            client = ClientBuilder.newClient().target(freebaseURI);
            albumInfoResponse = client.request().get(String.class);
            JsonNode albumInfo = mapper.readTree(albumInfoResponse).get("property");

            Album album = new Album();
            aux = FreebasePropertyExtractor.getPersonName(albumInfo);

            album.setName(aux);
            album.setThumbnails(FreebasePropertyExtractor.getThumbnail(albumInfo));
            album.setWikiPageExternalLink(FreebasePropertyExtractor.getPrimaryTopicOf(albumInfo));
            album.setDescription(FreebasePropertyExtractor.getAbstractDescription(albumInfo));
            album.setReleaseDate(FreebasePropertyExtractor.getReleaseDate(albumInfo));
            album.setGenre(FreebasePropertyExtractor.getGenre(albumInfo));

            String releaseId = FreebasePropertyExtractor.getPrimaryReleaseId(albumInfo);
            releaseId = FreebasePropertyExtractor.getFreebaseLink(releaseId);
            //album.setTrackList(getTrackList(new URI(releaseId)));

            return album;

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;

    }

    private ArrayList<StringPair> getTrackList(URI freebaseURI) {
        if (freebaseURI == null || freebaseURI.toString().trim().isEmpty()) {
            return null;
        }

        try {
            WebTarget client;
            String albumInfoResponse;
            JsonNode aux;
            ObjectMapper mapper = new ObjectMapper();

            client = ClientBuilder.newClient().target(freebaseURI);
            albumInfoResponse = client.request().get(String.class);
            JsonNode albumInfo = mapper.readTree(albumInfoResponse).get("property");


            ArrayList<StringPair> trackList = FreebasePropertyExtractor.getTrackList(albumInfo);
            for(StringPair track : trackList) {
                track.setValue(FreebasePropertyExtractor.getFreebaseLink(track.getValue()));
                client = ClientBuilder.newClient().target(track.getValue());
                albumInfoResponse = client.request().get(String.class);
                albumInfo = mapper.readTree(albumInfoResponse).get("property");

                aux = albumInfo.path(MetadataProperties.TRACK_LENGTH.getFreebase()).path("values");
                if(!FreebasePropertyExtractor.isMissingNode(aux)) {
                    if(aux.isArray()) {
                        aux = aux.get(0);
                    }

                    track.setValue(DBPediaPropertyExtractor.extractValue(aux.get("value")));
                } else {
                    track.setValue("");
                }
            }

            return trackList;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private void extractDBPediaAnswers(String questionId, List<String> uris, List<Answer> answers, int offset, int finish) {
        Album album;
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
                album = dbpediaAlbum(new URI(uris.get(idx)));
                Answer s = Answer.getBuilderForQuestion(questionId)
                        .setBody(album)
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
        Album album;
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
                album = freebaseAlbum(new URI(uris.get(idx)));
                Answer s = Answer.getBuilderForQuestion(questionId)
                        .setBody(album)
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
