package ro.semanticwebsearch.api.rest.endpoint;

import org.apache.log4j.Logger;
import ro.semanticwebsearch.api.rest.model.Feedback;
import ro.semanticwebsearch.api.rest.model.ResultsDAO;
import ro.semanticwebsearch.businesslogic.Dispatcher;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Created by Spac on 6/2/2015.
 */
@Path("question/{questionId}")
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

    @GET
    @Path("numberOfAccesses")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public long getNumberOfAccesses(@PathParam("questionId")String questionId) {
        if (log.isInfoEnabled()) {
            log.info("Number of accesses for : " + questionId);
        }
        return Dispatcher.getAccessesNumberFor(questionId);
    }

    @POST
    @Path("{answerId}/like/{feedback}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response updateLike(@BeanParam Feedback feedback) {
        if (log.isInfoEnabled()) {
            log.info("Feedback for : " + feedback.getAnswerId());
        }
        if(Dispatcher.updateLike(feedback) > 0) {
            return Response.ok().build();
        } else {
            return Response.serverError().build();
        }
    }

    @POST
    @Path("{answerId}/dislike/{feedback}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response updateDislike(@BeanParam Feedback feedback) {
        if (log.isInfoEnabled()) {
            log.info("Feedback for : " + feedback.getAnswerId());
        }
        if(Dispatcher.updateDislike(feedback) > 0) {
            return Response.ok().build();
        } else {
            return Response.serverError().build();
        }
    }
}
