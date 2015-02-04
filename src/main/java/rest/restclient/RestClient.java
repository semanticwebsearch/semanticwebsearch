package rest.restclient;

import com.hp.hpl.jena.query.ResultSet;

/**
 * Created by valentin.spac on 2/4/2015.
 */
public interface RestClient {
    ResultSet query(String queryString);
}
