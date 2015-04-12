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

    public static void executeQuery(SearchDAO searchDAO) {
        if (log.isInfoEnabled()) {
            log.info("Execute query : " + searchDAO.toString());
        }
        mappingStuff();

        String[] responses = new String[2];
        try {
            responses[0] = queryDbpedia(searchDAO);
        } catch (InstantiationException | IllegalArgumentException |
                IllegalAccessException | UnsupportedEncodingException | URISyntaxException e) {
            if (log.isDebugEnabled()) {
                log.debug("Could not query DBPedia ", e);
            }
        }

        try {
            responses[1] = queryFreebase(searchDAO);
        } catch (InstantiationException | IllegalArgumentException |
                IllegalAccessException | UnsupportedEncodingException | URISyntaxException e) {
            if (log.isDebugEnabled()) {
                log.debug("Could not query DBPedia ", e);
            }
        }

        System.out.println("DBPedia : " + responses[0]);
        System.out.println("Freebase : " + responses[1]);


    }

    private static void mappingStuff() {
        try {
            QuestionType q = QuestionFactory.getInstance().getInstanceFor("band");
            QuestionType m = QuestionFactory.getInstance().getInstanceFor("bandmembers");
            q.doSomething();
            m.doSomething();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private static String queryFreebase(SearchDAO searchDAO)
            throws IllegalAccessException, InstantiationException, UnsupportedEncodingException, URISyntaxException {
        Service service = ServiceFactory.getInstanceFor("freebase");
        Quepy quepy = new Quepy(QueryType.MQL, searchDAO.getQuery());
        QuepyResponse response = quepy.query();


        String qt = sanitizeRule(response.getRule());
        QuestionType q = QuestionFactory.getInstance().getInstanceFor(qt);
        q.doSomething();

        return service.query(response.getQuery());
    }

    private static String queryDbpedia(SearchDAO searchDAO)
            throws IllegalAccessException, InstantiationException, UnsupportedEncodingException, URISyntaxException {
        Service service = ServiceFactory.getInstanceFor("dbpedia");
        Quepy quepy = new Quepy(QueryType.SPARQL, searchDAO.getQuery());
        QuepyResponse response = quepy.query();

        //get the type of the question and find more infos
        String qt = sanitizeRule(response.getRule());
        QuestionType q = QuestionFactory.getInstance().getInstanceFor(qt);
        q.doSomething();


        return service.query(response.getQuery());
    }

    private static String sanitizeRule(String rule) {
        return rule.replace("Question", "").toLowerCase();
    }
}
