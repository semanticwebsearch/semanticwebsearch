package ro.semanticwebsearch.businesslogic;

import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;

/**
 * Created by Spac on 4/18/2015.
 */
public class Person {
    private String name;
    private String birthdate;
    private String deathdate;
    private Pair<String, String> birthplace;
    private String description;
    private Pair<String, String> education;
    private Pair<String, String> nationality;
    private String shortDescription;
    private ArrayList<String> parents;
    private String wikiPageExternal;

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

    public Pair<String, String> getBirthplace() {
        return birthplace;
    }

    public void setBirthplace(Pair<String, String> birthplace) {
        this.birthplace = birthplace;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public Pair<String, String> getEducation() {
        return education;
    }

    public void setEducation(Pair<String, String> education) {
        this.education = education;
    }



    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public ArrayList<String> getParents() {
        return parents;
    }

    public void setParents(ArrayList<String> parents) {
        this.parents = parents;
    }

    public String getWikiPageExternal() {
        return wikiPageExternal;
    }

    public void setWikiPageExternal(String wikiPageExternal) {
        this.wikiPageExternal = wikiPageExternal;
    }

    public Pair<String, String> getNationality() {
        return nationality;
    }

    public void setNationality(Pair<String, String> nationality) {
        this.nationality = nationality;
    }
}
