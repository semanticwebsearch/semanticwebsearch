package ro.semanticwebsearch.responsegenerator.model;

import java.util.List;

/**
 * Created by Spac on 6/23/2015.
 */
public class EducationInstitution {
    private String description;//
    private String wikiPageExternal;//
    private List<String> websites; //
    private Geolocation geolocation; //
    private String dateFounded; //
    private String name;//
    private List<StringPair> graduates; //

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

    public List<String> getWebsites() {
        return websites;
    }

    public void setWebsites(List<String> websites) {
        this.websites = websites;
    }

    public Geolocation getGeolocation() {
        return geolocation;
    }

    public void setGeolocation(Geolocation geolocation) {
        this.geolocation = geolocation;
    }

    public String getDateFounded() {
        return dateFounded;
    }

    public void setDateFounded(String dateFounded) {
        this.dateFounded = dateFounded;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<StringPair> getGraduates() {
        return graduates;
    }

    public void setGraduates(List<StringPair> graduates) {
        this.graduates = graduates;
    }
}
