package ro.semanticwebsearch.api.rest.model;

import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

/**
 * Created by Spac on 6/2/2015.
 */
public class ResultsDAO {

    //region Query params
    @PathParam("questionId")
    private String questionId;

    @QueryParam("offset")
    private int offset;

    @QueryParam("max")
    private int max;
    //endregion

    public String getQuestionId() {
        return questionId;
    }

    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }
}
