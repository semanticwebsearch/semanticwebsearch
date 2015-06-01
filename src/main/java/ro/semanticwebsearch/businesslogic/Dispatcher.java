package ro.semanticwebsearch.businesslogic;

import org.apache.log4j.Logger;
import ro.semanticwebsearch.api.rest.model.SearchDAO;
import ro.semanticwebsearch.persistence.MongoDBManager;
import ro.semanticwebsearch.responsegenerator.model.Answer;
import ro.semanticwebsearch.responsegenerator.model.Question;
import ro.semanticwebsearch.responsegenerator.parser.ParserFactory;
import ro.semanticwebsearch.responsegenerator.parser.ParserType;
import ro.semanticwebsearch.responsegenerator.parser.helper.Constants;
import ro.semanticwebsearch.services.*;
import ro.semanticwebsearch.utils.JsonUtil;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Spac on 4/8/2015.
 */
public class Dispatcher {
    private static final Map<String, String> questionParserMapping = new HashMap<>();
    public static final String DBPEDIA = "dbpedia";
    public static final String FREEBASE = "freebase";
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
        /**
         * If searchDAO.getQuery exista in db, retrieve and go
         * */
        Map<String, Object> result = new HashMap<>();
        List<Question> questions = MongoDBManager.getQuestionsByBody(searchDAO.getQuery());

        if(questions != null && questions.size() > 0) {
            List<Answer> answers = MongoDBManager.getAnswersForQuestion(questions.get(0).getId().toString(), 0,
                    Constants.MAX_CHUNK_SIZE);
            result = toAnswerMap(answers);
        } else {
            ServiceResponse response = new ServiceResponse();
            queryQuepyForSPARQL(searchDAO, response);
            queryQuepyForMQL(searchDAO, response);

            if (response.getQuestionType() != null) {
                Question question = new Question();
                question.setNumberOfAccesses(0);
                question.setBody(searchDAO.getQuery());
                question.setType(response.getQuestionType());

                result = parseServicesResponses(response, question.getId().toString());

                MongoDBManager.saveQuestion(question);
            }
        }
        return JsonUtil.pojoToString(result);
    }

    private static Map<String, Object> toAnswerMap(List<Answer> answers) {
        Map<String, Object> map = new HashMap<>();
        List<Answer> dbpedia = new ArrayList<>();
        List<Answer> freebase = new ArrayList<>();

        for(Answer answer : answers) {
            if(Constants.DBPEDIA.equalsIgnoreCase(answer.getOrigin())) {
                dbpedia.add(answer);
            } else if(Constants.FREEBASE.equalsIgnoreCase(answer.getOrigin())) {
                freebase.add(answer);
            }
        }

        map.put(Constants.DBPEDIA, dbpedia);
        map.put(Constants.FREEBASE, freebase);

        return map;
    }

    private static Map<String, Object> parseServicesResponses(ServiceResponse response, String questionId)
            throws IllegalAccessException {
        Map<String, Object> resultMap = new HashMap<>();
        Object aux;
        String entityType;

        try {
            ParserType qt = ParserFactory.getInstance().getInstanceFor(getParserForRule(response.getQuestionType()));

            aux = qt.parseDBPediaResponse(response.getDbpediaResponse(), questionId);
            if(aux != null) {
                resultMap.put(Constants.DBPEDIA, aux);
            }

            aux = qt.parseFreebaseResponse(response.getFreebaseResponse(), questionId);
            if(aux != null) {
                resultMap.put(Constants.FREEBASE, aux);
            }

            entityType = qt.getClass().getSimpleName().replace("Parser", "");
            resultMap.put("entityType", entityType);
        } catch (InstantiationException e) {
            if (log.isDebugEnabled()) {
                log.debug("Could not query for additional info ", e);
            }
        }

        return resultMap;
    }

    private static void queryQuepyForMQL(SearchDAO searchDAO, ServiceResponse response) {
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
    }

    private static void queryQuepyForSPARQL(SearchDAO searchDAO, ServiceResponse response) {
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
