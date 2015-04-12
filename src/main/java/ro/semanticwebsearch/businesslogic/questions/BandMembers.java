package ro.semanticwebsearch.businesslogic.questions;

/**
 * Created by Spac on 4/8/2015.
 */
public class BandMembers extends AbstractQuestion {


    public BandMembers(){
        System.out.println("Constructor Band memebers");
    }

    @Override
    public void doSomethingUseful(String query) {
        System.out.println("Do BandMembers");
    }

}
