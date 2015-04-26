package ro.semanticwebsearch.responsegenerator;

import org.apache.log4j.Logger;
import ro.semanticwebsearch.responsegenerator.model.Person;

/**
 * Created by Spac on 4/26/2015.
 */
public class WeaponUsedByCountryInConflict extends AbstractQuestionType {

    private static Logger log = Logger.getLogger(WeaponUsedByCountryInConflict.class.getCanonicalName());

    public Person parseFreebaseResponse(String freebaseResponse) {
        return null;
    }

    public Person parseDBPediaResponse(String dbpediaResponse) {
        return null;
    }
}
