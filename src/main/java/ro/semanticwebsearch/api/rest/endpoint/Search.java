package ro.semanticwebsearch.api.rest.endpoint;

import org.apache.log4j.Logger;
import ro.semanticwebsearch.api.rest.model.SearchDAO;
import ro.semanticwebsearch.businesslogic.Dispatcher;

import javax.ws.rs.BeanParam;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * Created by Spac on 25 Ian 2015.
 */
@Path("query")
public class Search {
    public static Logger log = Logger.getLogger(Search.class.getCanonicalName());

    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public String query(@BeanParam SearchDAO searchDAO) throws Exception {
        if (log.isInfoEnabled()) {
            log.info("Search query : " + searchDAO.toString());
        }

        return Dispatcher.executeQuery(searchDAO);
    }

    @GET
    @Path("topSearches")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public String topSearches() {
        if (log.isInfoEnabled()) {
            log.info("Top searches");
        }

        return Dispatcher.getTopSearches();
    }


}
