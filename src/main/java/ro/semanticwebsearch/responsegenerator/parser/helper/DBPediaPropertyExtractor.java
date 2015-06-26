package ro.semanticwebsearch.responsegenerator.parser.helper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import ro.semanticwebsearch.responsegenerator.model.Geolocation;
import ro.semanticwebsearch.responsegenerator.model.StringPair;

import java.util.ArrayList;

/**
 * Created by Spac on 4/23/2015.
 */
public class DBPediaPropertyExtractor {

    public static ArrayList<StringPair> getChildren(JsonNode personInfo) {
        ArrayList<StringPair> children = new ArrayList<>();
        ArrayNode childrenArray = (ArrayNode) personInfo.get(MetadataProperties.CHILDREN.getDbpedia());
        if (childrenArray != null) {
            for (JsonNode child : childrenArray) {
                if (isLiteral(child)) {
                    children.add(new StringPair("", extractValue(child.get("value"))));
                } else if (isUri(child)) {
                    String uri = extractValue(child.get("value"));
                    String[] pieces = uri.split("/");
                    String spouseName = pieces[pieces.length - 1];
                    children.add(new StringPair(getLink(AdditionalQuestion.WHO_IS,
                            spouseName.replace("_", " ")), spouseName.replace("_", " ")));
                }
            }
        }

        return children;

    }

    public static ArrayList<StringPair> getSpouse(JsonNode personInfo) {
        ArrayList<StringPair> parents = new ArrayList<>();
        ArrayNode spouses = (ArrayNode) personInfo.get(MetadataProperties.SPOUSE.getDbpedia());
        if (spouses != null) {
            for (JsonNode spouse : spouses) {
                if (isLiteral(spouse)) {
                    parents.add(new StringPair("", extractValue(spouse.get("value"))));
                } else if (isUri(spouse)) {
                    String uri = extractValue(spouse.get("value"));
                    String[] pieces = uri.split("/");
                    String spouseName = pieces[pieces.length - 1];
                    parents.add(new StringPair(getLink(AdditionalQuestion.WHO_IS,
                            spouseName.replace("_", " ")), spouseName.replace("_", " ")));
                }
            }
        }

        return parents;

    }

    public static ArrayList<String> getThumbnail(JsonNode personInfo) {
        ArrayList<String> thumbs = new ArrayList<>();
        ArrayNode thumbnails = (ArrayNode) personInfo.get(MetadataProperties.THUMBNAIL.getDbpedia());
        if (thumbnails != null) {
            for (JsonNode thumbnail : thumbnails) {
                if (isUri(thumbnail)) {
                    thumbs.add(extractValue(thumbnail.get("value")));
                }
            }
        }
        return thumbs;

    }

    public static ArrayList<StringPair> getParents(JsonNode personInfo) {
        ArrayList<StringPair> parents = new ArrayList<>();
        ArrayNode parentsArray = (ArrayNode) personInfo.get(MetadataProperties.PARENTS.getDbpedia());
        if (parentsArray != null) {
            for (JsonNode parent : parentsArray) {
                if (isLiteral(parent)) {
                    parents.add(new StringPair("", extractValue(parent.get("value"))));
                } else if (isUri(parent)) {
                    String uri = extractValue(parent.get("value"));
                    String[] pieces = uri.split("/");
                    String parentName = pieces[pieces.length - 1];
                    parents.add(new StringPair(getLink(AdditionalQuestion.WHO_IS,
                            parentName.replace("_", " ")), parentName.replace("_", " ")));
                }
            }
        }

        return parents;

    }

    public static ArrayList<StringPair> getNationality(JsonNode personInfo) {
        ArrayList<StringPair> nationalitiesArray = new ArrayList<>();
        ArrayNode nationalities = (ArrayNode) personInfo.get(MetadataProperties.NATIONALITY.getDbpedia());
        if (nationalities != null) {
            for (JsonNode nationality : nationalities) {
                if (isLiteral(nationality)) {
                    nationalitiesArray.add(new StringPair("", extractValue(nationality.get("value"))));
                } else if (isUri(nationality)) {
                    String uri = extractValue(nationality.get("value"));
                    String[] pieces = uri.split("/");
                    String name = pieces[pieces.length - 1];
                    nationalitiesArray.add(new StringPair(uri, name.replace("_", " ")));
                }
            }
        }

        return nationalitiesArray;

    }

