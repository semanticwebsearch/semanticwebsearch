package ro.semanticwebsearch.services;

import ro.semanticwebsearch.services.exception.InvalidConfigurationFileException;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;

/**
 * Created by valentin.spac on 2/4/2015.
 */
class Freebase implements Service {

    private static String FREEBASE_ENDPOINT;

    /**
     * Queries the Freebase endpoint using the {@code queryString} given as parameter
     * @param queryString query to be executed against Freebase endpoint
     * @return a {@code String} object representing a JSON which contains the response
     */
    @Override
    public String query(String queryString) throws UnsupportedEncodingException, URISyntaxException {

        if(FREEBASE_ENDPOINT == null) {
            initialize();
        }

        Client client = ClientBuilder.newClient();
        //queryString = UriComponent.encode(queryString, UriComponent.Type.QUERY_PARAM_SPACE_ENCODED);

        URI s = new URI(FREEBASE_ENDPOINT + URLEncoder.encode(queryString, "UTF-8"));
        return client.target(s)
                .request()
                .get(String.class);

    }

    private void initialize() {
        try {
            FREEBASE_ENDPOINT = PropertiesLoader.getInstance().getProperties().getProperty("freebase_endpoint");

        } catch (IOException e) {
            throw new RuntimeException("Error reading services.properties file", e);
        }

        if(FREEBASE_ENDPOINT == null) {
            throw new InvalidConfigurationFileException("[freebase_endpoint] property was not set.");
        }
    }

}
