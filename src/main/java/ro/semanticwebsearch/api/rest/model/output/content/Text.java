package ro.semanticwebsearch.api.rest.model.output.content;

/**
 * Created by valentin.spac on 1/27/2015.
 */
public class Text implements Content {
    private String description;

    public Text() {
    }

    public Text(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
