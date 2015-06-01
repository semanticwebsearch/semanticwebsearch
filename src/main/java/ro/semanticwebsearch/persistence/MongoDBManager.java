package ro.semanticwebsearch.persistence;

import com.mongodb.MongoClient;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import org.mongodb.morphia.query.Query;
import ro.semanticwebsearch.responsegenerator.model.Answer;
import ro.semanticwebsearch.responsegenerator.model.Question;
import ro.semanticwebsearch.utils.Config;

import java.util.List;

/**
 * Created by Spac on 5/30/2015.
 */
public class MongoDBManager {

    private static MongoClient mongoManager;
    private static String DBNAME;
    private static Morphia morphia;

    static {
        int port = Integer.parseInt(Config.getProperty("mongodb_port"));
        String uri = Config.getProperty("mongodb_endpoint");
        DBNAME = Config.getProperty("mongodb_dbname");
        mongoManager = new MongoClient(uri, port);
        morphia = new Morphia();
        morphia.map(Answer.class);
        morphia.map(Question.class);
    }

    public static void saveAnswer(Answer answer) {
        Datastore ds = getDatastore(morphia);
        ds.save(answer);
    }

    public static void saveAnswerList(List<Answer> answer) {
        Datastore ds = getDatastore(morphia);
        ds.save(answer);
    }

    public static List<Answer> getAnswersForQuestion(String questionId, int offset, int limit) {
        Datastore ds = getDatastore(morphia);
        Query<Answer> query = ds.createQuery(Answer.class)
                .offset(offset)
                .limit(limit);
        query.and(query.criteria("questionId").equal(questionId));

        return query.asList();
    }

    public static List<Question> getQuestionsByBody(String body) {
        Datastore ds = getDatastore(morphia);
        Query<Question> query = ds.createQuery(Question.class);
        query.and(query.criteria("body").equal(body));

        return query.asList();
    }

    private static Datastore getDatastore(Morphia morphia) {
        return morphia.createDatastore(mongoManager, DBNAME);
    }

    public static void saveQuestion(Question question) {
        Datastore ds = getDatastore(morphia);
        ds.save(question);
    }
}
