package ro.semanticwebsearch.businesslogic.questions;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.log4j.Logger;
import ro.semanticwebsearch.businesslogic.Person;
import ro.semanticwebsearch.businesslogic.ServiceResponse;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.Map;

/**
 * Created by Spac on 4/18/2015.
 */
public class WhoIs implements QuestionType {

    private static final String QUESTION = "Who is";
    private static final String FREEBASE_RESOURCE_LINK = "https://www.googleapis.com/freebase/v1/topic%s?filter=all";
    private static Logger log = Logger.getLogger(WhoIs.class.getCanonicalName());

    public WhoIs() {
        System.out.println("constructor WhoIs");
    }

    @Override
    public Map<String, JsonNode> doSomethingUseful(ServiceResponse response)
            throws UnsupportedEncodingException, URISyntaxException, InstantiationException, IllegalAccessException {

        if (log.isInfoEnabled()) {
            log.info(QUESTION + " : " + response);
        }

        String dbpediaResponse = response.getDbpediaResponse();
        parseDBPediaResponse(dbpediaResponse);

        String freebaseResponse = response.getFreebaseResponse();

        return null;
    }

    private void parseDBPediaResponse(String dbpediaResponse) {
        String personUri = null;

        //region Extract person Uri from x0
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode response = mapper.readTree(dbpediaResponse).findValue("results").findValue("bindings");
            if(response.isArray()) {
                ArrayNode bindingsArray = (ArrayNode) response;
                JsonNode aux;

                //iterates through object in bindings array
                for (JsonNode node : bindingsArray) {
                    //elements from every object (x0,x1..) these are properties
                    aux = node.findValue("x0");
                    if(aux != null && aux.findValue("type").toString().equals("\"uri\"")) {
                        personUri = extractValue(aux.findValue("value"));
                        break;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        //endregion

        WebTarget client;
        String personInfoResponse, aux;
        JsonNode personInfo;
        //am facut get pe person uri, am luat datele sale
        if(personUri != null) {
            client = ClientBuilder.newClient().target(convertDBPediaUrlToResourceUrl(personUri));
            personInfoResponse = client.request().get(String.class);
            Person person = new Person();
            try {
                ObjectMapper mapper = new ObjectMapper();
                //in person info avem informatii despre persoana respectiva data nasterii/death, birthplace, etc
                personInfo = mapper.readTree(personInfoResponse).findValue(personUri);

                aux = getPersonName(personInfo);
                person.setName(aux);


                aux = getBirthdate(personInfo);
                person.setBirthdate(aux);

                aux = getDeathdate(personInfo);
                person.setDeathdate(aux);

                person.setBirthplace(getBirthplace(personInfo));

                aux = getAbstractDescription(personInfo);
                person.setDescription(aux);

                aux = getShortDescription(personInfo);
                person.setShortDescription(aux);

                aux = getPrimaryTopicOf(personInfo);
                person.setWikiPageExternal(aux);

                person.setEducation(getDBpediaEducation(personInfo));
                person.setNationality(getDBPediaNationality(personInfo));
                System.out.println(person);

            }catch (IOException e) {
                e.printStackTrace();
            }

        }

    }

    private Pair<String, String> getDBPediaNationality(JsonNode personInfo) {
        ArrayNode nationalities = (ArrayNode)personInfo.findValue(MetadataProperties.NATIONALITY.getDbpedia());
        if (nationalities != null) {
            for(JsonNode nationality : nationalities) {
                if (isLiteral(nationality)) {
                    return new MutablePair<>("", extractValue(nationality.findValue("value")));
                } else if(isUri(nationality)) {
                    String uri = extractValue(nationality.findValue("value"));
                    String[] pieces = uri.split("/");
                    String education = pieces[pieces.length - 1];
                    return new MutablePair<>(uri, education.replace("_", " "));
                }
            }
        }

        return null;

    }

    private Pair<String, String> getDBpediaEducation(JsonNode personInfo) {
        ArrayNode educations = (ArrayNode) personInfo.findValue(MetadataProperties.EDUCATION_ONTOLOGY.getDbpedia());
        if(educations != null) {
            for(JsonNode educationNode : educations) {
                if(isLiteral(educationNode)) {
                    return new MutablePair<>("", extractValue(educationNode.findValue("value")));
                } else if(isUri(educationNode)) {
                    String uri = extractValue(educationNode.findValue("value"));
                    String[] pieces = uri.split("/");
                    String education = pieces[pieces.length - 1];
                    return new MutablePair<>(uri, education.replace("_", " "));
                }
            }
        }

        educations = (ArrayNode) personInfo.findValue(MetadataProperties.ALMA_MATER.getDbpedia());
        if(educations != null) {
            for(JsonNode educationNode : educations) {
                if(isLiteral(educationNode)) {
                    return new MutablePair<>("", extractValue(educationNode.findValue("value")));
                } else if(isUri(educationNode)) {
                    String uri = extractValue(educationNode.findValue("value"));
                    String[] pieces = uri.split("/");
                    String education = pieces[pieces.length - 1];
                    return new MutablePair<>(uri, education.replace("_", " "));
                }
            }
        }

        return null;

    }

    private String getPrimaryTopicOf(JsonNode personInfo) {
        ArrayNode topics = (ArrayNode)personInfo.findValue(MetadataProperties.PRIMARY_TOPIC_OF.getDbpedia());
        if(topics != null) {
            for (JsonNode topic : topics) {
                if (isUri(topic)) {
                    return extractValue(topic.findValue("value"));
                }
            }
        }
        return "";
    }

    private String getShortDescription(JsonNode personInfo) {
        ArrayNode descriptions = (ArrayNode)personInfo.findValue(MetadataProperties.SHORT_DESCRIPTION.getDbpedia());
        if (descriptions != null) {
            for(JsonNode description : descriptions) {
                if(isLiteral(description)) {
                    return extractValue(description.findValue("value"));
                }
            }
        }
        return "";
    }

    private String getAbstractDescription(JsonNode personInfo) {
        ArrayNode descriptions = (ArrayNode)personInfo.findValue(MetadataProperties.ABSTRACT.getDbpedia());
        if(descriptions != null) {
            for (JsonNode abstractDescription : descriptions) {
                if (isLiteral(abstractDescription) && isEN(abstractDescription)) {
                    return extractValue(abstractDescription.findValue("value"));
                }
            }

            for (JsonNode abstractDescription : descriptions) {
                if (isLiteral(abstractDescription)/* && isEN(abstractDescription)*/) {
                    return extractValue(abstractDescription.findValue("value"));
                }
            }
        }
        return "";
    }

    private String getDeathdate(JsonNode personInfo) {
        ArrayNode dates = (ArrayNode) personInfo.findValue(MetadataProperties.DEATHDATE.getDbpedia());
        if(dates != null) {
            for (JsonNode deathDate : dates) {
                if (isLiteral(deathDate)) {
                    return extractValue(deathDate.findValue("value"));
                }
            }
        }
        return "";
    }

    private Pair<String, String> getBirthplace(JsonNode personInfo) {
        ArrayNode places = (ArrayNode)personInfo.findValue(MetadataProperties.BIRTHPLACE.getDbpedia());
        if(places != null) {
            for (JsonNode birthplace : places) {
                if (isLiteral(birthplace)) {
                    return new MutablePair<>("", extractValue(birthplace.findValue("value")));
                } else if (isUri(birthplace)) {
                    String uri = extractValue(birthplace.findValue("value"));
                    String[] pieces = uri.split("/");
                    String birthPlace = pieces[pieces.length - 1];
                    return new MutablePair<>(uri, birthPlace.replace("_", " "));
                }
            }
        }
        return null;
    }

    private String getBirthdate(JsonNode personInfo) {
        ArrayNode dates = (ArrayNode)personInfo.findValue(MetadataProperties.BIRTHDATE.getDbpedia());
        if(dates != null) {
            for (JsonNode birthdate : dates) {
                if (isLiteral(birthdate)) {
                    return extractValue(birthdate.findValue("value"));
                }
            }
        }
        return "";
    }

    private String getPersonName(JsonNode personInfo) {
        ArrayNode names = (ArrayNode)personInfo.findValue(MetadataProperties.NAME.getDbpedia());
        StringBuilder sb = new StringBuilder();
        if(names != null) {
            for (JsonNode name : names) {
                if (isLiteral(name)) {
                    sb.append(extractValue(name.findValue("value"))).append(" / ");
                }
            }
        }
        sb.replace(sb.length() - 3, sb.length() - 1, "");
        return sb.toString();
    }

    /**
     * Given the json node, extracts the value and removes the "
     */
    private String extractValue(JsonNode node) {
        return node.toString().replace("\"", "");
    }

    /**
     * Given the resource url found in dbpedia response, converts it in a resource url by replaces /page/ or /resource/ with /data/
     */
    private String convertDBPediaUrlToResourceUrl(String dbpediaURL) {
        StringBuilder builder = null;
        if(dbpediaURL.contains("/page/")){
            builder = new StringBuilder(dbpediaURL.replace("/page/",
                    "/data/"));
        }else{
            builder = new StringBuilder(dbpediaURL.replace("/resource/",
                    "/data/"));
        }

        builder.append(".json");
        return builder.toString();
    }

    private boolean isUri(JsonNode node) {
        return "\"uri\"".equals(node.findValue("type").toString());
    }

    private boolean isLiteral(JsonNode node) {
        return "\"literal\"".equals(node.findValue("type").toString());
    }

    private boolean isEN(JsonNode node) {
        return node.findValue("lang")!= null && "\"en\"".equals(node.findValue("lang").toString());
    }


}