    public static ArrayList<StringPair> getEducation(JsonNode personInfo) {
        ArrayNode educations = (ArrayNode) personInfo.get(MetadataProperties.EDUCATION_ONTOLOGY.getDbpedia());
        ArrayList<StringPair> institutions = new ArrayList<>();
        if (educations != null) {
            for (JsonNode educationNode : educations) {
                if (isLiteral(educationNode)) {
                    institutions.add(FreebasePropertyExtractor.extractStringPair(AdditionalQuestion.EDUCATION_INFO,
                            educationNode.get("value")));
                } else if (isUri(educationNode)) {
                    String uri = extractValue(educationNode.get("value"));
                    String[] pieces = uri.split("/");
                    String education = pieces[pieces.length - 1];
                    institutions.add(FreebasePropertyExtractor.extractStringPair(AdditionalQuestion.EDUCATION_INFO,
                            education.replace("_", " ")));
                }
            }
        }

        educations = (ArrayNode) personInfo.get(MetadataProperties.ALMA_MATER.getDbpedia());
        if (educations != null) {
            for (JsonNode educationNode : educations) {
                if (isLiteral(educationNode)) {
                    institutions.add(new StringPair("", extractValue(educationNode.get("value"))));
                } else if (isUri(educationNode)) {
                    String uri = extractValue(educationNode.get("value"));
                    String[] pieces = uri.split("/");
                    String education = pieces[pieces.length - 1];
                    institutions.add(FreebasePropertyExtractor.extractStringPair(AdditionalQuestion.EDUCATION_INFO,
                            education.replace("_", " ")));
                }
            }
        }

        return institutions;

    }

    public static String getPrimaryTopicOf(JsonNode personInfo) {
        ArrayNode topics = (ArrayNode) personInfo.get(MetadataProperties.PRIMARY_TOPIC_OF.getDbpedia());
        if (topics != null) {
            for (JsonNode topic : topics) {
                if (isUri(topic)) {
                    return extractValue(topic.get("value"));
                }
            }
        }
        return "";
    }

    public static String getShortDescription(JsonNode personInfo) {
        ArrayNode descriptions = (ArrayNode) personInfo.get(MetadataProperties.SHORT_DESCRIPTION.getDbpedia());
        if (descriptions != null) {
            for (JsonNode description : descriptions) {
                if (isLiteral(description)) {
                    return extractValue(description.get("value"));
                }
            }
        }
        return "";
    }

    public static String getAbstractDescription(JsonNode personInfo) {
        ArrayNode descriptions = (ArrayNode) personInfo.get(MetadataProperties.ABSTRACT.getDbpedia());
        if (descriptions != null) {
            for (JsonNode abstractDescription : descriptions) {
                if (isLiteral(abstractDescription) && isEN(abstractDescription)) {
                    return extractValue(abstractDescription.get("value"));
                }
            }

            for (JsonNode abstractDescription : descriptions) {
                if (isLiteral(abstractDescription)/* && isEN(abstractDescription)*/) {
                    return extractValue(abstractDescription.get("value"));
                }
            }
        }
        return "";
    }

    public static String getDeathdate(JsonNode personInfo) {
        ArrayNode dates = (ArrayNode) personInfo.get(MetadataProperties.DEATHDATE.getDbpedia());
        if (dates != null) {
            for (JsonNode deathDate : dates) {
                if (isLiteral(deathDate)) {
                    return extractValue(deathDate.get("value"));
                }
            }
        }
        return "";
    }

    public static StringPair getBirthplace(JsonNode personInfo) {
        ArrayNode places = (ArrayNode) personInfo.get(MetadataProperties.BIRTHPLACE.getDbpedia());
        if (places != null) {
            for (JsonNode birthplace : places) {
                if (isLiteral(birthplace)) {
                    return new StringPair("", extractValue(birthplace.get("value")));
                } else if (isUri(birthplace)) {
                    String uri = extractValue(birthplace.get("value"));
                    String[] pieces = uri.split("/");
                    String birthPlace = pieces[pieces.length - 1];
                    return FreebasePropertyExtractor.extractStringPair(AdditionalQuestion.LOCATION_INFO,
                            birthPlace.replace("_", " "));
                }
            }
        }
        return null;
    }

    public static String getBirthdate(JsonNode personInfo) {
        ArrayNode dates = (ArrayNode) personInfo.get(MetadataProperties.BIRTHDATE.getDbpedia());
        if (dates != null) {
            for (JsonNode birthdate : dates) {
                if (isLiteral(birthdate)) {
                    return extractValue(birthdate.get("value"));
                }
            }
        }
        return "";
    }

