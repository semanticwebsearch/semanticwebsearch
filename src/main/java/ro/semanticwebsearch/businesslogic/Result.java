package ro.semanticwebsearch.businesslogic;

import ro.semanticwebsearch.responsegenerator.model.Answer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Spac on 6/21/2015.
 */
public class Result {
    private List<Answer> dbpedia = new ArrayList<>();
    private List<Answer> freebase = new ArrayList<>();
    private String entityType;

    public List<Answer> getDbpedia() {
        return dbpedia;
    }

    public void setDbpedia(List<Answer> dbpedia) {
        this.dbpedia = dbpedia;
    }

    public List<Answer> getFreebase() {
        return freebase;
    }

    public void setFreebase(List<Answer> freebase) {
        this.freebase = freebase;
    }

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }
}
