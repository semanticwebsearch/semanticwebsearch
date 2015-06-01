package ro.semanticwebsearch.responsegenerator.model;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Indexed;
import org.mongodb.morphia.utils.IndexDirection;

/**
 * Created by Spac on 5/31/2015.
 */
@Entity
public class Question {
    @Id
    private String id = new ObjectId().toString();
    private String type;

    @Indexed(value = IndexDirection.ASC, name = "bodyIndex", unique = true)
    private String body;
    private long numberOfAccesses;

    public String getId() {
        return id;
    }

    private void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public long getNumberOfAccesses() {
        return numberOfAccesses;
    }

    public void setNumberOfAccesses(long numberOfAccesses) {
        this.numberOfAccesses = numberOfAccesses;
    }
}
