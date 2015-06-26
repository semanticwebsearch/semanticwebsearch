package ro.semanticwebsearch.businesslogic;

import org.apache.log4j.Logger;
import org.bson.types.ObjectId;
import ro.semanticwebsearch.api.rest.model.SearchDAO;
import ro.semanticwebsearch.persistence.MongoDBManager;
import ro.semanticwebsearch.responsegenerator.model.Answer;
import ro.semanticwebsearch.responsegenerator.model.Question;
import ro.semanticwebsearch.responsegenerator.parser.ParserFactory;
import ro.semanticwebsearch.responsegenerator.parser.ParserType;
import ro.semanticwebsearch.responsegenerator.parser.helper.Constants;
import ro.semanticwebsearch.services.*;
import ro.semanticwebsearch.utils.Config;
import ro.semanticwebsearch.utils.JsonUtil;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.*;

/**
 * Created by Spac on 4/8/2015.
 */
public class Dispatcher {
    private static final Map<String, String> questionParserMapping = new HashMap<>();
    public static Logger log = Logger.getLogger(Dispatcher.class.getCanonicalName());
    private static int RETENTION_TIME;

    static {
        String aux = Config.getProperty("data_lease_time");
        try {
            int retention_days = Integer.parseInt(aux);
            RETENTION_TIME = retention_days * Constants.SECONDS_IN_A_DAY;
        } catch (Exception e) {
            RETENTION_TIME = Constants.SECONDS_IN_A_DAY;
        }

    }

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
        Result result;
        List<Question> questions = MongoDBManager.getQuestionsByBody(searchDAO.getQuery());
        List<Answer> answers = null;
        Question question = null;

        if(questions != null && questions.size() > 0) {
            answers = MongoDBManager.getAnswersForQuestion(questions.get(0).getId(), 0,
                    Constants.MAX_CHUNK_SIZE);
            question = questions.get(0);
        }


        if(answers == null || answers.size() == 0) {
            result = queryServices(searchDAO, question);
        } else if(isOutdated(answers)) {
            MongoDBManager.deleteAnswers(answers);
            result = queryServices(searchDAO, question);
        } else {
            MongoDBManager.updateAccessNumberOfQuestion(question);
            result = toResultObject(answers);
        }

