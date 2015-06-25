package ro.semanticwebsearch.responsegenerator.parser.helper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import ro.semanticwebsearch.responsegenerator.model.Casualty;
import ro.semanticwebsearch.responsegenerator.model.Geolocation;
import ro.semanticwebsearch.responsegenerator.model.StringPair;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Spac on 4/25/2015.
 */
public class FreebasePropertyExtractor {

    private static final String KEY = "AIzaSyDgPg3TRQ2Fi4ccOyX26qAbU70vdw6UUks";

    public static ArrayList<StringPair> getChildren(JsonNode personInfo) {
        ArrayList<StringPair> parentsArray = new ArrayList<>();
        JsonNode children = personInfo.path(MetadataProperties.CHILDREN.getFreebase()).path("values");
        if (isMissingNode(children)) {
            return null;
        }

        if (children.isArray()) {
            for (JsonNode parent : children) {
                if (DBPediaPropertyExtractor.isEN(parent)) {
                    parentsArray.add(extractStringPair(AdditionalQuestion.WHO_IS, parent.get("text")));
                }
            }
        }
        return parentsArray;

    }

    public static boolean isMissingNode(JsonNode node) {
        return node == null || node.getNodeType() == JsonNodeType.MISSING;
    }

    public static ArrayList<StringPair> getSpouse(JsonNode personInfo) {
        ArrayList<StringPair> spousesArray = new ArrayList<>();
        JsonNode spouses = personInfo.path(MetadataProperties.SPOUSE.getFreebase()).path("values");

        if (isMissingNode(spouses)) {
            return null;
        }

        JsonNode aux = null;
        if (spouses.isArray()) {
            for (JsonNode spouse : spouses) {
                if (spouse != null) {
                    aux = spouse.path("property").path("/people/marriage/spouse").path("values");
                }

                if (!isMissingNode(aux) && aux.isArray()) {
                    for (JsonNode parent : aux) {
                        if (DBPediaPropertyExtractor.isEN(parent)) {
                            spousesArray.add(extractStringPair(AdditionalQuestion.WHO_IS, parent.get("text")));
                        }
                    }
                }
            }
        }
        return spousesArray;
    }

    public static ArrayList<StringPair> getParents(JsonNode personInfo) {
        ArrayList<StringPair> parentsArray = new ArrayList<>();
        JsonNode parents = personInfo.path(MetadataProperties.PARENTS.getFreebase()).path("values");

        if (isMissingNode(parents)) {
            return null;
        }

        if (parents.isArray()) {
            for (JsonNode parent : parents) {
                if (DBPediaPropertyExtractor.isEN(parent)) {
                    parentsArray.add(extractStringPair(AdditionalQuestion.WHO_IS, parent.get("text")));
                }
            }
        }
        return parentsArray;
    }

    public static ArrayList<StringPair> getNationality(JsonNode personInfo) {
        ArrayList<StringPair> nationalitiesArray = new ArrayList<>();
        JsonNode nationalities = personInfo.path(MetadataProperties.NATIONALITY.getFreebase()).path("values");

        if (isMissingNode(nationalities)) {
            return null;
        }

        if (nationalities.isArray()) {
            for (JsonNode nationality : nationalities) {
                if (DBPediaPropertyExtractor.isEN(nationality)) {
                    nationalitiesArray.add(extractStringPair(AdditionalQuestion.LOCATION_INFO, nationality.get("text")));
                }
            }
        }

        return nationalitiesArray;
    }

