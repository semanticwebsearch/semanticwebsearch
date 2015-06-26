package ro.semanticwebsearch.responsegenerator.parser.helper;

/**
 * Created by Spac on 4/23/2015.
 */
public class Constants {
    public static final String LOCALHOST_LINK = "http://localhost:8080/api/query/?q=%s";
    public static final String FREEBASE = "freebase";
    public static final String DBPEDIA = "dbpedia";
    public static final String FREEBASE_RESOURCE_LINK = "https://www.googleapis.com/freebase/v1/topic%s?filter=all&key=%s";
    public static final String FREEBASE_IMAGE_LINK = "https://usercontent.googleapis.com/freebase/v1/image%s"; //?maxwidth=200&maxheight=200
    public static final String DBPEDIA_IMAGE_LINK = "http://commons.wikimedia.org/wiki/Special:FilePath/%s;"; //?width=300";)
    public static final int MAX_CHUNK_SIZE = 10;
    public static final int SECONDS_IN_A_DAY = 24*60*60;
    public static final String PERSON = "Person";
    public static final String EDUCATION_INSTITUTION = "EducationInstitution";
    public static final String BOOK = "Book";
    public static final String WEAPON = "Weapon";
    public static final String LOCATION = "Location";
    public static final String CONFLICT = "Conflict";
    public static final String ALBUM = "Album";
    public static final String ENTITY_TYPE = "entityType";


    public static final String SONG = "Song";
}
