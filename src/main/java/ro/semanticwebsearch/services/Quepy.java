package ro.semanticwebsearch.services;

import ro.semanticwebsearch.services.exception.InvalidConfigurationFileException;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;

/**
 * Created by Spac on 17 Feb 2015.
 */
public class Quepy {

    private static String QUEPY_ENDPOINT;
    private WebTarget client;

    /**
     * Initializes the quepy endpoint read from properties file and
     * sets the client target to that endpoint
     */
    static {
        try {
            QUEPY_ENDPOINT = PropertiesLoader.getInstance().getProperties().getProperty("quepy_endpoint");
        } catch (IOException e) {
            throw new RuntimeException("Error reading services.properties file", e);
        }

        if (QUEPY_ENDPOINT == null) {
            throw new InvalidConfigurationFileException("[quepy_endpoint] property was not set.");
        }
    }

    public Quepy(QueryType type, String queryString) {
        client = ClientBuilder.newClient().target(QUEPY_ENDPOINT);
        client = client.queryParam("type", type).queryParam("q", queryString);
    }

    public Quepy(String type, String queryString) {
        client = ClientBuilder.newClient().target(QUEPY_ENDPOINT);
        client = client.queryParam("type", type).queryParam("q", queryString);
    }

    /**
     * Queries the Quepy endpoint using the {@code queryString} given as parameter
     *
     * @return a {@code String} object representing the response (transformed query)
     */
    public QuepyResponse query() throws UnsupportedEncodingException, URISyntaxException {
        return client.request(MediaType.APPLICATION_JSON_TYPE).get(QuepyResponse.class);
    }

}
