package ro.semanticwebsearch.api.rest.model;

import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

/**
 * Created by Spac on 6/20/2015.
 */
public class Feedback {
    //region Query params
    @PathParam("questionId")
    private String questionId;

    @PathParam("answerId")
    private String answerId;

    @PathParam("feedback")
    private int feedback;

    public String getQuestionId() {
        return questionId;
    }

    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }

    public int getFeedback() {
        return feedback;
    }

    public void setFeedback(int feedback) {
        this.feedback = feedback;
    }

    public String getAnswerId() {
        return answerId;
    }

    public void setAnswerId(String answerId) {
        this.answerId = answerId;
    }
}
