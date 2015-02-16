package ro.semanticwebsearch.client.rest;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

/**
 * Created by Spac on 16 Feb 2015.
 */
class GenericClient implements RestClient{

    @Override
    public String GET(String queryString) {
        Client client = ClientBuilder.newClient();
        return null;
    }

   /* public String GET(String url, String[][] queryString) {
        Client client = ClientBuilder.newClient();

        queryString = UriComponent.encode(queryString, UriComponent.Type.QUERY_PARAM_SPACE_ENCODED);
        MultivaluedHashMap
        String results = client.target(FREEBASE_ENDPOINT)
                .queryParam("query", queryString)
                .request().get(String.class);
        return null;
    }*/
}
