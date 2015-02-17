package ro.semanticwebsearch.services;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;

/**
 * Created by valentin.spac on 2/4/2015.
 */
public interface Service {

    String query(String queryString) throws UnsupportedEncodingException, URISyntaxException;
}