    //TODO should we add also check for label?
    public static String getName(JsonNode personInfo) {
        ArrayNode names = (ArrayNode) personInfo.get(MetadataProperties.NAME.getDbpedia());
        StringBuilder sb = new StringBuilder();
        if (names != null) {
            for (JsonNode name : names) {
                if (isLiteral(name)) {
                    sb.append(extractValue(name.get("value"))).append(" / ");
                }
            }
        }
        if (sb.length() > 3) {
            sb.replace(sb.length() - 3, sb.length() - 1, "");
        }
        return sb.toString().trim();
    }

    /**
     * Given the json node, extracts the value and removes the "
     */
    public static String extractValue(JsonNode node) {
        if (node != null) {
            return node.toString().replace("\"", "");
        } else {
            return "";
        }

    }

    /**
     * Given the resource url found in dbpedia response, converts it in a resource url by replaces /page/ or /resource/ with /data/
     */
    public static String convertDBPediaUrlToResourceUrl(String dbpediaURL) {
        StringBuilder builder;
        if(dbpediaURL.contains("/data/") && dbpediaURL.endsWith(".json")) {
            return dbpediaURL;
        } else {
            if (dbpediaURL.contains("/page/")) {
                builder = new StringBuilder(dbpediaURL.replace("/page/",
                        "/data/"));
            } else {
                builder = new StringBuilder(dbpediaURL.replace("/resource/",
                        "/data/"));
            }

            builder.append(".json");
            return builder.toString();
        }
    }

    public static boolean isUri(JsonNode node) {
        return "\"uri\"".equals(node.get("type").toString());
    }

    public static boolean isLiteral(JsonNode node) {
        return "\"literal\"".equals(node.get("type").toString());
    }

    public static boolean isEN(JsonNode node) {
        return node.get("lang") != null && "\"en\"".equals(node.get("lang").toString());
    }

    public static String getLink(AdditionalQuestion type, JsonNode text) {
        String result;
        if (text != null) {
            result = String.format(type.getValue(),
                    extractValue(text));
            return String.format(Constants.LOCALHOST_LINK, result);
        }

        return "";
    }

    public static String getLink(AdditionalQuestion type, String text) {
        String result;
        if (text != null) {
            result = String.format(type.getValue(), text);
            return String.format(Constants.LOCALHOST_LINK, result);
        }

        return "";
    }

    public static String getWeaponLength(JsonNode info) {
        ArrayNode descriptions = (ArrayNode) info.get(MetadataProperties.WEAPON_LENGTH.getDbpedia());
        if (descriptions != null) {
            for (JsonNode description : descriptions) {
                if (isLiteral(description)) {
                    return extractValue(description.get("value"));
                }
            }
        }
        return "";
    }

    public static String getWeaponWeight(JsonNode info) {
        ArrayNode descriptions = (ArrayNode) info.get(MetadataProperties.WEAPON_WEIGHT.getDbpedia());
        if (descriptions != null) {
            for (JsonNode description : descriptions) {
                if (isLiteral(description)) {
                    return extractValue(description.get("value"));
                }
            }
        }
        return "";
    }

    public static String getService(JsonNode info) {
        ArrayNode descriptions = (ArrayNode) info.get(MetadataProperties.PROP_SERVICE.getDbpedia());
        if (descriptions != null) {
            for (JsonNode description : descriptions) {
                if (isLiteral(description)) {
                    return extractValue(description.get("value"));
                }
            }
        }
        return "";
    }

    public static String getCaliber(JsonNode info) {
        ArrayNode descriptions = (ArrayNode) info.get(MetadataProperties.CALIBER.getDbpedia());
        if (descriptions != null) {
            for (JsonNode description : descriptions) {
                if (isLiteral(description)) {
                    return extractValue(description.get("value"));
                }
            }
        }
        return "";
    }

