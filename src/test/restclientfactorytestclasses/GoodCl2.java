package restclientfactorytestclasses;

import com.hp.hpl.jena.query.ResultSet;
import ro.semanticwebsearch.restclient.RestClient;

/**
 * Created by valentin.spac on 2/4/2015.
 */
public class GoodCl2 implements RestClient{

        public GoodCl2() {

        }

        public GoodCl2(String t) {

        }

        @Override
        public ResultSet query(String queryString) {
            return null;
        }

}
