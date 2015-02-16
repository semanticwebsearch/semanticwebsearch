package ro.semanticwebsearch.api.rest.model.output.content;

/**
 * Created by valentin.spac on 1/27/2015.
 */
public class Video implements Content {
    private String source;
    private String description;

    public Video() {
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
