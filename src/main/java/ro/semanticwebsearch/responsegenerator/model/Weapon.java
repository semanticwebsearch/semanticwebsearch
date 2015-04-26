package ro.semanticwebsearch.responsegenerator.model;

import java.util.ArrayList;

/**
 * Created by Spac on 4/26/2015.
 */
public class Weapon {
    private String name;
    private ArrayList<String> thumbnails;
    private String description;
    private String wikiPageExternal;
    private String weight;
    private String length;
    private StringPair designer;
    private StringPair origin;
    private String caliber;
    private String service;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<String> getThumbnails() {
        return thumbnails;
    }

    public void setThumbnails(ArrayList<String> thumbnails) {
        this.thumbnails = thumbnails;
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

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getLength() {
        return length;
    }

    public void setLength(String length) {
        this.length = length;
    }


    public StringPair getDesigner() {
        return designer;
    }

    public void setDesigner(StringPair designer) {
        this.designer = designer;
    }

    public StringPair getOrigin() {
        return origin;
    }

    public void setOrigin(StringPair origin) {
        this.origin = origin;
    }

    public String getCaliber() {
        return caliber;
    }

    public void setCaliber(String caliber) {
        this.caliber = caliber;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }
}
