package ro.semanticwebsearch.businesslogic;

import org.apache.log4j.Logger;
import ro.semanticwebsearch.api.rest.model.SearchDAO;
import ro.semanticwebsearch.businesslogic.questions.QuestionFactory;
import ro.semanticwebsearch.businesslogic.questions.QuestionType;
import ro.semanticwebsearch.services.*;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;

/**
 * Created by Spac on 4/8/2015.
 */
public class Dispatcher {
    public static Logger log = Logger.getLogger(Dispatcher.class.getCanonicalName());
    public static final String DBPEDIA = "dbpedia";
    public static final String FREEBASE = "freebase";

    public static void executeQuery(SearchDAO searchDAO) throws IllegalAccessException {

        if (log.isInfoEnabled()) {
            log.info("Execute query : " + searchDAO.toString());
        }

        String[] responses = new String[2];
        String questionType = "";
        try {
            QuepyResponse quepyResponse = queryQuepy(QueryType.SPARQL, searchDAO.getQuery());
            responses[0] = queryService(DBPEDIA, quepyResponse.getQuery());
            questionType = sanitizeRule(quepyResponse.getRule());

            if (log.isInfoEnabled()) {
                log.info("DBPedia quepy response: " + quepyResponse.toString());
            }

        } catch (InstantiationException | IllegalArgumentException |
                IllegalAccessException | UnsupportedEncodingException | URISyntaxException e) {
            if (log.isDebugEnabled()) {
                log.debug("Could not query DBPedia ", e);
            }
        }

        try {
            QuepyResponse quepyResponse = queryQuepy(QueryType.MQL, searchDAO.getQuery());
            responses[0] = queryService(FREEBASE, quepyResponse.getQuery());
            questionType = sanitizeRule(quepyResponse.getRule());

            if (log.isInfoEnabled()) {
                log.info("Freebase quepy response: " + quepyResponse.toString());
            }

        } catch (InstantiationException | IllegalArgumentException |
                IllegalAccessException | UnsupportedEncodingException | URISyntaxException e) {
            if (log.isDebugEnabled()) {
                log.debug("Could not query DBPedia ", e);
            }
        }

        System.out.println("DBPedia : " + responses[0]);
        System.out.println("Freebase : " + responses[1]);

        try {
            QuestionType qt = QuestionFactory.getInstance().getInstanceFor(questionType);
            qt.doSomethingUseful(searchDAO.getQuery());

        } catch (UnsupportedEncodingException | URISyntaxException | InstantiationException e) {
            if (log.isDebugEnabled()) {
                log.debug("Could not query for additional info ", e);
            }
        }

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