    public static StringPair getDesigner(JsonNode personInfo) {
        ArrayNode names = (ArrayNode) personInfo.get(MetadataProperties.DESIGNER.getDbpedia());
        if (names != null) {
            for (JsonNode name : names) {
                if (isLiteral(name)) {
                    return new StringPair("", extractValue(name.get("value")));
                } else if (isUri(name)) {
                    //todo Check if it is not better to get the link, get the name/label and form the question after that
                    String uri = extractValue(name.get("value"));
                    String[] pieces = uri.split("/");
                    String birthPlace = pieces[pieces.length - 1];
                    return new StringPair(getLink(AdditionalQuestion.WHO_IS, birthPlace.replace("_", " ")),
                            birthPlace.replace("_", " "));
                }
               /* if (isLiteral(name)) {
                    sb.append(extractValue(name.get("value"))).append(" / ");
                } else {
                    //todo we need to treat uris too
                    System.out.println("DESIGNER IS URI!");
                }*/
            }
        }
        return null;
    }

    public static StringPair getOrigin(JsonNode personInfo) {
        ArrayNode origins = (ArrayNode) personInfo.get(MetadataProperties.ORIGIN.getDbpedia());
        if (origins != null) {
            for (JsonNode item : origins) {
                if (isLiteral(item)) {
                    return new StringPair("", extractValue(item.get("value")));
                } else if (isUri(item)) {
                    String uri = extractValue(item.get("value"));
                    String[] pieces = uri.split("/");
                    String origin = pieces[pieces.length - 1];
                    return new StringPair(getLink(AdditionalQuestion.LOCATION_INFO, origin.replace("_", " ")),
                            origin.replace("_", " "));
                }
               /* if (isLiteral(name)) {
                    sb.append(extractValue(name.get("value"))).append(" / ");
                } else {
                    //todo we need to treat uris too
                    System.out.println("DESIGNER IS URI!");
                }*/
            }
        }
        return null;
    }

    public static ArrayList<StringPair> getPlaces(JsonNode personInfo) {
        ArrayNode names = (ArrayNode) personInfo.get(MetadataProperties.PLACE.getDbpedia());
        ArrayList<StringPair> places = new ArrayList<>();
        if (names != null) {
            for (JsonNode name : names) {
                if (isLiteral(name)) {
                    places.add(FreebasePropertyExtractor.extractStringPair(AdditionalQuestion.LOCATION_INFO,
                            name.get("value")));
                } else if (isUri(name)) {
                    String uri = extractValue(name.get("value"));
                    String[] pieces = uri.split("/");
                    String birthPlace = pieces[pieces.length - 1];
                    places.add(FreebasePropertyExtractor.extractStringPair(AdditionalQuestion.LOCATION_INFO,
                            birthPlace.replace("_", " ")));
                }
            }
        }
        return places;
    }

    public static String getResult(JsonNode info) {
        ArrayNode descriptions = (ArrayNode) info.get(MetadataProperties.RESULT.getDbpedia());
        if (descriptions != null) {
            for (JsonNode description : descriptions) {
                if (isLiteral(description)) {
                    return extractValue(description.get("value"));
                }
            }
        }
        return "";
    }

    public static String getDate(JsonNode info) {
        ArrayNode descriptions = (ArrayNode) info.get(MetadataProperties.DATE.getDbpedia());
        if (descriptions != null) {
            for (JsonNode description : descriptions) {
                if (isLiteral(description)) {
                    return extractValue(description.get("value"));
                }
            }
        }
        return "";
    }

    public static ArrayList<StringPair> getCommanders(JsonNode conflictInfo) {
        ArrayList<StringPair> commanders = new ArrayList<>();
        ArrayNode parentsArray = (ArrayNode) conflictInfo.get(MetadataProperties.COMMANDERS.getDbpedia());
        if (parentsArray != null) {
            for (JsonNode parent : parentsArray) {
                if (isLiteral(parent)) {
                    commanders.add(FreebasePropertyExtractor.extractStringPair(AdditionalQuestion.WHO_IS,
                            parent.get("value")));
                } else if (isUri(parent)) {
                    String uri = extractValue(parent.get("value"));
                    String[] pieces = uri.split("/");
                    String parentName = pieces[pieces.length - 1];
                    //TODO better get the name from uri
                    commanders.add(FreebasePropertyExtractor.extractStringPair(AdditionalQuestion.WHO_IS,
                            parentName.replace("_", " ")));
                }
            }
        }

        return commanders;
    }

