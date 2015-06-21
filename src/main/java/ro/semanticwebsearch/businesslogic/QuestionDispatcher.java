package ro.semanticwebsearch.businesslogic;

import ro.semanticwebsearch.api.rest.model.Feedback;
import ro.semanticwebsearch.api.rest.model.ResultsDAO;
import ro.semanticwebsearch.persistence.MongoDBManager;
import ro.semanticwebsearch.responsegenerator.model.Answer;
import ro.semanticwebsearch.responsegenerator.parser.helper.Constants;
import ro.semanticwebsearch.utils.JsonUtil;

import java.util.List;

/**
 * Created by Spac on 6/21/2015.
 */
public class QuestionDispatcher {

    public static int updateDislike(Feedback feedback) {
        return MongoDBManager.dislikeQuestion(feedback.getQuestionId(), feedback.getAnswerId(),
                feedback.getFeedback());
    }

    public static int updateLike(Feedback feedback) {
        return MongoDBManager.likeQuestion(feedback.getQuestionId(), feedback.getAnswerId(),
                feedback.getFeedback());

    }

    public static String getMoreResults(ResultsDAO resultsDAO) {
        List<Answer> answers = MongoDBManager.getAnswersForQuestion(
                resultsDAO.getQuestionId(),
                resultsDAO.getOffset(),
                Math.max(resultsDAO.getMax(), Constants.MAX_CHUNK_SIZE)
        );

        Result results = Dispatcher.toResultObject(answers);

        return JsonUtil.pojoToString(results);
    }
}
