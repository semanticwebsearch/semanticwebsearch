package ro.semanticwebsearch.responsegenerator.model;

/**
 * Created by Spac on 4/22/2015.
 */
public class StringPair{
    private String uri;
    private String text;

    public StringPair() {}

    public StringPair(String uri, String text) {
        this.uri = uri;
        this.text = text;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
