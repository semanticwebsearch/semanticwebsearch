package testPackage;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

/**
 * Created by Spac on 24 Nov 2014.
 */
@Path("/test")
public class Test {

    @GET
    @Path("/path")
    public String tralala() {
        return "It's working";
    }
}
