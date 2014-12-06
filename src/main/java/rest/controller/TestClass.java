package rest.controller;

import com.sun.jersey.api.view.Viewable;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

/**
 * Created by Spac on 24 Nov 2014.
 */
@Path("/test2")
public class TestClass {

    @GET
    @Path("/path2")
    public String tralala() {
        return "It's working 2";
    }

    @GET
    @Path("/go")
    public Viewable redirect() {
        return new Viewable("/index2.html");
    }
}
