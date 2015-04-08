package ro.semanticwebsearch.businesslogic;

import org.apache.log4j.Logger;
import ro.semanticwebsearch.api.rest.model.SearchDAO;
import ro.semanticwebsearch.services.Service;
import ro.semanticwebsearch.services.ServiceFactory;

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

    private static String queryFreebase(SearchDAO searchDAO)
            throws IllegalAccessException, InstantiationException, UnsupportedEncodingException, URISyntaxException {
        Service service = ServiceFactory.getInstanceFor("freebase");
        return service.query(searchDAO.getQuery());
    }

    private static String queryDbpedia(SearchDAO searchDAO)
            throws IllegalAccessException, InstantiationException, UnsupportedEncodingException, URISyntaxException {
        Service service = ServiceFactory.getInstanceFor("dbpedia");
        return service.query(searchDAO.getQuery());
    }
}
