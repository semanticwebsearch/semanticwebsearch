package ro.semanticwebsearch.services;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;

/**
 * Created by Spac on 17 Feb 2015.
 */
class Quepy implements Service {

    /**
     * Queries the Quepy endpoint using the {@code queryString} given as parameter
     * @param queryString query to be executed against Quepy server
     * @return a {@code String} object representing the response (transformed query)
     */
    @Override
    public String query(String queryString) throws UnsupportedEncodingException, URISyntaxException {
        return null;
    }
}
