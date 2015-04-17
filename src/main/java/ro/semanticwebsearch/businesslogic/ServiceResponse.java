package ro.semanticwebsearch.businesslogic;

/**
 * Created by Spac on 4/15/2015.
 */
public class ServiceResponse {
    private String dbpediaResponse;
    private String freebaseResponse;
    private String questionType;

    public String getDbpediaResponse() {
        return dbpediaResponse;
    }

    public void setDbpediaResponse(String dbpediaResponse) {
        this.dbpediaResponse = dbpediaResponse;
    }

    public String getFreebaseResponse() {
        return freebaseResponse;
    }

    public void setFreebaseResponse(String freebaseResponse) {
        this.freebaseResponse = freebaseResponse;
    }

    public String getQuestionType() {
        return questionType;
    }

    public void setQuestionType(String questionType) {
        this.questionType = questionType;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("ServiceResponse : [ dbpedia: ");
        sb.append(dbpediaResponse).append(";\n freebase : ").append(freebaseResponse)
                .append(";\n question type: ").append(questionType);

        return sb.toString();

    }
}
