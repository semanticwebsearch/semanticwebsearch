package ro.semanticwebsearch.responsegenerator.parser.helper;

/**
 * Created by Spac on 4/12/2015.
 */
public enum MetadataProperties {
    NAME("http://xmlns.com/foaf/0.1/name", "/type/object/name"),
    NOTABLE_FOR("http://xmlns.com/foaf/0.1/name", "/common/topic/notable_for"),
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
    STUDENTS_GRADUATES("", "/education/educational_institution/students_graduates"),
    STUDENT("", "/education/education/student"),
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
    PART_OF("http://dbpedia.org/ontology/isPartOfMilitaryConflict", "/time/event/included_in_event"),
    START_DATE("", "/time/event/start_date"),
    RELEASE_DATE("http://dbpedia.org/property/released", "/music/album/release_date"),
    GENRE("http://dbpedia.org/ontology/genre", "/music/album/genre"),
    CASUALTIES_COMBATANT("", "/military/casualties/combatant"),
    CASUALTIES_ESTIMATE("", "/military/casualties/lower_estimate"),
    CASUALTY_TYPE("", "/military/casualties/type_of_casualties"),
    CASUALTIES("", "/military/military_conflict/casualties"),
    END_DATE("", "/time/event/end_date"),
    OFFICIAL_WEBSITE("", "/common/topic/official_website"),
    PRIMARY_RELEASE("", "/music/album/primary_release"),
    TRACK_LIST("", "/music/release/track_list"),
    TRACK_ALBUM("", "/music/release_track/release"),
    TRACK_NUMBER("", "/music/release_track/track_number"),
    TRACK_LENGTH("", "/music/release_track/length"),
    MILITARY_STRENGTHS("", "/military/military_conflict/force_strengths"),
    MILITARY_CONFLICT_COMBATANTS("http://dbpedia.org/ontology/combatant", "/military/military_conflict/combatants"),
    MILITARY_COMBATANT_GROUP_COMBATANTS("", "/military/military_combatant_group/combatants"),
    MILITARY_COMMANDER("", "/military/military_command/military_commander"),
    MILITARY_PERSONNEL_INVOLVED("", "/military/military_conflict/military_personnel_involved"),
    CAPITAL("http://dbpedia.org/ontology/capital", "/location/country/capital"),
    AREA("http://dbpedia.org/ontology/PopulatedPlace/areaTotal", "/location/location/area"),
    DATE_FOUNDED("http://dbpedia.org/ontology/foundingDate", "/location/dated_location/date_founded"),
    POPULATION("http://dbpedia.org/ontology/populationTotal", "/location/statistical_region/population"),
    RELIGIONS("", "/location/statistical_region/religions"),
    RELIGIONS_PERCENTAGE("", "/location/religion_percentage/percentage"),
    RELIGIONS_TYPE("", "/location/religion_percentage/religion"),
    GEOLOCATION("", "/location/location/geolocation"),
    GEOLOCATION_LATITUDE("http://www.w3.org/2003/01/geo/wgs84_pos#lat", "/location/geocode/latitude"),
    GEOLOCATION_LONGITUDE("http://www.w3.org/2003/01/geo/wgs84_pos#long", "/location/geocode/longitude"),
    CURRENCY("http://dbpedia.org/property/currency", "/location/country/currency_used"),
    CALLING_CODE("http://dbpedia.org/property/callingCode", "/location/country/calling_code"),
    OFFICIAL_LANGUAGE("http://dbpedia.org/ontology/language", "/location/country/official_language"),
    COMMANDERS("http://dbpedia.org/ontology/commander", "/military/military_conflict/commanders"),
    SIBLING_RELATIONSHIP("http://dbpedia.org/ontology/relative", "/people/sibling_relationship/sibling"),
    SIBLING_S("", "/people/person/sibling_s"),
    ORGANIZATION_DATE_FOUNDED("", "/organization/organization/date_founded"),
    LABEL("http://www.w3.org/2000/01/rdf-schema#label", "");

    private String dbpedia;
    private String freebase;

    MetadataProperties(String dbpedia, String freebase) {
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
