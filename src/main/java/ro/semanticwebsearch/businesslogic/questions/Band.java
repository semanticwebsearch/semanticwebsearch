package ro.semanticwebsearch.businesslogic.questions;

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
    public Map<String, Object> doSomethingUseful(ServiceResponse response) {
        System.out.println("Do Band si atat");
        return null;
    }


}
