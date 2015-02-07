package ro.semanticwebsearch.restclient;

import org.glassfish.jersey.uri.UriComponent;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

/**
 * Created by valentin.spac on 2/4/2015.
 */
class Freebase implements RestClient {

    private static String FREEBASE_ENDPOINT = "https://www.googleapis.com/freebase/v1/mqlread";

    /**
     * Queries the Freebase endpoint using the {@code queryString} given as parameter
     * @param queryString query to be executed against Freebase endpoint
     * @return a {@code String} object representing a JSON which contains the response
     */
    @Override
    public String query(String queryString) {
        Client client = ClientBuilder.newClient();
        queryString = UriComponent.encode(queryString, UriComponent.Type.QUERY_PARAM_SPACE_ENCODED);

        String results = client.target(FREEBASE_ENDPOINT)
                .queryParam("query", queryString)
                .request().get(String.class);

        return results;
    }

}
