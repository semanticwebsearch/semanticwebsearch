package ro.semanticwebsearch.responsegenerator.parser.helper;

/**
 * Created by Spac on 4/23/2015.
 */
public enum Constants {
    LOCALHOST_LINK("http://localhost:8080/api/query/?q=%s"),
    FREEBASE_RESOURCE_LINK("https://www.googleapis.com/freebase/v1/topic%s?filter=all&key=%s"),
    FREEBASE_IMAGE_LINK("https://usercontent.googleapis.com/freebase/v1/image%s"), //?maxwidth=200&maxheight=200
    DBPEDIA_IMAGE_LINK("http://commons.wikimedia.org/wiki/Special:FilePath/%s;"); //?width=300";)

    private String value;

    Constants(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "Constants{" +
                "value='" + value + '\'' +
                '}';
    }
}
