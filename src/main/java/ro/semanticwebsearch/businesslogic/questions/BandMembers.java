package ro.semanticwebsearch.businesslogic.questions;

import ro.semanticwebsearch.businesslogic.ServiceResponse;

import java.util.Map;

/**
 * Created by Spac on 4/8/2015.
 */
public class BandMembers extends AbstractQuestion {


    public BandMembers(){
        System.out.println("Constructor Band memebers");
    }

    @Override
    public Map<String, Object> doSomethingUseful(ServiceResponse response) {
        System.out.println("Do BandMembers");
        return null;
    }

}
