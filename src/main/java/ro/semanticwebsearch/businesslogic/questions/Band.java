package ro.semanticwebsearch.businesslogic.questions;

import com.fasterxml.jackson.databind.JsonNode;
import ro.semanticwebsearch.businesslogic.ServiceResponse;

import java.util.Map;

/**
 * Created by Spac on 4/8/2015.
 */
public class Band extends AbstractQuestion {


    public Band(){
        System.out.println("Band");
    }

    @Override
    public Map<String, JsonNode> doSomethingUseful(ServiceResponse response) {
        System.out.println("Do Band si atat");
        return null;
    }


}
