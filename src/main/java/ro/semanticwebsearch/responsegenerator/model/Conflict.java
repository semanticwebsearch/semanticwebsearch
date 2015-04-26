package ro.semanticwebsearch.responsegenerator.model;

/**
 * Created by Spac on 4/26/2015.
 */
public class Conflict {
    private String name;
    private String result;
    private String date;
    private String description;
    private String wikiPageExternal;
    private StringPair place;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getWikiPageExternal() {
        return wikiPageExternal;
    }

    public void setWikiPageExternal(String wikiPageExternal) {
        this.wikiPageExternal = wikiPageExternal;
    }

    public StringPair getPlace() {
        return place;
    }

    public void setPlace(StringPair place) {
        this.place = place;
    }
}
