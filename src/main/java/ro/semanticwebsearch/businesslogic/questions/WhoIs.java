package ro.semanticwebsearch.businesslogic.questions;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.apache.log4j.Logger;
import ro.semanticwebsearch.businesslogic.Person;
import ro.semanticwebsearch.businesslogic.ServiceResponse;
import ro.semanticwebsearch.businesslogic.StringPair;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Spac on 4/18/2015.
 */
public class WhoIs implements QuestionType {

    private static final String QUESTION = "Who is";
    private static final String FREEBASE_RESOURCE_LINK = "https://www.googleapis.com/freebase/v1/topic%s?filter=all";
    private static final String FREEBASE_IMAGE_LINK = "https://usercontent.googleapis.com/freebase/v1/image%s"; //?maxwidth=200&maxheight=200
    private static Logger log = Logger.getLogger(WhoIs.class.getCanonicalName());

    public WhoIs() {
        System.out.println("constructor WhoIs");
    }

    @Override
    public Map<String, Object> doSomethingUseful(ServiceResponse response)
            throws UnsupportedEncodingException, URISyntaxException, InstantiationException, IllegalAccessException {

        if (log.isInfoEnabled()) {
            log.info(QUESTION + " : " + response);
        }

        String dbpediaResponse = response.getDbpediaResponse();
        Person dbpediaPerson = parseDBPediaResponse(dbpediaResponse);

        String freebaseResponse = response.getFreebaseResponse();
        Person freebasePerson = parseFreebaseResponse(freebaseResponse);
        Map<String, Object> map = new HashMap<>();
        map.put("freebase", freebasePerson);
        map.put("dbpedia", dbpediaPerson);
        return map;
    }

    //region FREEBASE Methods

