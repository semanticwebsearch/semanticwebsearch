package ro.semanticwebsearch.responsegenerator.model;

import java.util.ArrayList;

/**
 * Created by Spac on 4/18/2015.
 */
public class Person {
    private String name;//
    private String birthdate;//
    private ArrayList<String> thumbnails;//
    private String deathdate;//
    private String shortDescription;
    private String description;//
    private String wikiPageExternal;//
    private StringPair birthplace;//
    private ArrayList<StringPair> nationality;//
    private ArrayList<StringPair> education;//
    private ArrayList<StringPair> children;//
    private ArrayList<StringPair> siblings;//
    private ArrayList<StringPair> parents;//
    private ArrayList<StringPair> spouse;//
    private String notableFor;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }

    public String getDeathdate() {
        return deathdate;
    }

    public void setDeathdate(String deathdate) {
        this.deathdate = deathdate;
    }

    public StringPair getBirthplace() {
        return birthplace;
    }

    public void setBirthplace(StringPair birthplace) {
        this.birthplace = birthplace;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ArrayList<StringPair> getEducation() {
        return education;
    }

    public void setEducation(ArrayList<StringPair> education) {
        this.education = education;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public ArrayList<StringPair> getParents() {
        return parents;
    }

    public void setParents(ArrayList<StringPair> parents) {
        this.parents = parents;
    }

    public String getWikiPageExternal() {
        return wikiPageExternal;
    }

    public void setWikiPageExternal(String wikiPageExternal) {
        this.wikiPageExternal = wikiPageExternal;
    }

    public ArrayList<StringPair> getNationality() {
        return nationality;
    }

    public void setNationality(ArrayList<StringPair> nationality) {
        this.nationality = nationality;
    }

    public ArrayList<String> getThumbnails() {
        return thumbnails;
    }

    public void setThumbnails(ArrayList<String> thumbnails) {
        this.thumbnails = thumbnails;
    }

    public ArrayList<StringPair> getSpouse() {
        return spouse;
    }

    public void setSpouse(ArrayList<StringPair> spouse) {
        this.spouse = spouse;
    }

    public ArrayList<StringPair> getChildren() {
        return children;
    }

    public void setChildren(ArrayList<StringPair> children) {
        this.children = children;
    }

    public void setNotableFor(String notableFor) {
        this.notableFor = notableFor;
    }

    public String getNotableFor() {
        return notableFor;
    }

    public ArrayList<StringPair> getSiblings() {
        return siblings;
    }

    public void setSiblings(ArrayList<StringPair> siblings) {
        this.siblings = siblings;
    }
}
