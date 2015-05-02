package ro.semanticwebsearch.responsegenerator.model;

import java.util.ArrayList;

/**
 * Created by Spac on 4/22/2015.
 */
public class Location {
    private String name;
    private String description;
    private ArrayList<String> thumbnails;
    private String wikiPageExternal;
    private StringPair capital;
    private String officialLanguage;
    private String currency;
    private String callingCode;
    private Geolocation geolocation;
    private String area;
    private ArrayList<StringPair> religions;
    private String dateFounded;
    private String population;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setThumbnails(ArrayList<String> thumbnails) {
        this.thumbnails = thumbnails;
    }

    public ArrayList<String> getThumbnails() {
        return thumbnails;
    }

    public void setWikiPageExternal(String wikiPageExternal) {
        this.wikiPageExternal = wikiPageExternal;
    }

    public String getWikiPageExternal() {
        return wikiPageExternal;
    }

    public void setCapital(StringPair capital) {
        this.capital = capital;
    }

    public StringPair getCapital() {
        return capital;
    }

    public void setOfficialLanguage(String officialLanguage) {
        this.officialLanguage = officialLanguage;
    }

    public String getOfficialLanguage() {
        return officialLanguage;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCallingCode(String callingCode) {
        this.callingCode = callingCode;
    }

    public String getCallingCode() {
        return callingCode;
    }

    public void setGeolocation(Geolocation geolocation) {
        this.geolocation = geolocation;
    }

    public Geolocation getGeolocation() {
        return geolocation;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getArea() {
        return area;
    }

    public void setReligions(ArrayList<StringPair> religions) {
        this.religions = religions;
    }

    public ArrayList<StringPair> getReligions() {
        return religions;
    }

    public void setDateFounded(String dateFounded) {
        this.dateFounded = dateFounded;
    }

    public String getDateFounded() {
        return dateFounded;
    }

    public void setPopulation(String population) {
        this.population = population;
    }

    public String getPopulation() {
        return population;
    }
}