    public static ArrayList<StringPair> getEducation(JsonNode personInfo) {
        JsonNode institutions = personInfo.path(MetadataProperties.EDUCATION_ONTOLOGY.getFreebase())
                .path("values");

        if (isMissingNode(institutions)) {
            return null;
        }

        JsonNode property, aux;
        ArrayList<StringPair> educationalInstitutions = new ArrayList<>();
        String uri = null, name = null;
        if (institutions.isArray()) {
            for (JsonNode institution : institutions) {
                property = institution.get("property");
                if (property != null) {
                    aux = property.get(MetadataProperties.EDUCATIONAL_INSTITUTION.getFreebase());
                    if(aux != null) {
                        aux = aux.get("values");
                    }
                    if (aux != null && aux.isArray()) {
                        for (JsonNode value : aux) {
                            if (DBPediaPropertyExtractor.isEN(value)) {
                                name = DBPediaPropertyExtractor.extractValue(value.get("text"));
                                uri = DBPediaPropertyExtractor.getLink(AdditionalQuestion.EDUCATION_INFO,
                                        name);
                            }
                        }
                    }
                    if (name != null) {
                        aux = property.get(MetadataProperties.DEGREE.getFreebase());
                        if (aux != null) {
                            aux = aux.get("values");
                            if (aux != null && aux.isArray()) {
                                for (JsonNode value : aux) {
                                    if (DBPediaPropertyExtractor.isEN(value)) {
                                        name += (" - " + DBPediaPropertyExtractor.extractValue(value.get("text")));
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
        JsonNode links = personInfo.path(MetadataProperties.PRIMARY_TOPIC_OF.getFreebase())
                .path("values");

        if (isMissingNode(links)) {
            return "";
        }
        String aux;
        if (links != null) {
            for (JsonNode topic : links) {
                aux = DBPediaPropertyExtractor.extractValue(topic.get("value"));
                if (aux != null && aux.contains("en.wikipedia")) {
                    return aux;
                }
            }
        }
        return "";

    }

    public static ArrayList<String> getThumbnail(JsonNode personInfo) {
        ArrayList<String> thumbs = new ArrayList<>();
        JsonNode thumbnails = personInfo.path(MetadataProperties.THUMBNAIL.getFreebase()).path("values");

        if (isMissingNode(thumbnails)) {
            return null;
        } else {
            for (JsonNode thumbnail : thumbnails) {
                thumbs.add(getImageLink(extractFreebaseId(thumbnail)));

            }
        }
        return thumbs;

    }

    public static String getImageLink(String resource) {
        return String.format(Constants.FREEBASE_IMAGE_LINK, resource);
    }

    public static String getShortDescription(JsonNode personInfo) {
        JsonNode values = personInfo.path(MetadataProperties.SHORT_DESCRIPTION.getFreebase()).path("values");
        if (isMissingNode(values)) {
            return "";
        }

        if ( values.isArray()) {
            for (JsonNode value : values) {
                if (DBPediaPropertyExtractor.isEN(value)) {
                    return DBPediaPropertyExtractor.extractValue(value.get("text"));
                }
            }
        }
        return "";
    }

    public static String getAbstractDescription(JsonNode personInfo) {
        JsonNode values = personInfo.path(MetadataProperties.ABSTRACT.getFreebase()).path("values");
        if (!isMissingNode(values)&& values.isArray()) {
            for (JsonNode value : values) {
                if (DBPediaPropertyExtractor.isEN(value)) {
                    return DBPediaPropertyExtractor.extractValue(value.get("value"));
                }
            }
        }

        return "";
    }

    public static StringPair getBirthplace(JsonNode personInfo) {
        JsonNode values = personInfo.get(MetadataProperties.BIRTHPLACE.getFreebase());

        if(values != null) {
            values = values.get("values");
        }

        if (values != null && values.isArray()) {
            for (JsonNode value : values) {
                if (!value.get("text").asText().isEmpty() && DBPediaPropertyExtractor.isEN(value)) {
                    return extractStringPair(AdditionalQuestion.LOCATION_INFO, value.get("text"));
                }
            }
        }

        return null;
    }

    public static String getDeathdate(JsonNode personInfo) {
        JsonNode values = personInfo.get(MetadataProperties.DEATHDATE.getFreebase());
        if (values == null) {
            return "";
        }

        values = values.get("values");
        JsonNode deathDate;
        if (values != null && values.isArray()) {
            for (JsonNode value : values) {
                deathDate = value.get("value");
                if (deathDate != null) {
                    deathDate = value.get("text");
                }

                if (deathDate != null) {
                    return DBPediaPropertyExtractor.extractValue(deathDate);
                }
            }
        }

        return "";

    }

    public static String getBirthdate(JsonNode personInfo) {
        JsonNode values = personInfo.get(MetadataProperties.BIRTHDATE.getFreebase());

        if (values == null) {
            return "";
        }

        values = values.get("values");
        JsonNode birthdate;
        if (values != null && values.isArray()) {
            for (JsonNode value : values) {
                birthdate = value.get("value");
                if (birthdate != null) {
                    birthdate = value.get("text");
                }

                if (birthdate != null) {
                    return DBPediaPropertyExtractor.extractValue(birthdate);
                }
            }
        }

        return "";
    }

    public static String getPersonName(JsonNode personInfo) {
        JsonNode values = personInfo.get(MetadataProperties.NAME.getFreebase());
        if (values != null) {
            values = values.get("values");
            JsonNode name;
            if (values != null && values.isArray()) {
                for (JsonNode value : values) {
                    if (DBPediaPropertyExtractor.isEN(value)) {
                        name = value.get("value");
                        if (name != null) {
                            name = value.get("text");
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
        JsonNode uris = item.get("id");
        if (uris != null && uris.isArray()) {
            for (JsonNode uri : uris) {// "type":
                return DBPediaPropertyExtractor.extractValue(uri.get("value"));
            }
        } else {
            return DBPediaPropertyExtractor.extractValue(uris);
        }

        return "";

    }

    public static String getFreebaseLink(String resource) {
        return String.format(Constants.FREEBASE_RESOURCE_LINK, resource, KEY);
    }

    public static String getEventDate(JsonNode conflictInfo) {
        return getStartDate(conflictInfo) + " - " + getEndDate(conflictInfo);
    }

    public static String getStartDate(JsonNode info) {
        JsonNode values = info.get(MetadataProperties.START_DATE.getFreebase());

        if (values == null) {
            return "";
        }

        values = values.get("values");
        JsonNode birthdate;
        if (values != null && values.isArray()) {
            for (JsonNode value : values) {
                birthdate = value.get("value");
                if (birthdate != null) {
                    birthdate = value.get("text");
                }

                if (birthdate != null) {
                    return DBPediaPropertyExtractor.extractValue(birthdate);
                }
            }
        }

        return "";
    }

    public static String getEndDate(JsonNode info) {
        JsonNode values = info.get(MetadataProperties.END_DATE.getFreebase());

        if (values == null) {
            return "";
        }

        values = values.get("values");
        JsonNode birthdate;
        if (values != null && values.isArray()) {
            for (JsonNode value : values) {
                birthdate = value.get("value");
                if (birthdate != null) {
                    birthdate = value.get("text");
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
        JsonNode thumbnails = personInfo.get(MetadataProperties.PART_OF.getFreebase());
        String aux;
        if (thumbnails == null) {
            return null;
        }

        thumbnails = thumbnails.get("values");

        if (thumbnails != null) {
            for (JsonNode thumbnail : thumbnails) {
                aux = DBPediaPropertyExtractor.extractValue(thumbnail.get("text"));
                thumbs.add(new StringPair(DBPediaPropertyExtractor.getLink(AdditionalQuestion.CONFLICT, aux), aux));

            }
        }
        return thumbs;

    }

    public static ArrayList<StringPair> getEventLocations(JsonNode conflictInfo) {
        ArrayList<StringPair> locations = new ArrayList<>();
        JsonNode locationsNode = conflictInfo.get(MetadataProperties.PLACE.getFreebase());

        if (locationsNode == null) {
            return null;
        }

        locationsNode = locationsNode.get("values");

        if (locationsNode != null) {
            for (JsonNode location : locationsNode) {
                locations.add(extractStringPair(AdditionalQuestion.LOCATION_INFO, location.get("text")));

            }
        }
        return locations;
    }

    public static ArrayList<StringPair> getCommanders(JsonNode conflictInfo) {
        JsonNode property, aux;
        ArrayList<StringPair> commandersArray = new ArrayList<>();
        JsonNode commanders = conflictInfo.get(MetadataProperties.COMMANDERS.getFreebase());

        if (commanders == null) {
            return null;
        }

        commanders = commanders.get("values");

        if (commanders != null && commanders.isArray()) {
            for (JsonNode commander : commanders) {
                property = commander.get("property");
                if (property != null) {
                    aux = property.get(MetadataProperties.MILITARY_COMMANDER.getFreebase()).get("values");
                    if (aux != null && aux.isArray()) {
                        for (JsonNode value : aux) {
                            if (DBPediaPropertyExtractor.isEN(value)) {
                                commandersArray.add(new StringPair(DBPediaPropertyExtractor.getLink(AdditionalQuestion.WHO_IS,
                                        DBPediaPropertyExtractor.extractValue(value.get("text"))),
                                        DBPediaPropertyExtractor.extractValue(value.get("text"))));
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

        return extractStringPair(AdditionalQuestion.LOCATION_INFO, combatants);
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
                    if(same.size() == 1) {
                        stringAux = DBPediaPropertyExtractor.extractValue(getDeepProperties("text", same.get(0)).get(0));
                        aux.setCombatant(new StringPair(DBPediaPropertyExtractor.getLink(AdditionalQuestion.WHO_IS, stringAux), stringAux));
                    }
                    if(same.size() == 2) {
                        stringAux = DBPediaPropertyExtractor.extractValue(getDeepProperties("text", same.get(1)).get(0));
                        aux.setCasualties(stringAux);
                    }
                    if(same.size() == 3) {
                        stringAux = DBPediaPropertyExtractor.extractValue(getDeepProperties("text", same.get(2)).get(0));
                        aux.setCasualtyType(stringAux);
                    }
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
            response.add(node.get(prop));
            return response;
        } else if (node.has("values")) {
            ArrayNode values = (ArrayNode) node.get("values");
            for (JsonNode value : values) {
                response.addAll(getDeepProperties(prop, value));
            }
        } else if (node.has("property")) {
            response.addAll(getDeepProperties(prop, node.get("property")));
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
                response.add(node.get(prop));

            } else if (node.has("values")) {
                ArrayNode values = (ArrayNode) node.get("values");
                for (JsonNode value : values) {
                    if (value.has(prop)) {
                        response.add(value.get(prop));

                    }
                }
            } else if (node.has("property")) {
                node = node.get("property");
                if (node.has(prop)) {
                    response.add(node.get(prop));

                }
            }
        }

        return response;
    }

    public static StringPair getCapital(JsonNode node) {
        ArrayList<String> properties = new ArrayList<>();
        properties.add(MetadataProperties.CAPITAL.getFreebase());
        properties.add("text");

        ArrayList<JsonNode> combatants = getDeepProperties(properties, node);
        ArrayList<StringPair> capitals = extractStringPair(AdditionalQuestion.LOCATION_INFO, combatants);

        if(capitals != null && capitals.size() > 0) {
            return capitals.get(0);
        } else {
            return null;
        }
    }

    public static String getOfficialLanguage(JsonNode node) {
        ArrayList<String> properties = new ArrayList<>();

        properties.add(MetadataProperties.OFFICIAL_LANGUAGE.getFreebase());
        properties.add("text");

        ArrayList<JsonNode> languages = getDeepProperties(properties, node);
        StringBuilder sb = new StringBuilder();
        for(JsonNode language : languages) {
            sb.append(DBPediaPropertyExtractor.extractValue(language)).append(" / ");
        }

        if (sb.length() > 3) {
            sb.replace(sb.length() - 3, sb.length() - 1, "");
        }

        return sb.toString();

    }

    public static String getCurrency(JsonNode node) {
        ArrayList<String> properties = new ArrayList<>();

        properties.add(MetadataProperties.CURRENCY.getFreebase());
        properties.add("text");

        ArrayList<JsonNode> currencies = getDeepProperties(properties, node);
        StringBuilder sb = new StringBuilder();
        for(JsonNode currency : currencies) {
            sb.append(DBPediaPropertyExtractor.extractValue(currency)).append(" / ");
        }

        if (sb.length() > 3) {
            sb.replace(sb.length() - 3, sb.length() - 1, "");
        }

        return sb.toString();

    }

    public static String getCallingCode(JsonNode node) {
        ArrayList<String> properties = new ArrayList<>();

        properties.add(MetadataProperties.CALLING_CODE.getFreebase());
        properties.add("text");

        ArrayList<JsonNode> currencies = getDeepProperties(properties, node);
        StringBuilder sb = new StringBuilder();
        for(JsonNode currency : currencies) {
            sb.append(DBPediaPropertyExtractor.extractValue(currency)).append(" / ");
        }

        if (sb.length() > 3) {
            sb.replace(sb.length() - 3, sb.length() - 1, "");
        }

        return sb.toString();

    }

    public static String getLatitude(JsonNode node) {
        ArrayList<String> properties = new ArrayList<>();

        properties.add(MetadataProperties.GEOLOCATION.getFreebase());
        properties.add(MetadataProperties.GEOLOCATION_LATITUDE.getFreebase());
        properties.add("text");

        ArrayList<JsonNode> currencies = getDeepProperties(properties, node);
        StringBuilder sb = new StringBuilder();
        for(JsonNode currency : currencies) {
            sb.append(DBPediaPropertyExtractor.extractValue(currency)).append(" / ");
        }

        if (sb.length() > 3) {
            sb.replace(sb.length() - 3, sb.length() - 1, "");
        }

        return sb.toString();

    }

    public static String getLongitude(JsonNode node) {
        ArrayList<String> properties = new ArrayList<>();

        properties.add(MetadataProperties.GEOLOCATION.getFreebase());
        properties.add(MetadataProperties.GEOLOCATION_LONGITUDE.getFreebase());
        properties.add("text");

        ArrayList<JsonNode> currencies = getDeepProperties(properties, node);
        StringBuilder sb = new StringBuilder();
        for(JsonNode currency : currencies) {
            sb.append(DBPediaPropertyExtractor.extractValue(currency)).append(" / ");
        }

        if (sb.length() > 3) {
            sb.replace(sb.length() - 3, sb.length() - 1, "");
        }

        return sb.toString();

    }

    public static Geolocation getGeolocation(JsonNode node) {
        Geolocation location = new Geolocation();
        location.setLatitude(getLatitude(node));
        location.setLongitude(getLongitude(node));
        return location;

    }

    public static String getArea(JsonNode node) {
        ArrayList<String> properties = new ArrayList<>();

        properties.add(MetadataProperties.AREA.getFreebase());
        properties.add("text");

        ArrayList<JsonNode> currencies = getDeepProperties(properties, node);
        StringBuilder sb = new StringBuilder();
        for(JsonNode currency : currencies) {
            sb.append(DBPediaPropertyExtractor.extractValue(currency)).append(" / ");
        }

        if (sb.length() > 3) {
            sb.replace(sb.length() - 3, sb.length() - 1, "");
        }

        return sb.toString();

    }

    public static String getDateFounded(JsonNode node) {
        ArrayList<String> properties = new ArrayList<>();

        properties.add(MetadataProperties.DATE_FOUNDED.getFreebase());
        properties.add("text");

        ArrayList<JsonNode> currencies = getDeepProperties(properties, node);
        StringBuilder sb = new StringBuilder();
        for(JsonNode currency : currencies) {
            sb.append(DBPediaPropertyExtractor.extractValue(currency)).append(" / ");
        }

        if (sb.length() > 3) {
            sb.replace(sb.length() - 3, sb.length() - 1, "");
        }

        return sb.toString();

    }

    public static String getPopulation(JsonNode node) {
        ArrayList<String> properties = new ArrayList<>();

        properties.add(MetadataProperties.POPULATION.getFreebase());
        properties.add("values");

        ArrayList<JsonNode> currencies = getDeepProperties(properties, node);

        for(JsonNode currency : currencies) {
            if(currency.isArray()) {
                for(JsonNode item : currency) {
                    properties.clear();
                    properties.add("/measurement_unit/dated_integer/number");
                    properties.add("text");
                    currencies = getDeepProperties(properties, item);
                    if(currencies.size() > 0) {
                        return DBPediaPropertyExtractor.extractValue(currencies.get(0));
                    }
                }
            } else {
                properties.clear();
                properties.add("/measurement_unit/dated_integer/number");
                properties.add("text");
                currencies = getDeepProperties(properties, currency);
                if(currencies.size() > 1) {
                    return DBPediaPropertyExtractor.extractValue(currencies.get(0));
                }
            }

        }

        return null;

    }

    public static ArrayList<StringPair> getReligions(JsonNode node) {
        ArrayList<String> properties = new ArrayList<>();
        ArrayList<String> sameLevel = new ArrayList<>();
        ArrayList<StringPair> result = new ArrayList<>();

        properties.add(MetadataProperties.RELIGIONS.getFreebase());
        properties.add("values");

        sameLevel.add(MetadataProperties.RELIGIONS_TYPE.getFreebase());
        sameLevel.add(MetadataProperties.RELIGIONS_PERCENTAGE.getFreebase());

        ArrayList<JsonNode> religions = getDeepProperties(properties, node);
        ArrayList<JsonNode> same;
        JsonNode type, percentage;
        for(JsonNode religion : religions) {
            if (religion.isArray()) {
                for(JsonNode item : religion) {
                    same = getSameLevelProperties(sameLevel, item);
                    if(same.size() > 1) {
                        type = getDeepProperties("text", same.get(0)).get(0);
                        percentage = getDeepProperties("text", same.get(1)).get(0);
                        if(type != null && percentage != null) {
                            result.add(new StringPair(DBPediaPropertyExtractor.extractValue(type),
                                    DBPediaPropertyExtractor.extractValue(percentage) + "%"));
                        }
                    }
                }
            } else {
                same = getSameLevelProperties(sameLevel, religion);

                type = getDeepProperties("text", same.get(0)).get(0);
                percentage = getDeepProperties("text", same.get(1)).get(0);
                if(type != null && percentage != null) {
                    result.add(new StringPair(DBPediaPropertyExtractor.extractValue(type),
                            DBPediaPropertyExtractor.extractValue(percentage) + "%"));
                }
            }
        }

        return result;

    }

    public static String getReleaseDate(JsonNode node) {
        ArrayList<String> properties = new ArrayList<>();

        properties.add(MetadataProperties.RELEASE_DATE.getFreebase());
        properties.add("text");

        ArrayList<JsonNode> languages = getDeepProperties(properties, node);
        StringBuilder sb = new StringBuilder();
        for(JsonNode language : languages) {
            sb.append(DBPediaPropertyExtractor.extractValue(language)).append(" / ");
        }

        if (sb.length() > 3) {
            sb.replace(sb.length() - 3, sb.length() - 1, "");
        }

        return sb.toString();

    }

    public static String getGenre(JsonNode node) {
        ArrayList<String> properties = new ArrayList<>();

        properties.add(MetadataProperties.GENRE.getFreebase());
        properties.add("text");

        ArrayList<JsonNode> languages = getDeepProperties(properties, node);
        StringBuilder sb = new StringBuilder();
        for(JsonNode language : languages) {
            sb.append(DBPediaPropertyExtractor.extractValue(language)).append(" / ");
        }

        if (sb.length() > 3) {
            sb.replace(sb.length() - 3, sb.length() - 1, "");
        }

        return sb.toString();

    }

    public static String getPrimaryReleaseId(JsonNode node) {
        ArrayList<String> properties = new ArrayList<>();

        properties.add(MetadataProperties.PRIMARY_RELEASE.getFreebase());
        properties.add("id");

        ArrayList<JsonNode> languages = getDeepProperties(properties, node);

        for(JsonNode language : languages) {
            return DBPediaPropertyExtractor.extractValue(language);
        }

        return null;
    }

    public static ArrayList<StringPair> getTrackList(JsonNode albumInfo) {
        ArrayList<String> properties = new ArrayList<>();
        ArrayList<StringPair> result = new ArrayList<>();

        properties.add(MetadataProperties.TRACK_LIST.getFreebase());
        properties.add("values");

        ArrayList<JsonNode> tracks = getDeepProperties(properties, albumInfo);

        for(JsonNode track : tracks) {
            if(track.isArray()) {
                for(JsonNode item : track) {
                    if(DBPediaPropertyExtractor.isEN(item)) {
                        result.add(new StringPair(DBPediaPropertyExtractor.extractValue(item.get("text")),
                                DBPediaPropertyExtractor.extractValue(item.get("id"))));
                    }
                }
            }


        }

        return result;

    }

    public static String getNotableFor(JsonNode node) {
        ArrayList<String> properties = new ArrayList<>();

        properties.add(MetadataProperties.NOTABLE_FOR.getFreebase());
        properties.add("text");

        ArrayList<JsonNode> currencies = getDeepProperties(properties, node);
        StringBuilder sb = new StringBuilder();
        for(JsonNode currency : currencies) {
            sb.append(DBPediaPropertyExtractor.extractValue(currency)).append(" / ");
        }

        if (sb.length() > 3) {
            sb.replace(sb.length() - 3, sb.length() - 1, "");
        }

        return sb.toString();
    }

    public static ArrayList<StringPair> getPersonSiblings(JsonNode personInfo) {
        ArrayList<String> properties = new ArrayList<>();
        ArrayList<StringPair> result = new ArrayList<>();

        properties.add(MetadataProperties.SIBLING_S.getFreebase());
        properties.add(MetadataProperties.SIBLING_RELATIONSHIP.getFreebase());
        properties.add("values");

        ArrayList<JsonNode> siblings = getDeepProperties(properties, personInfo);

        for(JsonNode aux : siblings) {
            if (!isMissingNode(aux) && aux.isArray()) {
                for (JsonNode parent : aux) {
                    if (DBPediaPropertyExtractor.isEN(parent)) {
                        result.add(extractStringPair(AdditionalQuestion.WHO_IS, parent.get("text")));
                    }
                }
            }
        }
        return result;

    }

    public static List<String> getOfficialWebsites(JsonNode info) {
        List<String> websites = new ArrayList<>();

        JsonNode aux = info.path(MetadataProperties.OFFICIAL_WEBSITE.getFreebase());
        if(!isMissingNode(aux)) {
            aux = aux.path("values");
            if(!isMissingNode(aux)) {
                for(JsonNode website : aux) {
                    websites.add(DBPediaPropertyExtractor.extractValue(website.get("text")));
                }
            }
        }

        return websites;
    }

    public static String getOrganizationDateFounded(JsonNode info) {
        JsonNode aux = info.path(MetadataProperties.ORGANIZATION_DATE_FOUNDED.getFreebase());
        if(!isMissingNode(aux)) {
            aux = aux.path("values");
            if(!isMissingNode(aux)) {
                for(JsonNode website : aux) {
                    return DBPediaPropertyExtractor.extractValue(website.get("value"));
                }
            }
        }

        return null;
    }

    public static List<StringPair> getGraduatesForEducationInstitution(JsonNode info) {
        ArrayList<String> properties = new ArrayList<>();
        List<StringPair> result = new ArrayList<>();
        properties.add(MetadataProperties.STUDENTS_GRADUATES.getFreebase());
        properties.add(MetadataProperties.STUDENT.getFreebase());
        properties.add("values");

        ArrayList<JsonNode> persons = getDeepProperties(properties, info);
        for(JsonNode person : persons) {
            if(person.isArray()) {
                for(JsonNode item : person) {
                    result.add(extractStringPair(AdditionalQuestion.WHO_IS,
                            DBPediaPropertyExtractor.extractValue(item.get("text"))));
                }
            }
        }
        return result;
    }

    public static String getAlbumOfSong(JsonNode songNode) {
        ArrayList<String> properties = new ArrayList<>();
        List<StringPair> result = new ArrayList<>();
        properties.add(MetadataProperties.TRACK_ALBUM.getFreebase());
        properties.add("values");

        ArrayList<JsonNode> persons = getDeepProperties(properties, songNode);
        for(JsonNode person : persons) {
            if(person.isArray()) {
                for(JsonNode item : person) {
                    return DBPediaPropertyExtractor.extractValue(item.get("text"));
                }
            }
        }
        return null;
    }

    public static String getTrackNumberOfSong(JsonNode songNode) {
        ArrayList<String> properties = new ArrayList<>();
        List<StringPair> result = new ArrayList<>();
        properties.add(MetadataProperties.TRACK_NUMBER.getFreebase());
        properties.add("values");

        ArrayList<JsonNode> persons = getDeepProperties(properties, songNode);
        for(JsonNode person : persons) {
            if(person.isArray()) {
                for(JsonNode item : person) {
                    return DBPediaPropertyExtractor.extractValue(item.get("text"));
                }
            }
        }
        return null;
    }
}
