package ro.semanticwebsearch.responsegenerator.parser;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import ro.semanticwebsearch.responsegenerator.model.StringPair;

import java.util.ArrayList;

/**
 * Created by Spac on 4/23/2015.
 */
public class DBPediaParser {

    public static ArrayList<StringPair> getChildren(JsonNode personInfo) {
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
                    children.add(new StringPair(getLink(AdditionalQuestion.WHO_IS,
                            spouseName.replace("_", " ")), spouseName.replace("_", " ")));
                }
            }
        }

        return children;

    }

    public static ArrayList<StringPair> getSpouse(JsonNode personInfo) {
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
                    parents.add(new StringPair(getLink(AdditionalQuestion.WHO_IS,
                            spouseName.replace("_", " ")), spouseName.replace("_", " ")));
                }
            }
        }

        return parents;

    }

    public static ArrayList<String> getThumbnail(JsonNode personInfo) {
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

    public static ArrayList<StringPair> getParents(JsonNode personInfo) {
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
                    parents.add(new StringPair(getLink(AdditionalQuestion.WHO_IS,
                            parentName.replace("_", " ")), parentName.replace("_", " ")));
                }
            }
        }

        return parents;

    }

    public static ArrayList<StringPair> getNationality(JsonNode personInfo) {
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

    public static ArrayList<StringPair> getEducation(JsonNode personInfo) {
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

    public static String getPrimaryTopicOf(JsonNode personInfo) {
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

    public static String getShortDescription(JsonNode personInfo) {
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

    public static String getAbstractDescription(JsonNode personInfo) {
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

    public static String getDeathdate(JsonNode personInfo) {
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

    public static StringPair getBirthplace(JsonNode personInfo) {
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

    public static String getBirthdate(JsonNode personInfo) {
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

    //TODO should we add also check for label?
    public static String getName(JsonNode personInfo) {
        ArrayNode names = (ArrayNode)personInfo.findValue(MetadataProperties.NAME.getDbpedia());
        StringBuilder sb = new StringBuilder();
        if(names != null) {
            for (JsonNode name : names) {
                if (isLiteral(name)) {
                    sb.append(extractValue(name.findValue("value"))).append(" / ");
                }
            }
        }
        if(sb.length() > 3) {
            sb.replace(sb.length() - 3, sb.length() - 1, "");
        }
        return sb.toString().trim();
    }

    /**
     * Given the json node, extracts the value and removes the "
     */
    public static String extractValue(JsonNode node) {
        if(node != null) {
            return node.toString().replace("\"", "");
        } else {
            return  "";
        }

    }

    /**
     * Given the resource url found in dbpedia response, converts it in a resource url by replaces /page/ or /resource/ with /data/
     */
    public static String convertDBPediaUrlToResourceUrl(String dbpediaURL) {
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

    public static boolean isUri(JsonNode node) {
        return "\"uri\"".equals(node.findValue("type").toString());
    }

    public static boolean isLiteral(JsonNode node) {
        return "\"literal\"".equals(node.findValue("type").toString());
    }

    public static boolean isEN(JsonNode node) {
        return node.findValue("lang")!= null && "\"en\"".equals(node.findValue("lang").toString());
    }

    public static String getLink(AdditionalQuestion type, JsonNode text) {
        String result;
        if(text != null) {
            result = String.format(type.getValue(),
                    extractValue(text));
            return String.format(Constants.LOCALHOST_LINK.getValue(), result);
        }

        return "";
    }

    public static String getLink(AdditionalQuestion type, String text) {
        String result;
        if(text != null) {
            result = String.format(type.getValue(), text);
            return String.format(Constants.LOCALHOST_LINK.getValue(), result);
        }

        return "";
    }

    public static String getWeaponLength(JsonNode info) {
        ArrayNode descriptions = (ArrayNode)info.findValue(MetadataProperties.WEAPON_LENGTH.getDbpedia());
        if (descriptions != null) {
            for(JsonNode description : descriptions) {
                if(isLiteral(description)) {
                    return extractValue(description.findValue("value"));
                }
            }
        }
        return "";
    }

    public static String getWeaponWeight(JsonNode info) {
        ArrayNode descriptions = (ArrayNode)info.findValue(MetadataProperties.WEAPON_WEIGHT.getDbpedia());
        if (descriptions != null) {
            for(JsonNode description : descriptions) {
                if(isLiteral(description)) {
                    return extractValue(description.findValue("value"));
                }
            }
        }
        return "";
    }

    public static String getService(JsonNode info) {
        ArrayNode descriptions = (ArrayNode)info.findValue(MetadataProperties.PROP_SERVICE.getDbpedia());
        if (descriptions != null) {
            for(JsonNode description : descriptions) {
                if(isLiteral(description)) {
                    return extractValue(description.findValue("value"));
                }
            }
        }
        return "";
    }

    public static String getCaliber(JsonNode info) {
        ArrayNode descriptions = (ArrayNode)info.findValue(MetadataProperties.CALIBER.getDbpedia());
        if (descriptions != null) {
            for(JsonNode description : descriptions) {
                if(isLiteral(description)) {
                    return extractValue(description.findValue("value"));
                }
            }
        }
        return "";
    }

    public static StringPair getDesigner(JsonNode personInfo) {
        ArrayNode names = (ArrayNode)personInfo.findValue(MetadataProperties.DESIGNER.getDbpedia());
        if(names != null) {
            for (JsonNode name : names) {
                if (isLiteral(name)) {
                    return new StringPair("", extractValue(name.findValue("value")));
                } else if (isUri(name)) {
                    //todo Check if it is not better to get the link, get the name/label and form the question after that
                    String uri = extractValue(name.findValue("value"));
                    String[] pieces = uri.split("/");
                    String birthPlace = pieces[pieces.length - 1];
                    return new StringPair(getLink(AdditionalQuestion.WHO_IS, birthPlace.replace("_", " ")),
                            birthPlace.replace("_", " "));
                }
               /* if (isLiteral(name)) {
                    sb.append(extractValue(name.findValue("value"))).append(" / ");
                } else {
                    //todo we need to treat uris too
                    System.out.println("DESIGNER IS URI!");
                }*/
            }
        }
        return null;
    }

    public static StringPair getOrigin(JsonNode personInfo) {
        ArrayNode origins = (ArrayNode)personInfo.findValue(MetadataProperties.ORIGIN.getDbpedia());
        if(origins != null) {
            for (JsonNode item : origins) {
                if (isLiteral(item)) {
                    return new StringPair("", extractValue(item.findValue("value")));
                } else if (isUri(item)) {
                    String uri = extractValue(item.findValue("value"));
                    String[] pieces = uri.split("/");
                    String origin = pieces[pieces.length - 1];
                    return new StringPair(getLink(AdditionalQuestion.PLACE_INFO, origin.replace("_", " ")),
                            origin.replace("_", " "));
                }
               /* if (isLiteral(name)) {
                    sb.append(extractValue(name.findValue("value"))).append(" / ");
                } else {
                    //todo we need to treat uris too
                    System.out.println("DESIGNER IS URI!");
                }*/
            }
        }
        return null;
    }

    public static StringPair getPlace(JsonNode personInfo) {
        ArrayNode names = (ArrayNode)personInfo.findValue(MetadataProperties.PLACE.getDbpedia());
        if(names != null) {
            for (JsonNode name : names) {
                if (isLiteral(name)) {
                    return new StringPair("", extractValue(name.findValue("value")));
                } else if (isUri(name)) {
                    String uri = extractValue(name.findValue("value"));
                    String[] pieces = uri.split("/");
                    String birthPlace = pieces[pieces.length - 1];
                    return new StringPair(getLink(AdditionalQuestion.PLACE_INFO, birthPlace.replace("_", " ")),
                            birthPlace.replace("_", " "));
                }
               /* if (isLiteral(name)) {
                    sb.append(extractValue(name.findValue("value"))).append(" / ");
                } else {
                    //todo we need to treat uris too
                    System.out.println("DESIGNER IS URI!");
                }*/
            }
        }
        return null;
    }

    public static String getResult(JsonNode info) {
        ArrayNode descriptions = (ArrayNode)info.findValue(MetadataProperties.RESULT.getDbpedia());
        if (descriptions != null) {
            for(JsonNode description : descriptions) {
                if(isLiteral(description)) {
                    return extractValue(description.findValue("value"));
                }
            }
        }
        return "";
    }

    public static String getDate(JsonNode info) {
        ArrayNode descriptions = (ArrayNode)info.findValue(MetadataProperties.DATE.getDbpedia());
        if (descriptions != null) {
            for(JsonNode description : descriptions) {
                if(isLiteral(description)) {
                    return extractValue(description.findValue("value"));
                }
            }
        }
        return "";
    }
}
