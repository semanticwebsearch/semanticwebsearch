package restclientfactorytestclasses;

import com.hp.hpl.jena.query.ResultSet;
import ro.semanticwebsearch.restclient.RestClient;

/**
 * Created by valentin.spac on 2/4/2015.
 */
public class GoodCl implements RestClient {

    @Override
    public ResultSet query(String queryString) {
        return null;
    }


}
