package rest.controller;

import rest.model.output.QueryResponse;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

/**
 * Created by Spac on 25 Ian 2015.
 */
@Path("query")
public class Search {

    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public QueryResponse query(@QueryParam("q")String queryString) {
        System.out.println(queryString);
        return new QueryResponse();
    }
}
