package ro.semanticwebsearch.responsegenerator.parser;

/**
 * Created by Spac on 4/12/2015.
 */
public enum MetadataProperties {
    NAME("http://xmlns.com/foaf/0.1/name", "/type/object/name"),
    BIRTHDATE("http://dbpedia.org/ontology/birthDate", "/people/person/date_of_birth"),
    DEATHDATE("http://dbpedia.org/ontology/deathDate", "/people/deceased_person/date_of_death"),
    BIRTHPLACE("http://dbpedia.org/ontology/birthPlace", "/people/person/place_of_birth"),
    ABSTRACT("http://dbpedia.org/ontology/abstract", "/common/topic/description"),
    SHORT_DESCRIPTION("http://dbpedia.org/property/shortDescription", "/common/topic/notable_for"),
    PRIMARY_TOPIC_OF("http://xmlns.com/foaf/0.1/isPrimaryTopicOf", "/common/topic/topic_equivalent_webpage"),
    EDUCATION_ONTOLOGY("http://dbpedia.org/ontology/education", "/people/person/education"),
    ALMA_MATER("http://dbpedia.org/ontology/almaMater", ""),
    NATIONALITY("http://dbpedia.org/ontology/nationality", "/people/person/nationality"),
    THUMBNAIL("http://dbpedia.org/ontology/thumbnail", "/common/topic/image"),
    SPOUSE("http://dbpedia.org/ontology/spouse", "/people/person/spouse_s"),
    PARENTS("http://dbpedia.org/property/parents", "/people/person/parents"),
    CHILDREN("http://dbpedia.org/property/children", "/people/person/children"),
    EDUCATIONAL_INSTITUTION("", "/education/education/institution"),
    DEGREE("", "/education/education/degree"),
    WEAPON_LENGTH("http://dbpedia.org/ontology/Weapon/length", ""),
    WEAPON_WEIGHT("http://dbpedia.org/ontology/Weapon/weight", ""),
    ORIGIN("http://dbpedia.org/ontology/origin", ""),
    CALIBER("http://dbpedia.org/property/caliber", ""),
    DESIGNER("http://dbpedia.org/property/designer", ""),
    PROP_SERVICE("http://dbpedia.org/property/service", ""),
    RESULT("http://dbpedia.org/ontology/result", ""),
    DATE("http://dbpedia.org/ontology/date", ""),
    PLACE("http://dbpedia.org/ontology/place", "/time/event/locations"),
    PART_OF("", "/time/event/included_in_event"),
    START_DATE("", "/time/event/start_date"),
    CASUALTIES_COMBATANT("", "/military/casualties/combatant"),
    CASUALTIES_ESTIMATE("", "/military/casualties/lower_estimate"),
    CASUALTY_TYPE("", "/military/casualties/type_of_casualties"),
    CASUALTIES("", "/military/military_conflict/casualties"),
    END_DATE("", "/time/event/end_date"),
    MILITARY_STRENGTHS("", "/military/military_conflict/force_strengths"),
    MILITARY_CONFLICT_COMBATANTS("", "/military/military_conflict/combatants"),
    MILITARY_COMBATANT_GROUP_COMBATANTS("", "/military/military_combatant_group/combatants"),
    MILITARY_COMMANDER("", "/military/military_command/military_commander"),
    COMMANDERS("http://dbpedia.org/ontology/commander", "/military/military_conflict/commanders"),
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
