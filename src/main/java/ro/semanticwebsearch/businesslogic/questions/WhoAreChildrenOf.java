package ro.semanticwebsearch.businesslogic.questions;

/**
 * Created by Spac on 4/12/2015.
 */
public class WhoAreChildrenOf implements QuestionType {

    public WhoAreChildrenOf() {
        System.out.println("constructor who are children of");
    }
    @Override
    public void doSomething() {
        System.out.println("Who are the children of called");
    }
}