    public static ArrayList<StringPair> getCombatants(JsonNode conflictInfo) {
        ArrayList<StringPair> commanders = new ArrayList<>();
        ArrayNode parentsArray = (ArrayNode) conflictInfo.get(MetadataProperties.MILITARY_CONFLICT_COMBATANTS.getDbpedia());
        if (parentsArray != null) {
            for (JsonNode parent : parentsArray) {
                if (isLiteral(parent)) {
                    commanders.add(FreebasePropertyExtractor.extractStringPair(AdditionalQuestion.LOCATION_INFO,
                            parent.get("value")));
                } else if (isUri(parent)) {
                    String uri = extractValue(parent.get("value"));
                    String[] pieces = uri.split("/");
                    String parentName = pieces[pieces.length - 1];
                    //TODO better get the name from uri
                    commanders.add(FreebasePropertyExtractor.extractStringPair(AdditionalQuestion.LOCATION_INFO,
                            parentName.replace("_", " ")));
                }
            }
        }

        return commanders;
    }

    public static ArrayList<StringPair> getPartOf(JsonNode conflictInfo) {
        ArrayList<StringPair> commanders = new ArrayList<>();
        ArrayNode parentsArray = (ArrayNode) conflictInfo.get(MetadataProperties.PART_OF.getDbpedia());
        if (parentsArray != null) {
            for (JsonNode parent : parentsArray) {
                if (isLiteral(parent)) {
                    commanders.add(new StringPair("", extractValue(parent.get("value"))));
                } else if (isUri(parent)) {
                    String uri = extractValue(parent.get("value"));
                    String[] pieces = uri.split("/");
                    String parentName = pieces[pieces.length - 1];
                    //TODO better get the name from uri
                    commanders.add(new StringPair(getLink(AdditionalQuestion.LOCATION_INFO,
                            parentName.replace("_", " ")), parentName.replace("_", " ")));
                }
            }
        }

        return commanders;
    }

    public static String getLatitude(JsonNode node) {
        ArrayNode descriptions = (ArrayNode) node.get(MetadataProperties.GEOLOCATION_LATITUDE.getDbpedia());
        if (descriptions != null) {
            for (JsonNode description : descriptions) {
                if (isLiteral(description)) {
                    return extractValue(description.get("value"));
                }
            }
        }
        return "";
    }

    public static String getLongitude(JsonNode node) {
        ArrayNode descriptions = (ArrayNode) node.get(MetadataProperties.GEOLOCATION_LONGITUDE.getDbpedia());
        if (descriptions != null) {
            for (JsonNode description : descriptions) {
                if (isLiteral(description)) {
                    return extractValue(description.get("value"));
                }
            }
        }
        return "";
    }

    public static String getPopulation(JsonNode node) {
        ArrayNode descriptions = (ArrayNode) node.get(MetadataProperties.POPULATION.getDbpedia());
        if (descriptions != null) {
            for (JsonNode description : descriptions) {
                if (isLiteral(description)) {
                    return extractValue(description.get("value"));
                }
            }
        }
        return "";
    }

    public static Geolocation getGeolocation(JsonNode node) {
        Geolocation geolocation = new Geolocation();
        geolocation.setLongitude(getLongitude(node));
        geolocation.setLatitude(getLatitude(node));

        return geolocation;
    }

    public static String getLanguage(JsonNode personInfo) {
        ArrayNode names = (ArrayNode) personInfo.get(MetadataProperties.OFFICIAL_LANGUAGE.getDbpedia());
        StringBuilder sb = new StringBuilder();
        if (names != null) {
            for (JsonNode name : names) {
                if (isLiteral(name)) {
                    sb.append(extractValue(name.get("value"))).append(" / ");
                } else if(isUri(name)) {
                    String uri = extractValue(name.get("value"));
                    String[] pieces = uri.split("/");
                    String language = pieces[pieces.length - 1];
                    sb.append(language.replace("_", " ")).append(" / ");
                }
            }
        }
        if (sb.length() > 3) {
            sb.replace(sb.length() - 3, sb.length() - 1, "");
        }
        return sb.toString().trim();
    }

    public static StringPair getCapital(JsonNode personInfo) {

        ArrayNode childrenArray = (ArrayNode) personInfo.get(MetadataProperties.CAPITAL.getDbpedia());
        if (childrenArray != null) {
            for (JsonNode child : childrenArray) {
                if (isLiteral(child)) {
                    return FreebasePropertyExtractor.extractStringPair(AdditionalQuestion.LOCATION_INFO,
                            child.get("value"));
                } else if (isUri(child)) {
                    String uri = extractValue(child.get("value"));
                    String[] pieces = uri.split("/");
                    String spouseName = pieces[pieces.length - 1];
                    return FreebasePropertyExtractor.extractStringPair(AdditionalQuestion.LOCATION_INFO,
                            spouseName.replace("_", " "));
                }
            }
        }

        return null;

    }

