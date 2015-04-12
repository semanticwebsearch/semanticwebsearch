package ro.semanticwebsearch.businesslogic.questions;

import org.apache.log4j.Logger;
import ro.semanticwebsearch.businesslogic.Dispatcher;
import ro.semanticwebsearch.services.QuepyResponse;
import ro.semanticwebsearch.services.QueryType;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;

/**
 * Created by Spac on 4/12/2015.
 */
public class WhoAreChildrenOf implements QuestionType {
    private static final String QUESTION = "Who are the children of";
    private static Logger log = Logger.getLogger(WhoAreChildrenOf.class.getCanonicalName());

    public WhoAreChildrenOf() {
        System.out.println("constructor who are children of");
    }

    @Override
    public void doSomethingUseful(String query)
            throws UnsupportedEncodingException, URISyntaxException, InstantiationException, IllegalAccessException {

        if (log.isInfoEnabled()) {
            log.info(QUESTION + " : " + query);
        }

        String person = query.replace(QUESTION, "");
        String infoQ = String.format(AdditionalQuestions.WHOIS.toString(), person);

        QuepyResponse qr = Dispatcher.queryQuepy(QueryType.SPARQL, infoQ);
        String additionalInfos = Dispatcher.queryService(Dispatcher.DBPEDIA, qr.getQuery());
        System.out.println(additionalInfos);

        qr = Dispatcher.queryQuepy(QueryType.MQL, infoQ);
        additionalInfos = Dispatcher.queryService(Dispatcher.FREEBASE, qr.getQuery());
        System.out.println(additionalInfos);
    }
}
