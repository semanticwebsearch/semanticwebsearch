package ro.semanticwebsearch.services;

/**
 * Created by Spac on 01 Mar 2015.
 */
public enum QueryType {
    MQL("mql"),
    SPARQL("sparql");

    private String value;

    QueryType(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return this.value;
    }

    public String getValue() {
        return value;
    }
}
