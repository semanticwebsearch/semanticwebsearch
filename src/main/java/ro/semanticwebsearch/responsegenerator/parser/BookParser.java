package ro.semanticwebsearch.responsegenerator.parser;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.Logger;
import ro.semanticwebsearch.persistence.MongoDBManager;
import ro.semanticwebsearch.responsegenerator.model.Answer;
import ro.semanticwebsearch.responsegenerator.model.Book;
import ro.semanticwebsearch.responsegenerator.model.Person;
import ro.semanticwebsearch.responsegenerator.parser.helper.Constants;
import ro.semanticwebsearch.responsegenerator.parser.helper.DBPediaPropertyExtractor;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

/**
 * Created by Spac on 6/21/2015.
 */
class BookParser extends AbstractParserType  {
    private static Logger log = Logger.getLogger(BookParser.class.getCanonicalName());

    public BookParser() {
        TYPE = Constants.PERSON;
    }


    @Override
    public List<Answer> parseFreebaseResponse(String freebaseResponse, String questionId) {
        System.out.println(freebaseResponse);
        return null;
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

    private void extractDBPediaAnswers(String questionId, List<String> uris,
                                       List<Answer> answers, int offset, int finish) {
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
                person = dbpediaBook(new URI(uris.get(idx)));
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

    private Person dbpediaBook(URI dbpediaUri) {
        if (dbpediaUri == null || dbpediaUri.toString().trim().isEmpty()) {
            return null;
        }

        try {
            WebTarget client;
            String personInfoResponse, aux;
            JsonNode bookInfo;
            ObjectMapper mapper = new ObjectMapper();

            client = ClientBuilder.newClient().target(DBPediaPropertyExtractor.convertDBPediaUrlToResourceUrl(dbpediaUri.toString()));
            personInfoResponse = client.request().get(String.class);
            JsonNode response = mapper.readTree(personInfoResponse);
            bookInfo = response.get(dbpediaUri.toString());

            Book book = new Book();
            //book.setAuthor();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
