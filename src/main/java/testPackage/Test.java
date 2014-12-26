package testPackage;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("/test")
public class Test {

    @GET
    @Path("/path")
    public String tralala() {
        return "It's working";
    }
}
