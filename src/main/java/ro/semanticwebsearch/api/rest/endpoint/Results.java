package ro.semanticwebsearch.api.rest.endpoint;

import org.apache.log4j.Logger;
import ro.semanticwebsearch.api.rest.model.ResultsDAO;
import ro.semanticwebsearch.businesslogic.Dispatcher;

import javax.ws.rs.BeanParam;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * Created by Spac on 6/2/2015.
 */
@Path("results")
public class Results {
    public static Logger log = Logger.getLogger(Results.class.getCanonicalName());

    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public String getMoreResults(@BeanParam ResultsDAO resultsDAO) {
        if (log.isInfoEnabled()) {
            log.info("More results for : " + resultsDAO.toString());
        }
        return Dispatcher.getMoreResults(resultsDAO);
    }
}
