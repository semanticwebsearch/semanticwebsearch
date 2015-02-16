package ro.semanticwebsearch.client.rest;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;

/**
 * Created by valentin.spac on 2/4/2015.
 */
class Freebase implements RestClient {

    private static String FREEBASE_ENDPOINT = "https://www.googleapis.com/freebase/v1/mqlread?query=";

    /**
     * Queries the Freebase endpoint using the {@code queryString} given as parameter
     * @param queryString query to be executed against Freebase endpoint
     * @return a {@code String} object representing a JSON which contains the response
     */
    @Override
    public String GET(String queryString) {
        Client client = ClientBuilder.newClient();
        //queryString = UriComponent.encode(queryString, UriComponent.Type.QUERY_PARAM_SPACE_ENCODED);

        String results = null;
        try {
            URI s = new URI(FREEBASE_ENDPOINT + URLEncoder.encode(queryString, "UTF-8"));
            results = client.target(s)
                    .request().get(String.class);
        } catch (URISyntaxException | UnsupportedEncodingException e) {
            //throw exception Malformed url
            e.printStackTrace();
        }

        return results;
    }

}
