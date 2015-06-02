package ro.semanticwebsearch.responsegenerator.parser;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import ro.semanticwebsearch.responsegenerator.model.Answer;
import ro.semanticwebsearch.responsegenerator.parser.helper.Constants;
import ro.semanticwebsearch.responsegenerator.parser.helper.MetadataProperties;

import java.io.IOException;
import java.util.List;

/**
 * Created by Spac on 6/2/2015.
 */
class PersonnelInvolvedParser extends AbstractParserType {

    public PersonnelInvolvedParser() {
        TYPE = Constants.PERSON;
    }

    @Override
    public List<Answer> parseDBPediaResponse(String dbpediaResponse, String questionId) {
        PersonParser parser = new PersonParser();
        return parser.parseDBPediaResponse(dbpediaResponse, questionId);
    }

    @Override
    public List<Answer> parseFreebaseResponse(String freebaseResponse, String questionId) {
        ArrayNode personIdResult = new ArrayNode(JsonNodeFactory.instance);
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode results = mapper.readTree(freebaseResponse).get("result");
            if (results.isArray()) {
                for (JsonNode item : results) {
                    item = item.get(MetadataProperties.MILITARY_PERSONNEL_INVOLVED.getFreebase());
                    if(item != null && !item.isMissingNode()) {
                        if(item.isArray()) {
                            personIdResult.addAll((ArrayNode)item);
                        } else {
                            personIdResult.add(item);
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        ObjectNode result = new ObjectNode(JsonNodeFactory.instance);
        result.put("result", personIdResult);

        PersonParser parser = new PersonParser();
        return parser.parseFreebaseResponse(result.toString(), questionId);
    }
}
