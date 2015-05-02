package ro.semanticwebsearch.responsegenerator.parser.helper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import ro.semanticwebsearch.responsegenerator.model.Casualty;
import ro.semanticwebsearch.responsegenerator.model.StringPair;

import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * Created by Spac on 4/25/2015.
 */
public class FreebasePropertyExtractor {

    private static final String KEY = "AIzaSyDgPg3TRQ2Fi4ccOyX26qAbU70vdw6UUks";

    public static ArrayList<StringPair> getChildren(JsonNode personInfo) {
        ArrayList<StringPair> parentsArray = new ArrayList<>();
        JsonNode children = personInfo.findValue(MetadataProperties.PARENTS.getFreebase());
        if (children == null) {
            return null;
        }

        children = children.findValue("values");

        if (children != null && children.isArray()) {
            for (JsonNode parent : children) {
                if (DBPediaPropertyExtractor.isEN(parent)) {
                    parentsArray.add(extractStringPair(AdditionalQuestion.WHO_IS, parent.findValue("text")));
                }
            }
        }
        return parentsArray;

    }

    public static ArrayList<StringPair> getSpouse(JsonNode personInfo) {
        //"/people/marriage/spouse"
        ArrayList<StringPair> spousesArray = new ArrayList<>();
        JsonNode spouses = personInfo.findValue(MetadataProperties.SPOUSE.getFreebase());

        if (spouses == null) {
            return null;
        }

        spouses = spouses.findValue("values");

        JsonNode aux = null;
        if (spouses != null && spouses.isArray()) {
            for (JsonNode spouse : spouses) {
                if (spouse != null) {
                    aux = spouse.findValue("/people/marriage/spouse").findValue("values");
                }

                if (aux != null && aux.isArray()) {
                    for (JsonNode parent : aux) {
                        if (DBPediaPropertyExtractor.isEN(parent)) {
                            spousesArray.add(extractStringPair(AdditionalQuestion.WHO_IS, parent.findValue("text")));
                        }
                    }
                }
            }
        }
        return spousesArray;
    }

    public static ArrayList<StringPair> getParents(JsonNode personInfo) {
        ArrayList<StringPair> parentsArray = new ArrayList<>();
        JsonNode parents = personInfo.findValue(MetadataProperties.PARENTS.getFreebase());

        if (parents == null) {
            return null;
        }

        parents = parents.findValue("values");

        if (parents != null && parents.isArray()) {
            for (JsonNode parent : parents) {
                if (DBPediaPropertyExtractor.isEN(parent)) {
                    parentsArray.add(extractStringPair(AdditionalQuestion.WHO_IS, parent.findValue("text")));
                }
            }
        }
        return parentsArray;
    }

    public static ArrayList<StringPair> getNationality(JsonNode personInfo) {
        ArrayList<StringPair> nationalitiesArray = new ArrayList<>();
        JsonNode nationalities = personInfo.findValue(MetadataProperties.NATIONALITY.getFreebase());

        if (nationalities == null) {
            return null;
        }

        nationalities = nationalities.findValue("values");

        if (nationalities != null && nationalities.isArray()) {
            for (JsonNode nationality : nationalities) {
                if (DBPediaPropertyExtractor.isEN(nationality)) {
                    nationalitiesArray.add(extractStringPair(AdditionalQuestion.PLACE_INFO, nationality.findValue("text")));
                }
            }
        }

        return nationalitiesArray;
    }

