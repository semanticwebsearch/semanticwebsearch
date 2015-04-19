package ro.semanticwebsearch.businesslogic.questions;

/**
 * Created by Spac on 4/12/2015.
 */
public enum MetadataProperties {
    NAME("http://xmlns.com/foaf/0.1/name", ""),
    BIRTHDATE("http://dbpedia.org/ontology/birthDate", ""),
    DEATHDATE("http://dbpedia.org/ontology/deathDate", ""),
    BIRTHPLACE("http://dbpedia.org/ontology/birthPlace", ""),
    ABSTRACT("http://dbpedia.org/ontology/abstract", ""),
    SHORT_DESCRIPTION("http://dbpedia.org/property/shortDescription", ""),
    PRIMARY_TOPIC_OF("http://xmlns.com/foaf/0.1/isPrimaryTopicOf", ""),
    EDUCATION_ONTOLOGY("http://dbpedia.org/ontology/education", ""),
    ALMA_MATER("http://dbpedia.org/ontology/almaMater", ""),
    NATIONALITY("http://dbpedia.org/ontology/nationality", ""),
    LABEL("http://www.w3.org/2000/01/rdf-schema#label", "");

    private String dbpedia;
    private String freebase;

    MetadataProperties(String dbpedia, String freebase){
        this.dbpedia = dbpedia;
        this.freebase = freebase;
    }

    public String getDbpedia() {
        return dbpedia;
    }

    public String getFreebase() {
        return freebase;
    }


    @Override
    public String toString() {
        return "[ " + this.dbpedia + ", " + this.freebase + " ]";
    }
}
