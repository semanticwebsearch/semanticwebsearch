package ro.semanticwebsearch.responsegenerator.parser;

import com.fasterxml.jackson.databind.JsonNode;
import ro.semanticwebsearch.responsegenerator.model.StringPair;

import java.util.ArrayList;

/**
 * Created by Spac on 4/25/2015.
 */
public class FreebaseParser {

    public static ArrayList<StringPair> getChildren(JsonNode personInfo) {
        ArrayList<StringPair> parentsArray = new ArrayList<>();
        JsonNode children = personInfo.findValue(MetadataProperties.PARENTS.getFreebase());
        if(children == null) {
            return null;
        }

        children = children.findValue("values");

        if(children != null && children.isArray()) {
            for (JsonNode parent : children) {
                if(DBPediaParser.isEN(parent)) {
                    parentsArray.add(new StringPair(DBPediaParser.getLink(AdditionalQuestion.WHO_IS,
                            parent.findValue("text")),DBPediaParser.extractValue(parent.findValue("text"))));
                }
            }
        }
        return parentsArray;

    }

    public static ArrayList<StringPair> getSpouse(JsonNode personInfo) {
        //"/people/marriage/spouse"
        ArrayList<StringPair> spousesArray = new ArrayList<>();
        JsonNode spouses = personInfo.findValue(MetadataProperties.SPOUSE.getFreebase());

        if(spouses == null) {
            return null;
        }

        spouses = spouses.findValue("values");

        JsonNode aux = null;
        if(spouses != null && spouses.isArray()) {
            for (JsonNode spouse : spouses) {
                if (spouse != null) {
                    aux = spouse.findValue("/people/marriage/spouse").findValue("values");
                }

                if (aux != null && aux.isArray()) {
                    for (JsonNode parent : aux) {
                        if (DBPediaParser.isEN(parent)) {
                            spousesArray.add(new StringPair(DBPediaParser.getLink(AdditionalQuestion.WHO_IS,
                                    parent.findValue("text")), DBPediaParser.extractValue(parent.findValue("text"))));
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

        if(parents == null) {
            return null;
        }

        parents = parents.findValue("values");

        if(parents != null && parents.isArray()) {
            for (JsonNode parent : parents) {
                if(DBPediaParser.isEN(parent)) {
                    parentsArray.add(new StringPair(DBPediaParser.getLink(AdditionalQuestion.WHO_IS, parent.findValue("text")),
                            DBPediaParser.extractValue(parent.findValue("text"))));
                }
            }
        }
        return parentsArray;
    }

    public static ArrayList<StringPair> getNationality(JsonNode personInfo) {
        ArrayList<StringPair> nationalitiesArray = new ArrayList<>();
        JsonNode nationalities = personInfo.findValue(MetadataProperties.NATIONALITY.getFreebase());

        if(nationalities == null) {
            return null;
        }

        nationalities = nationalities.findValue("values");

        if(nationalities != null && nationalities.isArray()) {
            for (JsonNode nationality : nationalities) {
                if(DBPediaParser.isEN(nationality)) {
                    nationalitiesArray.add(new StringPair(getFreebaseLink(extractFreebaseId(nationality)),
                            DBPediaParser.extractValue(nationality.findValue("text"))));
                }
            }
        }

        return nationalitiesArray;
    }

    public static ArrayList<StringPair> getEducation(JsonNode personInfo) {
        JsonNode institutions = personInfo.findValue(MetadataProperties.EDUCATION_ONTOLOGY.getFreebase());

        if(institutions == null) {
            return null;
        }

        institutions = institutions.findValue("values");

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
                            if (DBPediaParser.isEN(value)) {
                                name = DBPediaParser.extractValue(value.findValue("text"));
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
                                    if (DBPediaParser.isEN(value)) {
                                        name += (" - " + DBPediaParser.extractValue(value.findValue("text")));
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

    public static String getPrimaryTopicOf(JsonNode personInfo) {
        JsonNode links = personInfo.findValue(MetadataProperties.PRIMARY_TOPIC_OF.getFreebase());

        if(links == null) {
            return "";
        }

        links = links.findValue("values");

        String aux;
        if(links != null) {
            for (JsonNode topic : links) {
                aux = DBPediaParser.extractValue(topic.findValue("value"));
                if(aux != null && aux.contains("en.wikipedia")) {
                    return aux;
                }
            }
        }
        return "";

    }

    public static ArrayList<String> getThumbnail(JsonNode personInfo) {
        ArrayList<String> thumbs = new ArrayList<>();
        JsonNode thumbnails = personInfo.findValue(MetadataProperties.THUMBNAIL.getFreebase());

        if(thumbnails == null) {
            return null;
        }

        thumbnails = thumbnails.findValue("values");

        if(thumbnails != null) {
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
        if(values == null) {
            return "";
        }

        values = values.findValue("values");

        if(values != null && values.isArray()) {
            for (JsonNode value : values) {
                if(DBPediaParser.isEN(value)) {
                    return DBPediaParser.extractValue(value.findValue("text"));
                }
            }
        }
        return "";
    }

    public static String getAbstractDescription(JsonNode personInfo) {
        JsonNode values = personInfo.findValue(MetadataProperties.ABSTRACT.getFreebase()).findValue("values");
        if(values != null && values.isArray()) {
            for (JsonNode value : values) {
                if(DBPediaParser.isEN(value)) {
                    return DBPediaParser.extractValue(value.findValue("value"));
                }
            }
        }

        return "";
    }

    public static StringPair getBirthplace(JsonNode personInfo) {
        JsonNode values = personInfo.findValue(MetadataProperties.BIRTHPLACE.getFreebase()).findValue("values");
        if(values != null && values.isArray()) {
            for (JsonNode value : values) {
                if(DBPediaParser.isEN(value)) {
                    return new StringPair(getFreebaseLink(extractFreebaseId(value)),
                            DBPediaParser.extractValue(value.findValue("text")));
                }
            }
        }

        return new StringPair();
    }

    public static String getDeathdate(JsonNode personInfo) {
        JsonNode values = personInfo.findValue(MetadataProperties.DEATHDATE.getFreebase());
        if(values == null) {
            return "";
        }

        values = values.findValue("values");
        JsonNode deathDate;
        if(values != null && values.isArray()) {
            for(JsonNode value : values) {
                deathDate = value.findValue("value");
                if(deathDate != null) {
                    deathDate = value.findValue("text");
                }

                if(deathDate != null) {
                    return DBPediaParser.extractValue(deathDate);
                }
            }
        }

        return "";

    }

    public static String getBirthdate(JsonNode personInfo) {
        JsonNode values = personInfo.findValue(MetadataProperties.BIRTHDATE.getFreebase());

        if(values == null) {
            return "";
        }

        values = values.findValue("values");
        JsonNode birthdate;
        if(values != null && values.isArray()) {
            for(JsonNode value : values) {
                birthdate = value.findValue("value");
                if(birthdate != null) {
                    birthdate = value.findValue("text");
                }

                if(birthdate != null) {
                    return DBPediaParser.extractValue(birthdate);
                }
            }
        }

        return "";
    }

    public static String getPersonName(JsonNode personInfo) {
        JsonNode values = personInfo.findValue(MetadataProperties.NAME.getFreebase());
        if(values!= null ) {
            values = values.findValue("values");
            JsonNode name;
            if(values != null && values.isArray()) {
                for(JsonNode value : values) {
                    if(DBPediaParser.isEN(value)) {
                        name = value.findValue("value");
                        if (name != null) {
                            name = value.findValue("text");
                        }

                        if (name != null) {
                            return DBPediaParser.extractValue(name);
                        }
                    }
                }
            }
        }
        return "";
    }

    public static String extractFreebaseId(JsonNode item) {
        JsonNode uris = item.findValue("id");
        if(uris != null && uris.isArray()) {
            for (JsonNode uri : uris) {// "type":
                return DBPediaParser.extractValue(uri.findValue("value"));
            }
        } else {
            return DBPediaParser.extractValue(uris);
        }

        return "";

    }

    public static String getFreebaseLink(String resource) {
        return String.format(Constants.FREEBASE_RESOURCE_LINK.getValue(), resource);
    }
}