    public static ArrayList<StringPair> getEducation(JsonNode personInfo) {
        JsonNode institutions = personInfo.findValue(MetadataProperties.EDUCATION_ONTOLOGY.getFreebase());

        if (institutions == null) {
            return null;
        }

        institutions = institutions.findValue("values");

        JsonNode property, aux;
        ArrayList<StringPair> educationalInstitutions = new ArrayList<>();
        String uri = null, name = null;
        if (institutions != null && institutions.isArray()) {
            for (JsonNode institution : institutions) {
                property = institution.findValue("property");
                if (property != null) {
                    aux = property.findValue(MetadataProperties.EDUCATIONAL_INSTITUTION.getFreebase()).findValue("values");
                    if (aux != null && aux.isArray()) {
                        for (JsonNode value : aux) {
                            if (DBPediaPropertyExtractor.isEN(value)) {
                                name = DBPediaPropertyExtractor.extractValue(value.findValue("text"));
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
                                    if (DBPediaPropertyExtractor.isEN(value)) {
                                        name += (" - " + DBPediaPropertyExtractor.extractValue(value.findValue("text")));
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
        return educationalInstitutions;
    }

    public static String getPrimaryTopicOf(JsonNode personInfo) {
        JsonNode links = personInfo.findValue(MetadataProperties.PRIMARY_TOPIC_OF.getFreebase());

        if (links == null) {
            return "";
        }

        links = links.findValue("values");

        String aux;
        if (links != null) {
            for (JsonNode topic : links) {
                aux = DBPediaPropertyExtractor.extractValue(topic.findValue("value"));
                if (aux != null && aux.contains("en.wikipedia")) {
                    return aux;
                }
            }
        }
        return "";

    }

    public static ArrayList<String> getThumbnail(JsonNode personInfo) {
        ArrayList<String> thumbs = new ArrayList<>();
        JsonNode thumbnails = personInfo.findValue(MetadataProperties.THUMBNAIL.getFreebase());

        if (thumbnails == null) {
            return null;
        }

        thumbnails = thumbnails.findValue("values");

        if (thumbnails != null) {
            for (JsonNode thumbnail : thumbnails) {
                thumbs.add(getImageLink(extractFreebaseId(thumbnail)));

            }
        }
        return thumbs;

    }

    public static String getImageLink(String resource) {
        return String.format(Constants.FREEBASE_IMAGE_LINK.getValue(), resource);
    }

    public static String getShortDescription(JsonNode personInfo) {
        JsonNode values = personInfo.findValue(MetadataProperties.SHORT_DESCRIPTION.getFreebase());
        if (values == null) {
            return "";
        }

        values = values.findValue("values");

        if (values != null && values.isArray()) {
            for (JsonNode value : values) {
                if (DBPediaPropertyExtractor.isEN(value)) {
                    return DBPediaPropertyExtractor.extractValue(value.findValue("text"));
                }
            }
        }
        return "";
    }

    public static String getAbstractDescription(JsonNode personInfo) {
        JsonNode values = personInfo.findValue(MetadataProperties.ABSTRACT.getFreebase()).findValue("values");
        if (values != null && values.isArray()) {
            for (JsonNode value : values) {
                if (DBPediaPropertyExtractor.isEN(value)) {
                    return DBPediaPropertyExtractor.extractValue(value.findValue("value"));
                }
            }
        }

        return "";
    }

    public static StringPair getBirthplace(JsonNode personInfo) {
        JsonNode values = personInfo.findValue(MetadataProperties.BIRTHPLACE.getFreebase()).findValue("values");
        if (values != null && values.isArray()) {
            for (JsonNode value : values) {
                if (DBPediaPropertyExtractor.isEN(value)) {
                    return extractStringPair(AdditionalQuestion.PLACE_INFO, value.findValue("text"));
                }
            }
        }

        return new StringPair();
    }

    public static String getDeathdate(JsonNode personInfo) {
        JsonNode values = personInfo.findValue(MetadataProperties.DEATHDATE.getFreebase());
        if (values == null) {
            return "";
        }

        values = values.findValue("values");
        JsonNode deathDate;
        if (values != null && values.isArray()) {
            for (JsonNode value : values) {
                deathDate = value.findValue("value");
                if (deathDate != null) {
                    deathDate = value.findValue("text");
                }

                if (deathDate != null) {
                    return DBPediaPropertyExtractor.extractValue(deathDate);
                }
            }
        }

        return "";

    }

    public static String getBirthdate(JsonNode personInfo) {
        JsonNode values = personInfo.findValue(MetadataProperties.BIRTHDATE.getFreebase());

        if (values == null) {
            return "";
        }

        values = values.findValue("values");
        JsonNode birthdate;
        if (values != null && values.isArray()) {
            for (JsonNode value : values) {
                birthdate = value.findValue("value");
                if (birthdate != null) {
                    birthdate = value.findValue("text");
                }

                if (birthdate != null) {
                    return DBPediaPropertyExtractor.extractValue(birthdate);
                }
            }
        }

        return "";
    }

    public static String getPersonName(JsonNode personInfo) {
        JsonNode values = personInfo.findValue(MetadataProperties.NAME.getFreebase());
        if (values != null) {
            values = values.findValue("values");
            JsonNode name;
            if (values != null && values.isArray()) {
                for (JsonNode value : values) {
                    if (DBPediaPropertyExtractor.isEN(value)) {
                        name = value.findValue("value");
                        if (name != null) {
                            name = value.findValue("text");
                        }

                        if (name != null) {
                            return DBPediaPropertyExtractor.extractValue(name);
                        }
                    }
                }
            }
        }
        return "";
    }

    public static String extractFreebaseId(JsonNode item) {
        JsonNode uris = item.findValue("id");
        if (uris != null && uris.isArray()) {
            for (JsonNode uri : uris) {// "type":
                return DBPediaPropertyExtractor.extractValue(uri.findValue("value"));
            }
        } else {
            return DBPediaPropertyExtractor.extractValue(uris);
        }

        return "";

    }

    public static String getFreebaseLink(String resource) {
        return String.format(Constants.FREEBASE_RESOURCE_LINK.getValue(), resource, KEY);
    }

    public static String getEventDate(JsonNode conflictInfo) {
        return getStartDate(conflictInfo) + " - " + getEndDate(conflictInfo);
    }

    public static String getStartDate(JsonNode info) {
        JsonNode values = info.findValue(MetadataProperties.START_DATE.getFreebase());

        if (values == null) {
            return "";
        }

        values = values.findValue("values");
        JsonNode birthdate;
        if (values != null && values.isArray()) {
            for (JsonNode value : values) {
                birthdate = value.findValue("value");
                if (birthdate != null) {
                    birthdate = value.findValue("text");
                }

                if (birthdate != null) {
                    return DBPediaPropertyExtractor.extractValue(birthdate);
                }
            }
        }

        return "";
    }

    public static String getEndDate(JsonNode info) {
        JsonNode values = info.findValue(MetadataProperties.END_DATE.getFreebase());

        if (values == null) {
            return "";
        }

        values = values.findValue("values");
        JsonNode birthdate;
        if (values != null && values.isArray()) {
            for (JsonNode value : values) {
                birthdate = value.findValue("value");
                if (birthdate != null) {
                    birthdate = value.findValue("text");
                }

                if (birthdate != null) {
                    return DBPediaPropertyExtractor.extractValue(birthdate);
                }
            }
        }

        return "";
    }

    public static ArrayList<StringPair> getPartOf(JsonNode personInfo) {
        ArrayList<StringPair> thumbs = new ArrayList<>();
        JsonNode thumbnails = personInfo.findValue(MetadataProperties.PART_OF.getFreebase());
        String aux;
        if (thumbnails == null) {
            return null;
        }

        thumbnails = thumbnails.findValue("values");

        if (thumbnails != null) {
            for (JsonNode thumbnail : thumbnails) {
                aux = DBPediaPropertyExtractor.extractValue(thumbnail.findValue("text"));
                thumbs.add(new StringPair(DBPediaPropertyExtractor.getLink(AdditionalQuestion.CONFLICT, aux), aux));

            }
        }
        return thumbs;

    }

    public static ArrayList<StringPair> getEventLocations(JsonNode conflictInfo) {
        ArrayList<StringPair> locations = new ArrayList<>();
        JsonNode locationsNode = conflictInfo.findValue(MetadataProperties.PLACE.getFreebase());

        if (locationsNode == null) {
            return null;
        }

        locationsNode = locationsNode.findValue("values");

        if (locationsNode != null) {
            for (JsonNode location : locationsNode) {
                locations.add(extractStringPair(AdditionalQuestion.PLACE_INFO, location.findValue("text")));

            }
        }
        return locations;
    }

    public static ArrayList<StringPair> getCommanders(JsonNode conflictInfo) {
        JsonNode property, aux;
        ArrayList<StringPair> commandersArray = new ArrayList<>();
        JsonNode commanders = conflictInfo.findValue(MetadataProperties.COMMANDERS.getFreebase());

        if (commanders == null) {
            return null;
        }

        commanders = commanders.findValue("values");

        if (commanders != null && commanders.isArray()) {
            for (JsonNode commander : commanders) {
                property = commander.findValue("property");
                if (property != null) {
                    aux = property.findValue(MetadataProperties.MILITARY_COMMANDER.getFreebase()).findValue("values");
                    if (aux != null && aux.isArray()) {
                        for (JsonNode value : aux) {
                            if (DBPediaPropertyExtractor.isEN(value)) {
                                commandersArray.add(new StringPair(DBPediaPropertyExtractor.getLink(AdditionalQuestion.WHO_IS,
                                        DBPediaPropertyExtractor.extractValue(value.findValue("text"))),
                                        DBPediaPropertyExtractor.extractValue(value.findValue("text"))));
                            }
                        }
                    }

                }
            }
        }

        return commandersArray;
    }

    public static ArrayList<StringPair> getCombatants(JsonNode conflictInfo) {
        ArrayList<String> properties = new ArrayList<>();
        properties.add(MetadataProperties.MILITARY_CONFLICT_COMBATANTS.getFreebase());
        properties.add(MetadataProperties.MILITARY_COMBATANT_GROUP_COMBATANTS.getFreebase());
        properties.add("text");

        ArrayList<JsonNode> combatants = getDeepProperties(properties, conflictInfo);

        return extractStringPair(AdditionalQuestion.PLACE_INFO, combatants);
    }

    public static ArrayList<StringPair> extractStringPair(AdditionalQuestion question, ArrayList<JsonNode> node) {
        return node.stream()
                .map(item -> {
                    String extractedValue = DBPediaPropertyExtractor.extractValue(item);
                    return new StringPair(DBPediaPropertyExtractor.getLink(question, extractedValue), extractedValue);
                })
                .collect(Collectors.toCollection(ArrayList<StringPair>::new));
    }

    public static StringPair extractStringPair(AdditionalQuestion question, JsonNode node) {
        String extractedValue = DBPediaPropertyExtractor.extractValue(node);
        return new StringPair(DBPediaPropertyExtractor.getLink(question, extractedValue), extractedValue);
    }

    public static StringPair extractStringPair(AdditionalQuestion question, String node) {
        return new StringPair(DBPediaPropertyExtractor.getLink(question, node), node);
    }

    public static ArrayList<Casualty> getCasualties(JsonNode conflictInfo) {
        Casualty aux;
        String stringAux;
        ArrayList<String> properties = new ArrayList<>();
        ArrayList<Casualty> response = new ArrayList<>();
        ArrayList<String> sameLevelProperties = new ArrayList<>();

        properties.add(MetadataProperties.CASUALTIES.getFreebase());
        properties.add("values");

        sameLevelProperties.add(MetadataProperties.CASUALTIES_COMBATANT.getFreebase());
        sameLevelProperties.add(MetadataProperties.CASUALTIES_ESTIMATE.getFreebase());
        sameLevelProperties.add(MetadataProperties.CASUALTY_TYPE.getFreebase());

        ArrayList<JsonNode> casualties = getDeepProperties(properties, conflictInfo);
        ArrayList<JsonNode> same;
        for (JsonNode casualty : casualties) {
            if (casualty.isArray()) {
                for (JsonNode item : casualty) {
                    same = getSameLevelProperties(sameLevelProperties, item);

                    aux = new Casualty();
                    stringAux = DBPediaPropertyExtractor.extractValue(getDeepProperties("text", same.get(0)).get(0));
                    aux.setCombatant(new StringPair(DBPediaPropertyExtractor.getLink(AdditionalQuestion.WHO_IS, stringAux), stringAux));

                    stringAux = DBPediaPropertyExtractor.extractValue(getDeepProperties("text", same.get(1)).get(0));
                    aux.setCasualties(stringAux);

                    stringAux = DBPediaPropertyExtractor.extractValue(getDeepProperties("text", same.get(2)).get(0));
                    aux.setCasualtyType(stringAux);

                    response.add(aux);
                }
            } else {
                same = getSameLevelProperties(sameLevelProperties, casualty);

                aux = new Casualty();
                stringAux = DBPediaPropertyExtractor.extractValue(getDeepProperties("text", same.get(0)).get(0));
                aux.setCombatant(new StringPair(DBPediaPropertyExtractor.getLink(AdditionalQuestion.WHO_IS, stringAux), stringAux));

                stringAux = DBPediaPropertyExtractor.extractValue(getDeepProperties("text", same.get(1)).get(0));
                aux.setCasualties(stringAux);

                stringAux = DBPediaPropertyExtractor.extractValue(getDeepProperties("text", same.get(2)).get(0));
                aux.setCasualtyType(stringAux);

                response.add(aux);
            }

        }

        return response;
    }

    public static ArrayList<JsonNode> getDeepProperties(String prop, JsonNode node) {
        ArrayList<JsonNode> response = new ArrayList<>();
        if (node.has(prop)) {
            response.add(node.findValue(prop));
            return response;
        } else if (node.has("values")) {
            ArrayNode values = (ArrayNode) node.findValue("values");
            for (JsonNode value : values) {
                response.addAll(getDeepProperties(prop, value));
            }
        } else if (node.has("property")) {
            response.addAll(getDeepProperties(prop, node.findValue("property")));
        }

        return response;

    }

    public static ArrayList<JsonNode> getDeepProperties(ArrayList<String> props, JsonNode node) {
        ArrayList<JsonNode> response = new ArrayList<>();
        ArrayList<JsonNode> aux2;
        response.add(node);
        for (String prop : props) {
            aux2 = new ArrayList<>();
            for (JsonNode item : response) {
                aux2.addAll(getDeepProperties(prop, item));
            }
            response = new ArrayList<>();
            response.addAll(aux2);
        }

        return response;

    }

    public static ArrayList<JsonNode> getSameLevelProperties(ArrayList<String> props, JsonNode node) {
        ArrayList<JsonNode> response = new ArrayList<>();
        for (String prop : props) {
            if (node.has(prop)) {
                response.add(node.findValue(prop));

            } else if (node.has("values")) {
                ArrayNode values = (ArrayNode) node.findValue("values");
                for (JsonNode value : values) {
                    if (value.has(prop)) {
                        response.add(value.findValue(prop));

                    }
                }
            } else if (node.has("property")) {
                node = node.findValue("property");
                if (node.has(prop)) {
                    response.add(node.findValue(prop));

                }
            }
        }

        return response;
    }
}
