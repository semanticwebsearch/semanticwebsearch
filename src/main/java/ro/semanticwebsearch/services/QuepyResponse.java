package ro.semanticwebsearch.services;

/**
 * Created by Spac on 4/11/2015.
 */
public class QuepyResponse {
    String query;
    String rule;

    public QuepyResponse(){}

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getRule() {
        return rule;
    }

    public void setRule(String rule) {
        this.rule = rule;
    }
}
