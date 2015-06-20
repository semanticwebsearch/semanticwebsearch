package ro.semanticwebsearch.responsegenerator.parser;

import org.apache.log4j.Logger;

/**
 * Created by Spac on 4/26/2015.
 */
abstract class AbstractParserType implements ParserType {

    private static Logger log = Logger.getLogger(AbstractParserType.class.getCanonicalName());
    protected String TYPE;

    public String getType() {
        return TYPE;
    }
}
