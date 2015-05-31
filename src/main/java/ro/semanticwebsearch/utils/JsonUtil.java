package ro.semanticwebsearch.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.Logger;

/**
 * Created by Spac on 5/29/2015.
 */
public class JsonUtil {
    public static Logger log = Logger.getLogger(JsonUtil.class.getCanonicalName());

    public static String pojoToString(Object obj) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            log.info("Could not serialize object. See stacktrace for more details: " + e);
            return null;
        }
    }
}
