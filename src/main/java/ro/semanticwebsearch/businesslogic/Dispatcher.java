package ro.semanticwebsearch.businesslogic;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.Logger;
import ro.semanticwebsearch.api.rest.model.SearchDAO;
import ro.semanticwebsearch.responsegenerator.parser.ParserType;
import ro.semanticwebsearch.responsegenerator.parser.ParserFactory;
import ro.semanticwebsearch.services.*;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Spac on 4/8/2015.
 */
public class Dispatcher {
    public static final String DBPEDIA = "dbpedia";
    public static final String FREEBASE = "freebase";
    private static final Map<String, String> questionParserMapping = new HashMap<>();
    public static Logger log = Logger.getLogger(Dispatcher.class.getCanonicalName());

    /**
     * Queries Quepy to transform natural language into sparql or mql.
     * After that queries DBPedia and Freebase using the criteria given by the user and also gets additional
     * information about the entitites found in response
     *
     * @param searchDAO the searching criteria selected by the user
     * @return string representing the JSON response
     * @throws IllegalAccessException
     */
    public static String executeQuery(SearchDAO searchDAO) throws IllegalAccessException {

        if (log.isInfoEnabled()) {
            log.info("Execute query : " + searchDAO.toString());
        }

        ServiceResponse response = new ServiceResponse();
        //region querying DBPedia
        try {
            QuepyResponse quepyResponse = queryQuepy(QueryType.SPARQL, searchDAO.getQuery());
            response.setDbpediaResponse(queryService(DBPEDIA, quepyResponse.getQuery()));
            response.setQuestionType(quepyResponse.getRule());

            if (log.isInfoEnabled()) {
                log.info("DBPedia quepy response: " + quepyResponse.toString());
            }

        } catch (InstantiationException | IllegalArgumentException |
                IllegalAccessException | UnsupportedEncodingException | URISyntaxException e) {
            if (log.isDebugEnabled()) {
                log.debug("Could not query DBPedia ", e);
            }
        }
        //endregion

        //region querying FREEBASE
        try {
            QuepyResponse quepyResponse = queryQuepy(QueryType.MQL, searchDAO.getQuery());
            response.setFreebaseResponse(queryService(FREEBASE, quepyResponse.getQuery()));

            if (!response.getFreebaseResponse().trim().isEmpty()) {
                response.setQuestionType(quepyResponse.getRule());
            }

            if (log.isInfoEnabled()) {
                log.info("Freebase quepy response: " + quepyResponse.toString());
            }

        } catch (InstantiationException | IllegalArgumentException |
                IllegalAccessException | UnsupportedEncodingException | URISyntaxException e) {
            if (log.isDebugEnabled()) {
                log.debug("Could not query DBPedia ", e);
            }
        }
        //endregion

        if (response.getQuestionType() != null) {
            Map<String, Object> res = null;
            String entityType = null;
            try {
                ParserType qt = ParserFactory.getInstance().getInstanceFor(getParserForRule(response.getQuestionType()));
                res = qt.doSomethingUseful(response);
                entityType = qt.getClass().getSimpleName().replace("Parser", "");
            } catch (UnsupportedEncodingException | URISyntaxException | InstantiationException e) {
                if (log.isDebugEnabled()) {
                    log.debug("Could not query for additional info ", e);
                }
            }
            if(res != null) {
                res.put("entityType", entityType);
                ObjectMapper mapper = new ObjectMapper();
                try {
                    return mapper.writeValueAsString(res);
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            }
        }

        return "";
    }

    public static String queryService(String serviceType, String query)
            throws IllegalAccessException, InstantiationException, UnsupportedEncodingException, URISyntaxException {
        if (query == null || query.trim().isEmpty() || "[{}]".equals(query)) {
            return "";
        }

        Service service = ServiceFactory.getInstanceFor(serviceType);
        return service.query(query);
    }


    private static String getParserForRule(String rule) {
        if (rule != null) {
            rule = rule.replace("Question", "").toLowerCase();
            rule = rule.replaceFirst("whatis", "");
            return questionParserMapping.get(rule).toLowerCase();
        } else {
            return "";
        }
    }

    public static QuepyResponse queryQuepy(QueryType queryType, String query)
            throws UnsupportedEncodingException, URISyntaxException {
        Quepy quepy = new Quepy(queryType, query);
        return quepy.query();
    }

    public static QuepyResponse queryQuepy(String queryType, String query)
            throws UnsupportedEncodingException, URISyntaxException {
        Quepy quepy = new Quepy(queryType, query);
        return quepy.query();
    }

    static {
        questionParserMapping.put("whoarechildrenof", "ChildrenOfParser");
        questionParserMapping.put("whois", "PersonParser");
        questionParserMapping.put("personthattookpartinconflict", "PersonParser");
        questionParserMapping.put("conflictthattookplaceincountry", "ConflictParser");
        questionParserMapping.put("weaponusedbycountryinconflict", "WeaponParser");
        questionParserMapping.put("location", "LocationParser");
        questionParserMapping.put("albumsof", "AlbumParser");
    }
}