        return JsonUtil.pojoToString(result);
    }

    private static Result queryServices(SearchDAO searchDAO,  Question question)
            throws IllegalAccessException {
        Result result = new Result();
        ServiceResponse response = new ServiceResponse();
        queryDBPedia(searchDAO, response);
        queryFreebase(searchDAO, response);

        if (response.getQuestionType() != null) {
            if(question == null) {
                question = new Question();
                question.setNumberOfAccesses(1);
                question.setBody(searchDAO.getQuery());
                question.setType(response.getQuestionType());
                MongoDBManager.saveQuestion(question);
            } else {
                MongoDBManager.updateAccessNumberOfQuestion(question);
            }
            result = parseServicesResponses(response, question.getId());
        }
        return result;
    }

    private static boolean isOutdated(List<Answer> answers) {
        if(answers != null && answers.size() > 0) {
            Answer answer = answers.get(0);
            ObjectId answerId = new ObjectId(answer.getId());
            Date currDate = new Date();
            int currTimestamp = (int)(currDate.getTime()/1000);
            return currTimestamp - answerId.getTimestamp() > RETENTION_TIME;
        }

        return false;
    }

    public static Result toResultObject(List<Answer> answers) {
        List<Answer> dbpedia = new ArrayList<>();
        List<Answer> freebase = new ArrayList<>();
        String entityType = null;

        for(Answer answer : answers) {
            if(Constants.DBPEDIA.equalsIgnoreCase(answer.getOrigin())) {
                dbpedia.add(answer);
            } else if(Constants.FREEBASE.equalsIgnoreCase(answer.getOrigin())) {
                freebase.add(answer);
            }

            if(entityType == null) {
                entityType = answer.getType();
            }
        }

        Result result = new Result();
        result.setEntityType(entityType);
        result.setDbpedia(dbpedia);
        result.setFreebase(freebase);

        return result;
    }

    private static Result parseServicesResponses(ServiceResponse response, String questionId)
            throws IllegalAccessException {
        List<Answer> aux;
        Result result = new Result();
        try {
            ParserType qt = ParserFactory.getInstance().getInstanceFor(getParserForRule(response.getQuestionType()));

            aux = qt.parseDBPediaResponse(response.getDbpediaResponse(), questionId);
            if(aux != null) {
                result.setDbpedia(aux);
            }

            aux = qt.parseFreebaseResponse(response.getFreebaseResponse(), questionId);
            if(aux != null) {
                result.setFreebase(aux);
            }

            result.setEntityType(qt.getType());
        } catch (InstantiationException e) {
            if (log.isDebugEnabled()) {
                log.debug("Could not query for additional info ", e);
            }
        }

        return result;
    }

    private static void queryFreebase(SearchDAO searchDAO, ServiceResponse response) {
        try {
            QuepyResponse quepyResponse = queryQuepy(QueryType.MQL, searchDAO.getQuery());
            response.setFreebaseResponse(queryService(Constants.FREEBASE, quepyResponse.getQuery()));

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

    private static void queryDBPedia(SearchDAO searchDAO, ServiceResponse response) {
        try {
            QuepyResponse quepyResponse = queryQuepy(QueryType.SPARQL, searchDAO.getQuery());
            response.setDbpediaResponse(queryService(Constants.DBPEDIA, quepyResponse.getQuery()));
            response.setQuestionType(quepyResponse.getRule());

            if (log.isInfoEnabled()) {
                log.info("DBPedia quepy response: " + quepyResponse.toString());
            }

        } catch (Exception e) {
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


    static {
        questionParserMapping.put("whoarechildrenof", "ChildrenOfParser");
        questionParserMapping.put("whois", "PersonParser");
        questionParserMapping.put("bandmembers", "PersonParser");
        questionParserMapping.put("presidentof", "PersonParser");
        questionParserMapping.put("personthattookpartinconflict", "PersonnelInvolvedParser");
        questionParserMapping.put("personthattookpartinconflictnationality", "PersonnelInvolvedParser");
        questionParserMapping.put("personthattookpartinconflictnationalitybornafter", "PersonnelInvolvedParser");
        questionParserMapping.put("personthattookpartinconflictnationalitybornbefore", "PersonnelInvolvedParser");
        questionParserMapping.put("personthattookpartinconflictbornbefore", "PersonnelInvolvedParser");
        questionParserMapping.put("personthattookpartinconflictbornafter", "PersonnelInvolvedParser");
        questionParserMapping.put("conflictthattookplaceincountry", "ConflictParser");
        questionParserMapping.put("conflictthattookplaceincountrybeforeyear", "ConflictParser");
        questionParserMapping.put("conflictthattookplaceincountryafteryear", "ConflictParser");
        questionParserMapping.put("weaponusedbycountryinconflict", "WeaponParser");
        questionParserMapping.put("location", "LocationParser");
        questionParserMapping.put("albumsof", "AlbumParser");
        questionParserMapping.put("albumswrittenbybandafteryear", "AlbumParser");
        questionParserMapping.put("albumsof", "AlbumParser");
        questionParserMapping.put("educationinstitution", "EducationInstitutionParser");
        questionParserMapping.put("songsaboutstuffwrittenbyperson", "SongParser");
        questionParserMapping.put("songsaboutstuff", "SongParser");
        questionParserMapping.put("songsfromalbumbyband", "SongParser");
        questionParserMapping.put("songnamefromalbumbyband", "SongParser");
    }


    public static String getTopSearches() {
        List<Question> topSearches = MongoDBManager.getTopSearches();
        return JsonUtil.pojoToString(topSearches);
    }
}