    private Person parseFreebaseResponse(String freebaseResponse) {
        String personUri = null;

        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode results = mapper.readTree(freebaseResponse).findValue("result");
            if(results.isArray()) {
                for(JsonNode item : results) {
                    personUri = getFreebaseLink(extractFreebaseId(item));
                    if(personUri != null && !personUri.trim().isEmpty()) {
                        break;
                    }
                }
            }


            WebTarget client;
            String personInfoResponse, aux;

            client = ClientBuilder.newClient().target(personUri);
            personInfoResponse = client.request().get(String.class);
            JsonNode personInfo = mapper.readTree(personInfoResponse).findValue("property");
            Person person = new Person();

            aux = getFreebasePersonName(personInfo);
            person.setName(aux);

            aux = getFreebaseBirthdate(personInfo);
            person.setBirthdate(aux);

            aux = getFreebaseDeathdate(personInfo);
            person.setDeathdate(aux);

            person.setBirthplace(getFreebaseBirthplace(personInfo));

            aux = getFreebaseAbstractDescription(personInfo);
            person.setDescription(aux);

            aux = getFreebaseShortDescription(personInfo);
            person.setShortDescription(aux);

            person.setThumbnails(getFreebaseThumbnail(personInfo));

            aux = getFreebasePrimaryTopicOf(personInfo);
            person.setWikiPageExternal(aux);

            person.setEducation(getFreebaseEducation(personInfo));
            person.setNationality(getFreebaseNationality(personInfo));

            person.setParents(getFreebaseParents(personInfo));

            person.setSpouse(getFreebaseSpouse(personInfo));
            person.setChildren(getFreebaseChildren(personInfo));

            return person;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private ArrayList<StringPair> getFreebaseChildren(JsonNode personInfo) {
        ArrayList<StringPair> parentsArray = new ArrayList<>();
        JsonNode parents = personInfo.findValue(MetadataProperties.PARENTS.getFreebase()).findValue("values");
        if(parents != null && parents.isArray()) {
            for (JsonNode parent : parents) {
                if(isEN(parent)) {
                    parentsArray.add(new StringPair(getFreebaseLink(extractFreebaseId(parent)),
                            extractValue(parent.findValue("text"))));
                }
            }
        }
        return parentsArray;

    }

    private ArrayList<StringPair> getFreebaseSpouse(JsonNode personInfo) {
        //"/people/marriage/spouse"
        ArrayList<StringPair> spousesArray = new ArrayList<>();
        JsonNode spouses = personInfo.findValue(MetadataProperties.SPOUSE.getFreebase()).findValue("values");
        JsonNode aux = null;
        if(spouses != null && spouses.isArray()) {
            for (JsonNode spouse : spouses) {
                if (spouse != null) {
                    aux = spouse.findValue("/people/marriage/spouse").findValue("values");
                }

                if (aux != null && aux.isArray()) {
                    for (JsonNode parent : aux) {
                        if (isEN(parent)) {
                            spousesArray.add(new StringPair(getFreebaseLink(extractFreebaseId(parent)),
                                    extractValue(parent.findValue("text"))));
                        }
                    }
                }
            }
        }
        return spousesArray;
    }

    private ArrayList<StringPair> getFreebaseParents(JsonNode personInfo) {
        ArrayList<StringPair> parentsArray = new ArrayList<>();
        JsonNode parents = personInfo.findValue(MetadataProperties.PARENTS.getFreebase()).findValue("values");
        if(parents != null && parents.isArray()) {
            for (JsonNode parent : parents) {
                if(isEN(parent)) {
                    parentsArray.add(new StringPair(getFreebaseLink(extractFreebaseId(parent)),
                            extractValue(parent.findValue("text"))));
                }
            }
        }
        return parentsArray;
    }

    private ArrayList<StringPair> getFreebaseNationality(JsonNode personInfo) {
        ArrayList<StringPair> nationalitiesArray = new ArrayList<>();
        JsonNode nationalities = personInfo.findValue(MetadataProperties.NATIONALITY.getFreebase()).findValue("values");
        if(nationalities != null && nationalities.isArray()) {
            for (JsonNode nationality : nationalities) {
                if(isEN(nationality)) {
                    nationalitiesArray.add(new StringPair(getFreebaseLink(extractFreebaseId(nationality)),
                            extractValue(nationality.findValue("text"))));
                }
            }
        }

        return nationalitiesArray;
    }

    private ArrayList<StringPair> getFreebaseEducation(JsonNode personInfo) {
        JsonNode institutions = personInfo.findValue(MetadataProperties.EDUCATION_ONTOLOGY.getFreebase()).findValue("values");
        JsonNode property, aux;
        ArrayList<StringPair> educationalInstitutions = new ArrayList<>();
        String uri=null, name = null;
        if(institutions != null && institutions.isArray()) {
            for (JsonNode institution : institutions) {
                property = institution.findValue("property");
                if(property != null) {
                    aux = property.findValue(MetadataProperties.EDUCATIONAL_INSTITUTION.getFreebase()).findValue("values");
                    if (aux != null && aux.isArray()) {
                        for (JsonNode value : aux) {
                            if (isEN(value)) {
                                name = extractValue(value.findValue("text"));
                                uri = getFreebaseLink(extractFreebaseId(value));
                            }
                        }
                    }
                    if (name != null) {
                        aux = property.findValue(MetadataProperties.DEGREE.getFreebase());
                        if (aux != null) {
                            aux = aux.findValue("values");
                            if (aux != null && aux.isArray()) {
                                for (JsonNode value : aux) {
                                    if (isEN(value)) {
                                        name += (" - " + extractValue(value.findValue("text")));
                                        break;
                                    }
                                }
                            }
                        }
                        educationalInstitutions.add(new StringPair(uri, name));
                    }
                }
            }
        }
        return  educationalInstitutions;
    }

    private String getFreebasePrimaryTopicOf(JsonNode personInfo) {
        ArrayNode links = (ArrayNode)personInfo.findValue(MetadataProperties.PRIMARY_TOPIC_OF.getFreebase()).findValue("values");
        String aux;
        if(links != null) {
            for (JsonNode topic : links) {
                aux = extractValue(topic.findValue("value"));
                if(aux != null && aux.contains("en.wikipedia")) {
                    return aux;
                }
            }
        }
        return "";

    }

    private ArrayList<String> getFreebaseThumbnail(JsonNode personInfo) {
        ArrayList<String> thumbs = new ArrayList<>();
        ArrayNode thumbnails = (ArrayNode)personInfo.findValue(MetadataProperties.THUMBNAIL.getFreebase()).findValue("values");
        if(thumbnails != null) {
            for (JsonNode thumbnail : thumbnails) {
                thumbs.add(getFreebaseImageLink(extractFreebaseId(thumbnail)));

            }
        }
        return thumbs;

    }

    private String getFreebaseImageLink(String resource) {
        return String.format(FREEBASE_IMAGE_LINK, resource);
    }

    private String getFreebaseShortDescription(JsonNode personInfo) {
        JsonNode values = personInfo.findValue(MetadataProperties.SHORT_DESCRIPTION.getFreebase()).findValue("values");
        if(values != null && values.isArray()) {
            for (JsonNode value : values) {
                if(isEN(value)) {
                    return extractValue(value.findValue("text"));
                }
            }
        }
        return "";
    }

    private String getFreebaseAbstractDescription(JsonNode personInfo) {
        JsonNode values = personInfo.findValue(MetadataProperties.ABSTRACT.getFreebase()).findValue("values");
        if(values != null && values.isArray()) {
            for (JsonNode value : values) {
                if(isEN(value)) {
                    return extractValue(value.findValue("value"));
                }
            }
        }

        return "";
    }

    private StringPair getFreebaseBirthplace(JsonNode personInfo) {
        JsonNode values = personInfo.findValue(MetadataProperties.BIRTHPLACE.getFreebase()).findValue("values");
        if(values != null && values.isArray()) {
            for (JsonNode value : values) {
                if(isEN(value)) {
                    return new StringPair(getFreebaseLink(extractFreebaseId(value)),
                            extractValue(value.findValue("text")));
                }
            }
        }

        return new StringPair();
    }

    private String getFreebaseDeathdate(JsonNode personInfo) {
        JsonNode values = personInfo.findValue(MetadataProperties.DEATHDATE.getFreebase()).findValue("values");
        JsonNode deathDate;
        if(values != null && values.isArray()) {
            for(JsonNode value : values) {
                deathDate = value.findValue("value");
                if(deathDate != null) {
                    deathDate = value.findValue("text");
                }

                if(deathDate != null) {
                    return extractValue(deathDate);
                }
            }
        }

        return "";

    }

    private String getFreebaseBirthdate(JsonNode personInfo) {
        JsonNode values = personInfo.findValue(MetadataProperties.BIRTHDATE.getFreebase()).findValue("values");
        JsonNode birthdate;
        if(values != null && values.isArray()) {
            for(JsonNode value : values) {
                birthdate = value.findValue("value");
                if(birthdate != null) {
                    birthdate = value.findValue("text");
                }

                if(birthdate != null) {
                    return extractValue(birthdate);
                }
            }
        }

        return "";
    }

    private String getFreebasePersonName(JsonNode personInfo) {
        JsonNode values = personInfo.findValue(MetadataProperties.NAME.getFreebase());
        if(values!= null ) {
            values = values.findValue("values");
            JsonNode name;
            if(values != null && values.isArray()) {
                for(JsonNode value : values) {
                    if(isEN(value)) {
                        name = value.findValue("value");
                        if (name != null) {
                            name = value.findValue("text");
                        }

                        if (name != null) {
                            return extractValue(name);
                        }
                    }
                }
            }
        }
        return "";
    }

    private String extractFreebaseId(JsonNode item) {
        JsonNode uris = item.findValue("id");
        if(uris.isArray()) {
            for (JsonNode uri : uris) {// "type":
                return extractValue(uri.findValue("value"));
            }
        } else {
            return extractValue(uris);
        }

        return "";

    }

    private String getFreebaseLink(String resource) {
        return String.format(FREEBASE_RESOURCE_LINK, resource);
    }

    //endregion

    //region DBPEDIA Methods

    private Person parseDBPediaResponse(String dbpediaResponse) {
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

                aux = getDBpediaPersonName(personInfo);
                person.setName(aux);

                aux = getDBpediaBirthdate(personInfo);
                person.setBirthdate(aux);

                aux = getDBpediaDeathdate(personInfo);
                person.setDeathdate(aux);

                person.setBirthplace(getDBpediaBirthplace(personInfo));

                aux = getDBpediaAbstractDescription(personInfo);
                person.setDescription(aux);

                aux = getDBpediaShortDescription(personInfo);
                person.setShortDescription(aux);

                aux = getDBpediaPrimaryTopicOf(personInfo);
                person.setWikiPageExternal(aux);

                person.setEducation(getDBpediaEducation(personInfo));
                person.setNationality(getDBPediaNationality(personInfo));

                person.setParents(getDBpediaParents(personInfo));
                person.setThumbnails(getDBpediaThumbnail(personInfo));
                person.setSpouse(getDBpediaSpouse(personInfo));
                person.setChildren(getDBpediaChildren(personInfo));

                return person;

            }catch (IOException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    private ArrayList<StringPair> getDBpediaChildren(JsonNode personInfo) {
        ArrayList<StringPair> children = new ArrayList<>();
        ArrayNode childrenArray = (ArrayNode)personInfo.findValue(MetadataProperties.CHILDREN.getDbpedia());
        if (childrenArray != null) {
            for(JsonNode child : childrenArray) {
                if (isLiteral(child)) {
                    children.add(new StringPair("", extractValue(child.findValue("value"))));
                } else if(isUri(child)) {
                    String uri = extractValue(child.findValue("value"));
                    String[] pieces = uri.split("/");
                    String spouseName = pieces[pieces.length - 1];
                    children.add(new StringPair(uri, spouseName.replace("_", " ")));
                }
            }
        }

        return children;

    }

    private ArrayList<StringPair> getDBpediaSpouse(JsonNode personInfo) {
        ArrayList<StringPair> parents = new ArrayList<>();
        ArrayNode spouses = (ArrayNode)personInfo.findValue(MetadataProperties.SPOUSE.getDbpedia());
        if (spouses != null) {
            for(JsonNode spouse : spouses) {
                if (isLiteral(spouse)) {
                    parents.add(new StringPair("", extractValue(spouse.findValue("value"))));
                } else if(isUri(spouse)) {
                    String uri = extractValue(spouse.findValue("value"));
                    String[] pieces = uri.split("/");
                    String spouseName = pieces[pieces.length - 1];
                    parents.add(new StringPair(uri, spouseName.replace("_", " ")));
                }
            }
        }

        return parents;

    }

    private ArrayList<String> getDBpediaThumbnail(JsonNode personInfo) {
        ArrayList<String> thumbs = new ArrayList<>();
        ArrayNode thumbnails = (ArrayNode)personInfo.findValue(MetadataProperties.THUMBNAIL.getDbpedia());
        if(thumbnails != null) {
            for (JsonNode thumbnail : thumbnails) {
                if (isUri(thumbnail)) {
                    thumbs.add(extractValue(thumbnails.findValue("value")));
                }
            }
        }
        return thumbs;

    }

    private ArrayList<StringPair> getDBpediaParents(JsonNode personInfo) {
        ArrayList<StringPair> parents = new ArrayList<>();
        ArrayNode parentsArray = (ArrayNode)personInfo.findValue(MetadataProperties.PARENTS.getDbpedia());
        if (parentsArray != null) {
            for(JsonNode parent : parentsArray) {
                if (isLiteral(parent)) {
                    parents.add(new StringPair("", extractValue(parent.findValue("value"))));
                } else if(isUri(parent)) {
                    String uri = extractValue(parent.findValue("value"));
                    String[] pieces = uri.split("/");
                    String parentName = pieces[pieces.length - 1];
                    parents.add(new StringPair(uri, parentName.replace("_", " ")));
                }
            }
        }

        return parents;

    }

    private ArrayList<StringPair> getDBPediaNationality(JsonNode personInfo) {
        ArrayList<StringPair> nationalitiesArray = new ArrayList<>();
        ArrayNode nationalities = (ArrayNode)personInfo.findValue(MetadataProperties.NATIONALITY.getDbpedia());
        if (nationalities != null) {
            for(JsonNode nationality : nationalities) {
                if (isLiteral(nationality)) {
                    nationalitiesArray.add( new StringPair("", extractValue(nationality.findValue("value"))));
                } else if(isUri(nationality)) {
                    String uri = extractValue(nationality.findValue("value"));
                    String[] pieces = uri.split("/");
                    String name = pieces[pieces.length - 1];
                    nationalitiesArray.add(new StringPair(uri, name.replace("_", " ")));
                }
            }
        }

        return nationalitiesArray;

    }

    private ArrayList<StringPair> getDBpediaEducation(JsonNode personInfo) {
        ArrayNode educations = (ArrayNode) personInfo.findValue(MetadataProperties.EDUCATION_ONTOLOGY.getDbpedia());
        ArrayList<StringPair> institutions = new ArrayList<>();
        if(educations != null) {
            for(JsonNode educationNode : educations) {
                if(isLiteral(educationNode)) {
                    institutions.add( new StringPair("", extractValue(educationNode.findValue("value"))));
                } else if(isUri(educationNode)) {
                    String uri = extractValue(educationNode.findValue("value"));
                    String[] pieces = uri.split("/");
                    String education = pieces[pieces.length - 1];
                    institutions.add( new StringPair(uri, education.replace("_", " ")));
                }
            }
        }

        educations = (ArrayNode) personInfo.findValue(MetadataProperties.ALMA_MATER.getDbpedia());
        if(educations != null) {
            for(JsonNode educationNode : educations) {
                if(isLiteral(educationNode)) {
                    institutions.add( new StringPair("", extractValue(educationNode.findValue("value"))));
                } else if(isUri(educationNode)) {
                    String uri = extractValue(educationNode.findValue("value"));
                    String[] pieces = uri.split("/");
                    String education = pieces[pieces.length - 1];
                    institutions.add( new StringPair(uri, education.replace("_", " ")));
                }
            }
        }

        return institutions;

    }

    private String getDBpediaPrimaryTopicOf(JsonNode personInfo) {
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

    private String getDBpediaShortDescription(JsonNode personInfo) {
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

    private String getDBpediaAbstractDescription(JsonNode personInfo) {
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

    private String getDBpediaDeathdate(JsonNode personInfo) {
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

    private StringPair getDBpediaBirthplace(JsonNode personInfo) {
        ArrayNode places = (ArrayNode)personInfo.findValue(MetadataProperties.BIRTHPLACE.getDbpedia());
        if(places != null) {
            for (JsonNode birthplace : places) {
                if (isLiteral(birthplace)) {
                    return new StringPair("", extractValue(birthplace.findValue("value")));
                } else if (isUri(birthplace)) {
                    String uri = extractValue(birthplace.findValue("value"));
                    String[] pieces = uri.split("/");
                    String birthPlace = pieces[pieces.length - 1];
                    return new StringPair(uri, birthPlace.replace("_", " "));
                }
            }
        }
        return null;
    }

    private String getDBpediaBirthdate(JsonNode personInfo) {
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

    private String getDBpediaPersonName(JsonNode personInfo) {
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
        if(node != null) {
            return node.toString().replace("\"", "");
        } else {
            return  "";
        }

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

    //endregion
}
