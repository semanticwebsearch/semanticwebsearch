package ro.semanticwebsearch.businesslogic;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.Logger;
import ro.semanticwebsearch.api.rest.model.SearchDAO;
import ro.semanticwebsearch.businesslogic.questions.QuestionFactory;
import ro.semanticwebsearch.businesslogic.questions.QuestionType;
import ro.semanticwebsearch.services.*;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.Map;

/**
 * Created by Spac on 4/8/2015.
 */
public class Dispatcher {
    public static Logger log = Logger.getLogger(Dispatcher.class.getCanonicalName());
    public static final String DBPEDIA = "dbpedia";
    public static final String FREEBASE = "freebase";

    public static String executeQuery(SearchDAO searchDAO) throws IllegalAccessException {

        if (log.isInfoEnabled()) {
            log.info("Execute query : " + searchDAO.toString());
        }

        ServiceResponse response = new ServiceResponse();
        //region querying DBPedia
        try {
            QuepyResponse quepyResponse = queryQuepy(QueryType.SPARQL, searchDAO.getQuery());
            response.setDbpediaResponse(queryService(DBPEDIA, quepyResponse.getQuery()));
            response.setQuestionType(quepyResponse.getRule());

            if (log.isInfoEnabled()) {
                log.info("DBPedia quepy response: " + quepyResponse.toString());
            }

        } catch (InstantiationException | IllegalArgumentException |
                IllegalAccessException | UnsupportedEncodingException | URISyntaxException e) {
            if (log.isDebugEnabled()) {
                log.debug("Could not query DBPedia ", e);
            }
        }
        //endregion

        //region querying FREEBASE
        try {
            QuepyResponse quepyResponse = queryQuepy(QueryType.MQL, searchDAO.getQuery());
            response.setFreebaseResponse(queryService(FREEBASE, quepyResponse.getQuery()));
            response.setQuestionType(quepyResponse.getRule());

            if (log.isInfoEnabled()) {
                log.info("Freebase quepy response: " + quepyResponse.toString());
            }

        } catch (InstantiationException | IllegalArgumentException |
                IllegalAccessException | UnsupportedEncodingException | URISyntaxException e) {
            if (log.isDebugEnabled()) {
                log.debug("Could not query DBPedia ", e);
            }
        }
        //endregion

        System.out.println("DBPedia : " + response.getDbpediaResponse());
        System.out.println("Freebase : " + response.getFreebaseResponse());
        Map<String, JsonNode> res = null;
        try {
            QuestionType qt = QuestionFactory.getInstance().getInstanceFor(sanitizeRule(response.getQuestionType()));
            res = qt.doSomethingUseful(response);

        } catch (UnsupportedEncodingException | URISyntaxException | InstantiationException e) {
            if (log.isDebugEnabled()) {
                log.debug("Could not query for additional info ", e);
            }
        }

        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(res);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String queryService(String serviceType, String query)
            throws IllegalAccessException, InstantiationException, UnsupportedEncodingException, URISyntaxException {
        Service service = ServiceFactory.getInstanceFor(serviceType);
        return service.query(query);
    }


    private static String sanitizeRule(String rule) {
        return rule.replace("Question", "").toLowerCase();
    }

    public static QuepyResponse queryQuepy(QueryType queryType, String query)
            throws UnsupportedEncodingException, URISyntaxException {
        Quepy quepy = new Quepy(queryType, query);
        return quepy.query();
    }

    public static QuepyResponse queryQuepy(String queryType, String query)
            throws UnsupportedEncodingException, URISyntaxException {
        Quepy quepy = new Quepy(queryType, query);
        return quepy.query();
    }
}