    public static String getCurrency(JsonNode personInfo) {
        ArrayNode names = (ArrayNode) personInfo.get(MetadataProperties.CURRENCY.getDbpedia());
        StringBuilder sb = new StringBuilder();
        if (names != null) {
            for (JsonNode name : names) {
                if (isLiteral(name)) {
                    sb.append(extractValue(name.get("value"))).append(" / ");
                }
            }
        }
        if (sb.length() > 3) {
            sb.replace(sb.length() - 3, sb.length() - 1, "");
        }
        return sb.toString().trim();
    }

    public static String getCallingCode(JsonNode personInfo) {
        ArrayNode names = (ArrayNode) personInfo.get(MetadataProperties.CALLING_CODE.getDbpedia());
        StringBuilder sb = new StringBuilder();
        if (names != null) {
            for (JsonNode name : names) {
                if (isLiteral(name)) {
                    sb.append(extractValue(name.get("value"))).append(" / ");
                }
            }
        }
        if (sb.length() > 3) {
            sb.replace(sb.length() - 3, sb.length() - 1, "");
        }
        return sb.toString().trim();
    }

    public static String getArea(JsonNode node) {
        ArrayNode names = (ArrayNode) node.get(MetadataProperties.AREA.getDbpedia());
        StringBuilder sb = new StringBuilder();
        if (names != null) {
            for (JsonNode name : names) {
                if (isLiteral(name)) {
                    sb.append(extractValue(name.get("value"))).append(" / ");
                    break;
                }
            }
        }
        if (sb.length() > 3) {
            sb.replace(sb.length() - 3, sb.length() - 1, "");
        }
        return sb.toString().trim();
    }

    public static String getFoundingDate(JsonNode node) {
        ArrayNode names = (ArrayNode) node.get(MetadataProperties.DATE_FOUNDED.getDbpedia());
        StringBuilder sb = new StringBuilder();
        if (names != null) {
            for (JsonNode name : names) {
                if (isLiteral(name)) {
                    sb.append(extractValue(name.get("value"))).append(" / ");
                }
            }
        }
        if (sb.length() > 3) {
            sb.replace(sb.length() - 3, sb.length() - 1, "");
        }
        return sb.toString().trim();
    }


    public static String getReleaseDate(JsonNode albumInfo) {
        ArrayNode names = (ArrayNode) albumInfo.get(MetadataProperties.RELEASE_DATE.getDbpedia());
        StringBuilder sb = new StringBuilder();
        if (names != null) {
            for (JsonNode name : names) {
                if (isLiteral(name)) {
                    sb.append(extractValue(name.get("value"))).append(" / ");
                }
            }
        }
        if (sb.length() > 3) {
            sb.replace(sb.length() - 3, sb.length() - 1, "");
        }
        return sb.toString().trim();
    }

    public static String getGenre(JsonNode albumInfo) {
        StringBuilder sb = new StringBuilder();
        ArrayNode childrenArray = (ArrayNode) albumInfo.get(MetadataProperties.GENRE.getDbpedia());

        if (childrenArray != null) {
            for (JsonNode child : childrenArray) {
                if (isLiteral(child)) {
                    sb.append(child.get("value")).append(" / ");
                } else if (isUri(child)) {
                    String uri = extractValue(child.get("value"));
                    String[] pieces = uri.split("/");
                    String spouseName = pieces[pieces.length - 1];
                    sb.append(spouseName.replace("_", " ")).append(" / ");
                }
            }
        }

        if (sb.length() > 3) {
            sb.replace(sb.length() - 3, sb.length() - 1, "");
        }
        return sb.toString().trim();
    }

    public static ArrayList<StringPair> getPersonSiblings(JsonNode response) {
        /*
        * Problema: formatul json`ului: entitatea(sibling) e cheie ce are ca prop persoana
        * nu pot lua parintele
        *  "http://dbpedia.org/resource/Scott_Trimble": {
                "http://dbpedia.org/ontology/relative": [
                    {
                        "type": "uri",
                        "value": "http://dbpedia.org/resource/Barack_Obama"
                    }
                ],
                "http://dbpedia.org/property/relatives": [
                    {
                        "type": "uri",
                        "value": "http://dbpedia.org/resource/Barack_Obama"
                    }
                ]
            },
        * */
        return null;

    }
}
