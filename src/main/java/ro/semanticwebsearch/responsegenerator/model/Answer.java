package ro.semanticwebsearch.responsegenerator.model;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Indexed;
import org.mongodb.morphia.utils.IndexDirection;

/**
 * Created by Spac on 5/29/2015.
 */
@Entity
public class Answer {
    @Id
    private ObjectId id = new ObjectId();
    @Indexed(value = IndexDirection.ASC, name = "questionIdIndex")
    private ObjectId questionId;
    private Object body;
    private long ups;
    private long downs;
    private String type;
    private String origin;

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public ObjectId getQuestionId() {
        return questionId;
    }

    public void setQuestionId(ObjectId questionId) {
        this.questionId = questionId;
    }

    public Object getBody() {
        return body;
    }

    public void setBody(Object body) {
        this.body = body;
    }

    public long getUps() {
        return ups;
    }

    public void setUps(long ups) {
        this.ups = ups;
    }

    public long getDowns() {
        return downs;
    }

    public void setDowns(long downs) {
        this.downs = downs;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public static Builder getBuilderForQuestion(ObjectId questionId) {
        Builder builder = new Builder();
        builder.setQuestionId(questionId);
        return builder;
    }

    public static class Builder {
        private ObjectId questionId;
        private ObjectId id = new ObjectId();
        private Object body;
        private long ups = 0;
        private long downs = 0;
        private String type;
        private String origin;

        public ObjectId getQuestionId() {
            return questionId;
        }

        private Builder setQuestionId(ObjectId questionId) {
            this.questionId = questionId;
            return this;
        }

        public ObjectId getId() {
            return id;
        }

        public Builder setId(ObjectId id) {
            this.id = id;
            return this;
        }

        public Object getBody() {
            return body;
        }

        public Builder setBody(Object body) {
            this.body = body;
            return this;
        }

        public long getUps() {
            return ups;
        }

        public Builder setUps(long ups) {
            this.ups = ups;
            return this;
        }

        public long getDowns() {
            return downs;
        }

        public Builder setDowns(long downs) {
            this.downs = downs;
            return this;
        }

        public String getType() {
            return type;
        }

        public Builder setType(String type) {
            this.type = type;
            return this;
        }

        public String getOrigin() {
            return origin;
        }

        public Builder setOrigin(String origin) {
            this.origin = origin;
            return this;
        }

        public Answer build() {
            Answer answer = new Answer();
            answer.setType(this.type);
            answer.setBody(this.body);
            answer.setId(this.id);
            answer.setDowns(this.downs);
            answer.setUps(this.ups);
            answer.setOrigin(this.origin);
            answer.setQuestionId(this.questionId);
            return answer;
        }
    }
}
