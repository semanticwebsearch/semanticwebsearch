package ro.semanticwebsearch.businesslogic.questions;

/**
 * Created by Spac on 4/12/2015.
 */
public enum AdditionalQuestions {
    WHOIS("Who is %s");

    private String value;

    AdditionalQuestions(String value){
        this.value = value;
    }


    @Override
    public String toString() {
        return this.value;
    }
}
